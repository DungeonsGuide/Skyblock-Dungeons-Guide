/*
 *     Dungeons Guide - The most intelligent Hypixel Skyblock Dungeons Mod
 *     Copyright (C) 2021  cyoung06
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package kr.syeyoung.dungeonsguide.features.impl.party.api;

import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import kr.syeyoung.dungeonsguide.utils.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import scala.tools.cmd.Opt;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.*;

public class ApiFetchur {
    private static final Gson gson = new Gson();

    private static final Map<String, CachedData<PlayerProfile>> playerProfileCache = new ConcurrentHashMap<>();
    private static final Map<String, CachedData<String>> nicknameToUID = new ConcurrentHashMap<>();
    private static final Map<String, CachedData<String>> UIDtoNickname = new ConcurrentHashMap<>();
    private static final Map<String, CachedData<GameProfile>> UIDtoGameProfile = new ConcurrentHashMap<>();

    private static final ExecutorService ex = Executors.newFixedThreadPool(4);

    private static final Set<String> invalidKeys = new HashSet<>();
    public static void purgeCache() {
        playerProfileCache.clear();
        nicknameToUID.clear();
        UIDtoNickname.clear();
        UIDtoGameProfile.clear();

        completableFutureMap.clear();
        completableFutureMap2.clear();
        completableFutureMap3.clear();
        completableFutureMap4.clear();
        invalidKeys.clear();
    }

    public static JsonObject getJson(String url) throws IOException {
        URLConnection connection = new URL(url).openConnection();
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        return gson.fromJson(new InputStreamReader(connection.getInputStream()), JsonObject.class);
    }
    public static JsonArray getJsonArr(String url) throws IOException {
        URLConnection connection = new URL(url).openConnection();
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        return gson.fromJson(new InputStreamReader(connection.getInputStream()), JsonArray.class);
    }

    private static final Map<String, CompletableFuture<Optional<GameProfile>>> completableFutureMap4 = new ConcurrentHashMap<>();
    public static CompletableFuture<Optional<GameProfile>> getSkinGameProfileByUUIDAsync(String uid) {
        if (UIDtoGameProfile.containsKey(uid)) {
            CachedData<GameProfile> cachedData = UIDtoGameProfile.get(uid);
            if (cachedData.getExpire() > System.currentTimeMillis()) {
                return CompletableFuture.completedFuture(Optional.ofNullable(cachedData.getData()));
            }
            UIDtoGameProfile.remove(uid);
        }
        if (completableFutureMap4.containsKey(uid)) return completableFutureMap4.get(uid);

        CompletableFuture<Optional<GameProfile>> completableFuture = new CompletableFuture<>();
        fetchNicknameAsync(uid).thenAccept(nick -> {
            if (!nick.isPresent()) {
                completableFuture.complete(Optional.empty());
                return;
            }
            ex.submit(() -> {
                try {
                    Optional<GameProfile> playerProfile = getSkinGameProfileByUUID(uid,nick.get());
                    UIDtoGameProfile.put(uid, new CachedData<GameProfile>(System.currentTimeMillis()+1000*60*30, playerProfile.orElse(null)));
                    completableFuture.complete(playerProfile);
                    completableFutureMap4.remove(uid);
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                completableFuture.complete(Optional.empty());
                completableFutureMap4.remove(uid);
            });
        });
        completableFutureMap4.put(uid, completableFuture);
        return completableFuture;
    }

    public static Optional<GameProfile> getSkinGameProfileByUUID(String uid, String nickname) throws IOException {
        GameProfile gameProfile = new GameProfile(UUID.fromString(uid), nickname);
        GameProfile newProf = Minecraft.getMinecraft().getSessionService().fillProfileProperties(gameProfile, true);
        return newProf == gameProfile ? Optional.empty() : Optional.of(newProf);
    }


    private static final Map<String, CompletableFuture<Optional<PlayerProfile>>> completableFutureMap = new ConcurrentHashMap<>();
    public static CompletableFuture<Optional<PlayerProfile>> fetchMostRecentProfileAsync(String uid, String apiKey) {
        if (playerProfileCache.containsKey(uid)) {
            CachedData<PlayerProfile> cachedData = playerProfileCache.get(uid);
            if (cachedData.getExpire() > System.currentTimeMillis()) {
                return CompletableFuture.completedFuture(Optional.ofNullable(cachedData.getData()));
            }
            playerProfileCache.remove(uid);
        }
        if (completableFutureMap.containsKey(uid)) return completableFutureMap.get(uid);
        if (invalidKeys.contains(apiKey)) {
            CompletableFuture cf = new CompletableFuture();
            cf.completeExceptionally(new IOException("403 for url"));
            return cf;
        }

        CompletableFuture<Optional<PlayerProfile>> completableFuture = new CompletableFuture<>();
        ex.submit(() -> {
            try {
                Optional<PlayerProfile> playerProfile = fetchMostRecentProfile(uid, apiKey);
                playerProfileCache.put(uid, new CachedData<PlayerProfile>(System.currentTimeMillis()+1000*60*30, playerProfile.orElse(null)));
                completableFuture.complete(playerProfile);
                completableFutureMap.remove(uid);
                return;
            } catch (IOException e) {
                if (e.getMessage().contains("403 for URL")) {
                    completableFuture.completeExceptionally(e);
                    completableFutureMap.remove(uid);
                    invalidKeys.add(apiKey);
                } else {
                    completableFuture.complete(Optional.empty());
                    completableFutureMap.remove(uid);
                }
                e.printStackTrace();
            }
        });
        completableFutureMap.put(uid, completableFuture);
        return completableFuture;
    }

    private static final Map<String, CompletableFuture<Optional<String>>> completableFutureMap3 = new ConcurrentHashMap<>();
    public static CompletableFuture<Optional<String>> fetchNicknameAsync(String uid) {
        if (UIDtoNickname.containsKey(uid)) {
            CachedData<String> cachedData = UIDtoNickname.get(uid);
            if (cachedData.getExpire() > System.currentTimeMillis()) {
                return CompletableFuture.completedFuture(Optional.ofNullable(cachedData.getData()));
            }
            UIDtoNickname.remove(uid);
        }
        if (completableFutureMap3.containsKey(uid)) return completableFutureMap3.get(uid);


        CompletableFuture<Optional<String>> completableFuture = new CompletableFuture<>();

        ex.submit(() -> {
            try {
                Optional<String> playerProfile = fetchNickname(uid);
                UIDtoNickname.put(uid, new CachedData<String>(System.currentTimeMillis()+1000*60*60*12,playerProfile.orElse(null)));
                if (playerProfile.isPresent())
                    nicknameToUID.put(playerProfile.orElse(null), new CachedData<>(System.currentTimeMillis()+1000*60*60*12, uid));
                completableFuture.complete(playerProfile);
                completableFutureMap3.remove(uid);
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
            completableFuture.complete(Optional.empty());
            completableFutureMap3.remove(uid);
        });
        completableFutureMap3.put(uid, completableFuture);

        return completableFuture;
    }

    private static final Map<String, CompletableFuture<Optional<String>>> completableFutureMap2 = new ConcurrentHashMap<>();
    public static CompletableFuture<Optional<String>> fetchUUIDAsync(String nickname) {
        if (nicknameToUID.containsKey(nickname)) {
            CachedData<String> cachedData = nicknameToUID.get(nickname);
            if (cachedData.getExpire() > System.currentTimeMillis()) {
                return CompletableFuture.completedFuture(Optional.ofNullable(cachedData.getData()));
            }
            nicknameToUID.remove(nickname);
        }
        if (completableFutureMap2.containsKey(nickname)) return completableFutureMap2.get(nickname);


        CompletableFuture<Optional<String>> completableFuture = new CompletableFuture<>();

        ex.submit(() -> {
            try {
                Optional<String> playerProfile = fetchUUID(nickname);
                nicknameToUID.put(nickname, new CachedData<String>(System.currentTimeMillis()+1000*60*60*12,playerProfile.orElse(null)));
                if (playerProfile.isPresent())
                    UIDtoNickname.put(playerProfile.orElse(null), new CachedData<>(System.currentTimeMillis()+1000*60*60*12, nickname));

                completableFuture.complete(playerProfile);
                completableFutureMap2.remove(nickname);
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
            completableFuture.complete(Optional.empty());
            completableFutureMap2.remove(nickname);
        });
        completableFutureMap2.put(nickname, completableFuture);

        return completableFuture;
    }

    public static Optional<String> fetchUUID(String nickname) throws IOException {
        JsonObject json = getJson("https://api.mojang.com/users/profiles/minecraft/"+nickname);
        if (json.has("error")) return Optional.empty();
        return Optional.of(TextUtils.insertDashUUID(json.get("id").getAsString()));
    }
    public static Optional<String> fetchNickname(String uuid) throws IOException {
        try {
            JsonArray json = getJsonArr("https://api.mojang.com/user/profiles/" + uuid.replace("-", "") + "/names");
            return Optional.of(json.get(json.size()-1).getAsJsonObject().get("name").getAsString());
        } catch (Exception e) {return Optional.empty();}
    }

    public static List<PlayerProfile> fetchPlayerProfiles(String uid, String apiKey) throws IOException {
        JsonObject json = getJson("https://api.hypixel.net/skyblock/profiles?uuid="+uid+"&key="+apiKey);
        if (!json.get("success").getAsBoolean()) return new ArrayList<>();
        JsonArray profiles = json.getAsJsonArray("profiles");
        String dashTrimmed = uid.replace("-", "");

        ArrayList<PlayerProfile> playerProfiles = new ArrayList<>();
        for (JsonElement jsonElement : profiles) {
            JsonObject semiProfile = jsonElement.getAsJsonObject();
            if (!semiProfile.has(dashTrimmed)) continue;
            playerProfiles.add(parseProfile(semiProfile, dashTrimmed));
        }
        return playerProfiles;
    }

    public static Optional<PlayerProfile> fetchMostRecentProfile(String uid, String apiKey) throws IOException {
        JsonObject json = getJson("https://api.hypixel.net/skyblock/profiles?uuid="+uid+"&key="+apiKey);
        if (!json.get("success").getAsBoolean()) return Optional.empty();
        JsonArray profiles = json.getAsJsonArray("profiles");
        String dashTrimmed = uid.replace("-", "");

        JsonObject profile = null;
        long lastSave = Long.MIN_VALUE;
        for (JsonElement jsonElement : profiles) {
            JsonObject semiProfile = jsonElement.getAsJsonObject();
            if (!semiProfile.getAsJsonObject("members").has(dashTrimmed)) continue;
            long lastSave2 = semiProfile.getAsJsonObject("members").getAsJsonObject(dashTrimmed).get("last_save").getAsLong();
            if (lastSave2 > lastSave) {
                profile = semiProfile;
                lastSave = lastSave2;
            }
        }

        if (profile == null) return Optional.empty();
        PlayerProfile pp = parseProfile(profile, dashTrimmed);
        json = getJson("https://api.hypixel.net/player?uuid="+uid+"&key="+apiKey);
        if (json.has("player")) {
            JsonObject treasures = json.getAsJsonObject("player");
            if (treasures.has("achievements")) {
                treasures = treasures.getAsJsonObject("achievements");
                if (treasures.has("skyblock_treasure_hunter")) {
                    pp.setTotalSecrets(treasures.get("skyblock_treasure_hunter").getAsInt());
                }
            }
        }

        return Optional.of(pp);
    }

    public static int getOrDefault(JsonObject jsonObject, String key, int value) {
        if (jsonObject == null || !jsonObject.has(key) || jsonObject.get(key) instanceof JsonNull) return value;
        return jsonObject.get(key).getAsInt();
    }
    public static long getOrDefault(JsonObject jsonObject, String key, long value) {
        if (jsonObject == null || !jsonObject.has(key) || jsonObject.get(key) instanceof JsonNull) return value;
        return jsonObject.get(key).getAsLong();
    }
    public static double getOrDefault(JsonObject jsonObject, String key, double value) {
        if (jsonObject == null || !jsonObject.has(key) || jsonObject.get(key) instanceof JsonNull) return value;
        return jsonObject.get(key).getAsDouble();
    }
    public static String getOrDefault(JsonObject jsonObject, String key, String value) {
        if (jsonObject == null || !jsonObject.has(key) || jsonObject.get(key) instanceof JsonNull) return value;
        return jsonObject.get(key).getAsString();
    }
    public static NBTTagCompound parseBase64NBT(String nbt) throws IOException {
        return CompressedStreamTools.readCompressed(new ByteArrayInputStream(Base64.getDecoder().decode(nbt)));
    }

    public static ItemStack deserializeNBT(NBTTagCompound nbtTagCompound) {
        if (nbtTagCompound.hasNoTags()) return null;
        ItemStack itemStack = new ItemStack(Blocks.stone);
        itemStack.deserializeNBT(nbtTagCompound);
        return itemStack;
    }

    public static PlayerProfile parseProfile(JsonObject profile, String dashTrimmed) throws IOException {
        PlayerProfile playerProfile = new PlayerProfile();
        playerProfile.setProfileUID(getOrDefault(profile, "profile_id", ""));
        playerProfile.setMemberUID(dashTrimmed);
        playerProfile.setProfileName(getOrDefault(profile, "cute_name", ""));

        JsonObject playerData = profile.getAsJsonObject("members").getAsJsonObject(dashTrimmed);
        playerProfile.setLastSave(getOrDefault(playerData, "last_save", 0L));
        playerProfile.setFairySouls(getOrDefault(playerData, "fairy_souls_collected", 0));
        playerProfile.setFairyExchanges(getOrDefault(playerData,  "fairy_exchanges", 0));

        if (playerData.has("inv_armor")) {
            playerProfile.setCurrentArmor(new PlayerProfile.Armor());
            NBTTagCompound armor = parseBase64NBT(playerData.getAsJsonObject("inv_armor")
                    .get("data")
                    .getAsString());
            NBTTagList array = armor.getTagList("i", 10);
            for (int i = 0; i < 4; i++) {
                NBTTagCompound item = array.getCompoundTagAt(i);
                playerProfile.getCurrentArmor().getArmorSlots()[i] = deserializeNBT(item);
            }
        }

        if (playerData.has("wardrobe_contents")) {
            NBTTagCompound armor = parseBase64NBT(playerData.getAsJsonObject("wardrobe_contents").get("data").getAsString());
            NBTTagList array = armor.getTagList("i", 10);
            for (int i = 0; i < array.tagCount(); i++) {
                if (i % 4 == 0) playerProfile.getWardrobe().add(new PlayerProfile.Armor());
                NBTTagCompound item = array.getCompoundTagAt(i);
                playerProfile.getWardrobe().get(i/4).getArmorSlots()[i%4] = deserializeNBT(item);
            }

        }
        playerProfile.setSelectedWardrobe(getOrDefault(playerData, "wardrobe_equipped_slot", -1));

        if (playerData.has("inv_contents")) {
            NBTTagCompound armor = parseBase64NBT(playerData.getAsJsonObject("inv_contents").get("data").getAsString());
            NBTTagList array = armor.getTagList("i", 10);
            playerProfile.setInventory(new ItemStack[array.tagCount()]);
            for (int i = 0; i < array.tagCount(); i++) {
                NBTTagCompound item = array.getCompoundTagAt(i);
                playerProfile.getInventory()[i] = deserializeNBT(item);
            }
        }
        if (playerData.has("ender_chest_contents")) {
            NBTTagCompound armor = parseBase64NBT(playerData.getAsJsonObject("ender_chest_contents").get("data").getAsString());
            NBTTagList array = armor.getTagList("i", 10);
            playerProfile.setEnderchest(new ItemStack[array.tagCount()]);
            for (int i = 0; i < array.tagCount(); i++) {
                NBTTagCompound item = array.getCompoundTagAt(i);
                playerProfile.getEnderchest()[i] = deserializeNBT(item);
            }
        }
        if (playerData.has("talisman_bag")) {
            NBTTagCompound armor = parseBase64NBT(playerData.getAsJsonObject("talisman_bag").get("data").getAsString());
            NBTTagList array = armor.getTagList("i", 10);
            playerProfile.setTalismans(new ItemStack[array.tagCount()]);
            for (int i = 0; i < array.tagCount(); i++) {
                NBTTagCompound item = array.getCompoundTagAt(i);
                playerProfile.getTalismans()[i] = deserializeNBT(item);
            }
        }

        for (Skill value : Skill.values()) {
            playerProfile.getSkillXp().put(value, getOrDefault(playerData, "experience_skill_"+value.getJsonName(), 0.0));
        }

        if (playerData.has("pets")) {
            for (JsonElement pets : playerData.getAsJsonArray("pets")) {
                JsonObject pet = pets.getAsJsonObject();
                Pet petObj = new Pet();
                petObj.setActive(pet.get("active").getAsBoolean());
                petObj.setExp(getOrDefault(pet, "exp", 0.0));
                petObj.setHeldItem(getOrDefault(pet, "heldItem", null));
                petObj.setSkin(getOrDefault(pet, "skin", null));
                petObj.setType(getOrDefault(pet, "type", null));
                petObj.setUuid(getOrDefault(pet, "uuid", null));

                playerProfile.getPets().add(petObj);
            }
        }

        if (playerData.has("dungeons") && playerData.getAsJsonObject("dungeons").has("dungeon_types")) {
            JsonObject types = playerData.getAsJsonObject("dungeons")
                    .getAsJsonObject("dungeon_types");
            for (DungeonType value : DungeonType.values()) {
                DungeonStat dungeonStat = new DungeonStat();
                DungeonSpecificData<DungeonStat> dungeonSpecificData = new DungeonSpecificData<>(value, dungeonStat);
                playerProfile.getDungeonStats().put(value, dungeonSpecificData);

                if (!types.has(value.getJsonName())) continue;

                JsonObject dungeonObj = types.getAsJsonObject(value.getJsonName());

                dungeonStat.setHighestCompleted(getOrDefault(dungeonObj, "highest_tier_completed", -1));

                for (Integer validFloor : value.getValidFloors()) {
                    DungeonStat.PlayedFloor playedFloor = new DungeonStat.PlayedFloor();
                    playedFloor.setBestScore(getOrDefault(dungeonObj.getAsJsonObject("best_score"), ""+validFloor, 0));
                    playedFloor.setCompletions(getOrDefault(dungeonObj.getAsJsonObject("tier_completions"), ""+validFloor, 0));
                    playedFloor.setFastestTime(getOrDefault(dungeonObj.getAsJsonObject("fastest_time"), ""+validFloor, -1));
                    playedFloor.setFastestTimeS(getOrDefault(dungeonObj.getAsJsonObject("fastest_time_s"), ""+validFloor, -1));
                    playedFloor.setFastestTimeSPlus(getOrDefault(dungeonObj.getAsJsonObject("fastest_time_s_plus"), ""+validFloor, -1));
                    playedFloor.setMobsKilled(getOrDefault(dungeonObj.getAsJsonObject("mobs_killed"), ""+validFloor, 0));
                    playedFloor.setMostMobsKilled(getOrDefault(dungeonObj.getAsJsonObject("most_mobs_killed"), ""+validFloor, 0));
                    playedFloor.setMostHealing(getOrDefault(dungeonObj.getAsJsonObject("most_healing"), ""+validFloor, 0));
                    playedFloor.setTimes_played(getOrDefault(dungeonObj.getAsJsonObject("times_played"), ""+validFloor, 0));
                    playedFloor.setWatcherKills(getOrDefault(dungeonObj.getAsJsonObject("watcher_kills"), ""+validFloor, 0));

                    for (DungeonClass dungeonClass : DungeonClass.values()) {
                        DungeonStat.PlayedFloor.ClassStatistics classStatistics = new DungeonStat.PlayedFloor.ClassStatistics();
                        classStatistics.setMostDamage(getOrDefault(dungeonObj.getAsJsonObject("most_damage_"+dungeonClass.getJsonName()), ""+validFloor, 0));
                        ClassSpecificData<DungeonStat.PlayedFloor.ClassStatistics> classStatisticsClassSpecificData = new ClassSpecificData<>(dungeonClass, classStatistics);

                        playedFloor.getClassStatistics().put(dungeonClass, classStatisticsClassSpecificData);
                    }

                    FloorSpecificData<DungeonStat.PlayedFloor> playedFloorFloorSpecificData = new FloorSpecificData<>(validFloor, playedFloor);
                    dungeonStat.getPlays().put(validFloor, playedFloorFloorSpecificData);
                }

                dungeonStat.setExperience(getOrDefault(dungeonObj, "experience", 0));


            }
        }
        if (playerData.has("dungeons") && playerData.getAsJsonObject("dungeons").has("player_classes")) {
            JsonObject classes = playerData.getAsJsonObject("dungeons")
                    .getAsJsonObject("player_classes");
            for (DungeonClass dungeonClass : DungeonClass.values()) {
                PlayerProfile.PlayerClassData classStatistics = new PlayerProfile.PlayerClassData();
                classStatistics.setExperience(getOrDefault(classes.getAsJsonObject(dungeonClass.getJsonName()), "experience", 0));
                ClassSpecificData<PlayerProfile.PlayerClassData> classStatisticsClassSpecificData = new ClassSpecificData<>(dungeonClass, classStatistics);

                playerProfile.getPlayerClassData().put(dungeonClass, classStatisticsClassSpecificData);
            }
        }
        if (playerData.has("dungeons")) {
            String id = getOrDefault(playerData.getAsJsonObject("dungeons"), "selected_dungeon_class", null);
            DungeonClass dungeonClass = DungeonClass.getClassByJsonName(id);
            playerProfile.setSelectedClass(dungeonClass);
        }

        return playerProfile;
    }
}
