package kr.syeyoung.dungeonsguide.eventlistener;

import kr.syeyoung.dungeonsguide.config.Config;
import kr.syeyoung.dungeonsguide.e;
import kr.syeyoung.dungeonsguide.utils.AhUtils;
import kr.syeyoung.dungeonsguide.utils.TextUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;

public class ItemGuiListener {
    @SubscribeEvent
    public void dungeonTooltip(ItemTooltipEvent event) {
        if (!e.getDungeonsGuide().getSkyblockStatus().isOnSkyblock()) return;
        if (!Config.itemtooltip_dungeonstat) return;

        ItemStack hoveredItem = event.itemStack;
        NBTTagCompound compound = hoveredItem.getTagCompound();
        if (compound == null)
            return;
        if (!compound.hasKey("ExtraAttributes"))
            return;
        NBTTagCompound nbtTagCompound = compound.getCompoundTag("ExtraAttributes");

        int floor = nbtTagCompound.getInteger("item_tier");
        int percentage = nbtTagCompound.getInteger("baseStatBoostPercentage");

        if (nbtTagCompound.hasKey("item_tier"))
            event.toolTip.add("§7Obtained in: §c"+(floor == 0 ? "Entrance" : "Floor "+floor));
        if (nbtTagCompound.hasKey("baseStatBoostPercentage"))
            event.toolTip.add("§7Stat Percentage: §"+(percentage == 50 ? "6§l":"c")+(percentage * 2)+"%");
    }
    @SubscribeEvent
    public void priceTooltip(ItemTooltipEvent event) {
        if (!e.getDungeonsGuide().getSkyblockStatus().isOnSkyblock()) return;
        if (!Config.itemtooltip_price) return;

        ItemStack hoveredItem = event.itemStack;
        NBTTagCompound compound = hoveredItem.getTagCompound();
        if (compound == null)
            return;
        if (!compound.hasKey("ExtraAttributes"))
            return;
        final String id = compound.getCompoundTag("ExtraAttributes").getString("id");
        if (id.equals("ENCHANTED_BOOK")) {
            final NBTTagCompound enchants = compound.getCompoundTag("ExtraAttributes").getCompoundTag("enchantments");
            Set<String> keys = enchants.getKeySet();
            Set<String> actualKeys = new TreeSet<String>(new Comparator<String>() {
                public int compare(String o1, String o2) {
                    String id2 = id + "::" + o1 + "-" + enchants.getInteger(o1);
                    AhUtils.AuctionData auctionData = AhUtils.auctions.get(id2);
                    int price1 = (auctionData == null) ? 0 : ((Integer)auctionData.prices.first()).intValue();
                    String id3 = id + "::" + o2 + "-" + enchants.getInteger(o2);
                    AhUtils.AuctionData auctionData2 = AhUtils.auctions.get(id3);
                    int price2 = (auctionData2 == null) ? 0 : ((Integer)auctionData2.prices.first()).intValue();
                    return (compare2(price1, price2) == 0) ? o1.compareTo(o2) : compare2(price1, price2);
                }

                public int compare2(int y, int x) {
                    return (x < y) ? -1 : ((x == y) ? 0 : 1);
                }
            });
            actualKeys.addAll(keys);
            int totalLowestPrice = 0;
            int totalHighestPrice = 0;
            int iterations = 0;
            for (String key : actualKeys) {
                iterations++;
                String id2 = id + "::" + key + "-" + enchants.getInteger(key);
                AhUtils.AuctionData auctionData = AhUtils.auctions.get(id2);
                if (auctionData == null) {
                    if (iterations < 10)
                        event.toolTip.add("§f"+ key + " " + enchants.getInteger(key) + "§7: §cn/a");
                    continue;
                }
                if (iterations < 10)
                    event.toolTip.add("§f"+ key + " " + enchants.getInteger(key) + "§7: §e"+ TextUtils.format((Integer) auctionData.prices.first()) + " §7to§e "+ TextUtils.format(auctionData.prices.last()));
                            totalLowestPrice += auctionData.prices.first();
                totalHighestPrice += auctionData.prices.last();
            }
            if (iterations >= 10)
                event.toolTip.add("§7"+ (iterations - 10) + " more enchants... ");
            event.toolTip.add("§fTotal§7 §e:"+ TextUtils.format(totalLowestPrice) + " §7to§e "+ TextUtils.format(totalHighestPrice));
        } else {
            AhUtils.AuctionData auctionData = AhUtils.auctions.get(id);
            event.toolTip.add("");
            if (auctionData == null) {
                event.toolTip.add("§fLowest ah §7: §cn/a");
                event.toolTip.add("§fHighest ah §7: §cn/a");
                event.toolTip.add("§fBazaar sell price §7: §cn/a");
                event.toolTip.add("§fBazaar buy price §7: §cn/a");
            } else {
                event.toolTip.add("§fLowest ah §7: " + ((auctionData.prices.size() != 0) ? ("§e"+ TextUtils.format(auctionData.prices.first().intValue())) : "§cn/a"));
                event.toolTip.add("§fHighest ah §7: " + ((auctionData.prices.size() != 0) ? ("§e"+ TextUtils.format((auctionData.prices.last()).intValue())) : "§cn/a"));
                event.toolTip.add("§fBazaar sell price §7: " + ((auctionData.sellPrice == -1) ? "§cn/a": ("§e"+ TextUtils.format(auctionData.sellPrice))));
                event.toolTip.add("§fBazaar buy price §7: " + ((auctionData.buyPrice == -1) ? "§cn/a": ("§e"+ TextUtils.format(auctionData.buyPrice))));
            }
        }
    }
}