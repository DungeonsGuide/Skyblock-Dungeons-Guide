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

package kr.syeyoung.dungeonsguide.features.impl.dungeon;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import kr.syeyoung.dungeonsguide.SkyblockStatus;
import kr.syeyoung.dungeonsguide.config.types.AColor;
import kr.syeyoung.dungeonsguide.dungeon.DungeonContext;
import kr.syeyoung.dungeonsguide.dungeon.MapProcessor;
import kr.syeyoung.dungeonsguide.dungeon.roomfinder.DungeonRoom;
import kr.syeyoung.dungeonsguide.DungeonsGuide;
import kr.syeyoung.dungeonsguide.features.FeatureParameter;
import kr.syeyoung.dungeonsguide.features.GuiFeature;
import kr.syeyoung.dungeonsguide.features.listener.*;
import kr.syeyoung.dungeonsguide.utils.RenderUtils;
import kr.syeyoung.dungeonsguide.utils.TextUtils;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec4b;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import java.awt.*;
import java.util.List;
import java.util.Comparator;

public class FeatureDungeonMap extends GuiFeature implements DungeonEndListener, DungeonStartListener, BossroomEnterListener {
    public FeatureDungeonMap() {
        super("Dungeon", "Dungeon Map", "Display dungeon map!", "dungeon.map", true, 128,128);
        this.setEnabled(false);
        parameters.put("scale", new FeatureParameter<Boolean>("scale", "Scale map", "Whether to scale map to fit screen", true, "boolean"));
        parameters.put("playerCenter", new FeatureParameter<Boolean>("playerCenter", "Center map at player", "Render you in the center", false, "boolean"));
        parameters.put("rotate", new FeatureParameter<Boolean>("rotate", "Rotate map centered at player", "Only works with Center map at player enabled", false, "boolean"));
        parameters.put("postScale", new FeatureParameter<Float>("postScale", "Scale factor of map", "Only works with Center map at player enabled", 1.0f, "float"));
        parameters.put("useplayerheads", new FeatureParameter<Boolean>("useplayerheads", "Use player heads instead of arrows", "Option to use player heads instead of arrows", true, "boolean"));
        parameters.put("showotherplayers", new FeatureParameter<Boolean>("showotherplayers", "Show other players", "Option to show other players in map", true, "boolean"));
        parameters.put("showtotalsecrets", new FeatureParameter<Boolean>("showtotalsecrets", "Show Total secrets in the room", "Option to overlay total secrets in the specific room", true, "boolean"));
        parameters.put("playerheadscale", new FeatureParameter<Float>("playerheadscale", "Player head scale", "Scale factor of player heads, defaults to 1", 1.0f, "float"));
        parameters.put("textScale", new FeatureParameter<Float>("textScale", "Text scale", "Scale factor of texts on map, defaults to 1", 1.0f, "float"));

        parameters.put("border_color", new FeatureParameter<AColor>("border_color", "Color of the border", "Same as name", new AColor(255,255,255,255), "acolor"));
        parameters.put("background_color", new FeatureParameter<AColor>("background_color", "Color of the background", "Same as name", new AColor(0x22000000, true), "acolor"));
        parameters.put("player_color", new FeatureParameter<AColor>("player_color", "Color of the player border", "Same as name", new AColor(255,255,255,0), "acolor"));
    }

    SkyblockStatus skyblockStatus = DungeonsGuide.getDungeonsGuide().getSkyblockStatus();
    public static final Ordering<NetworkPlayerInfo> field_175252_a = Ordering.from(new PlayerComparator());

    private boolean on = false;

    @Override
    public void onDungeonEnd() {
        on = false;
    }

    @Override
    public void onDungeonStart() {
        on = true;
    }

    @Override
    public void onBossroomEnter() {
        on = false;
    }

    @SideOnly(Side.CLIENT)
    static class PlayerComparator implements Comparator<NetworkPlayerInfo>
    {
        private PlayerComparator()
        {
        }

        public int compare(NetworkPlayerInfo p_compare_1_, NetworkPlayerInfo p_compare_2_)
        {
            ScorePlayerTeam scoreplayerteam = p_compare_1_.getPlayerTeam();
            ScorePlayerTeam scoreplayerteam1 = p_compare_2_.getPlayerTeam();
            return ComparisonChain.start().compareTrueFirst(p_compare_1_.getGameType() != WorldSettings.GameType.SPECTATOR, p_compare_2_.getGameType() != WorldSettings.GameType.SPECTATOR).compare(scoreplayerteam != null ? scoreplayerteam.getRegisteredName() : "", scoreplayerteam1 != null ? scoreplayerteam1.getRegisteredName() : "").compare(p_compare_1_.getGameProfile().getName(), p_compare_2_.getGameProfile().getName()).result();
        }
    }
    @Override
    public void drawHUD(float partialTicks) {
        if (!skyblockStatus.isOnDungeon()) return;
        if (skyblockStatus.getContext() == null || !skyblockStatus.getContext().getMapProcessor().isInitialized()) return;
        if (!on) return;

        DungeonContext context = skyblockStatus.getContext();
        MapProcessor mapProcessor = context.getMapProcessor();
        MapData mapData = mapProcessor.getLastMapData2();
        Rectangle featureRect =getFeatureRect().getRectangle();
        Gui.drawRect(0,0,featureRect.width, featureRect.height, RenderUtils.getColorAt(featureRect.x, featureRect.y, this.<AColor>getParameter("background_color").getValue()));
        GlStateManager.color(1,1,1,1);
        GlStateManager.pushMatrix();
        if (mapData == null) {
            Gui.drawRect(0,0,featureRect.width, featureRect.height, 0xFFFF0000);
        } else {
            renderMap(partialTicks,mapProcessor,mapData,context);
        }
        GlStateManager.popMatrix();
        GL11.glLineWidth(2);
        RenderUtils.drawUnfilledBox(0,0,featureRect.width, featureRect.height, this.<AColor>getParameter("border_color").getValue());
    }

    @Override
    public void drawDemo(float partialTicks) {
        if (skyblockStatus.isOnDungeon() && skyblockStatus.getContext() != null && skyblockStatus.getContext().getMapProcessor().isInitialized() && on) {
            drawHUD(partialTicks);
            return;
        }
        Rectangle featureRect =getFeatureRect().getRectangle();
        Gui.drawRect(0,0,featureRect.width, featureRect.height, RenderUtils.getColorAt(featureRect.x, featureRect.y, this.<AColor>getParameter("background_color").getValue()));
        FontRenderer fr = getFontRenderer();

        GlStateManager.enableBlend();
        GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        fr.drawString("Please join a dungeon to see preview", featureRect.width / 2 - fr.getStringWidth("Please join a dungeon to see preview") / 2, featureRect.height / 2 - fr.FONT_HEIGHT / 2, 0xFFFFFFFF);
        GL11.glLineWidth(2);
        RenderUtils.drawUnfilledBox(0,0,featureRect.width, featureRect.height, this.<AColor>getParameter("border_color").getValue());
    }

    public void renderMap(float partialTicks, MapProcessor mapProcessor, MapData mapData, DungeonContext context){
        float postScale = this.<Boolean>getParameter("playerCenter").getValue() ? this.<Float>getParameter("postScale").getValue() : 1;
        Rectangle featureRect =getFeatureRect().getRectangle();
        int width = featureRect.width;
        float scale = (this.<Boolean>getParameter("scale").getValue() ? width / 128.0f : 1);
        GlStateManager.translate(width / 2, width / 2, 0);
        GlStateManager.scale(scale, scale, 0);
        GlStateManager.scale(postScale, postScale,0);
        EntityPlayer p = Minecraft.getMinecraft().thePlayer;
        Point pt = mapProcessor.worldPointToMapPoint(p.getPositionEyes(partialTicks));
        double yaw = p.prevRotationYawHead + (p.rotationYaw - p.prevRotationYawHead) * partialTicks;
        if (this.<Boolean>getParameter("playerCenter").getValue()) {
            if (this.<Boolean>getParameter("rotate").getValue()) {
                GlStateManager.rotate((float) (180.0 - yaw), 0,0,1);
            }
            GlStateManager.translate( -pt.x, -pt.y, 0);
        } else {
            GlStateManager.translate( -64, -64, 0);
        }
        updateMapTexture(mapData.colors, mapProcessor, context.getDungeonRoomList());
        render(mapData, false);


        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(1, 771, 0, 1);

        if (this.<Boolean>getParameter("useplayerheads").getValue()) {
            renderHeads(mapProcessor, mapData, scale, postScale, partialTicks);
        } else {
            renderArrows(mapProcessor, mapData, scale, postScale, partialTicks);
        }


        FontRenderer fr = getFontRenderer();
        if (this.<Boolean>getParameter("showtotalsecrets").getValue()) {
            for (DungeonRoom dungeonRoom : context.getDungeonRoomList()) {
                GlStateManager.pushMatrix();
                GlStateManager.pushAttrib();
                Point mapPt = mapProcessor.roomPointToMapPoint(dungeonRoom.getUnitPoints().get(0));
                GlStateManager.translate(mapPt.x + mapProcessor.getUnitRoomDimension().width / 2, mapPt.y + mapProcessor.getUnitRoomDimension().height / 2, 0);

                if (this.<Boolean>getParameter("playerCenter").getValue() && this.<Boolean>getParameter("rotate").getValue()) {
                    GlStateManager.rotate((float) (yaw - 180), 0, 0, 1);
                }
                GlStateManager.scale(1 / scale, 1 / scale, 0);
                GlStateManager.scale(1 / postScale, 1 / postScale, 0);
                float s = this.<Float>getParameter("textScale").getValue();
                GlStateManager.scale(s,s,0);
                String str = "";
                str += dungeonRoom.getTotalSecrets() == -1 ? "?" : String.valueOf(dungeonRoom.getTotalSecrets());
                str += " ";
                if (dungeonRoom.getCurrentState() == DungeonRoom.RoomState.FINISHED) {
                    str += "✔";
                } else if (dungeonRoom.getCurrentState() == DungeonRoom.RoomState.COMPLETE_WITHOUT_SECRETS) {
                    str += "☑";
                } else if (dungeonRoom.getCurrentState() == DungeonRoom.RoomState.DISCOVERED) {
                    str += "☐";
                } else if (dungeonRoom.getCurrentState() == DungeonRoom.RoomState.FAILED) {
                    str += "❌";
                }


                GlStateManager.enableBlend();
                GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
                fr.drawString(str, -(fr.getStringWidth(str) / 2), -(fr.FONT_HEIGHT / 2), dungeonRoom.getCurrentState() == DungeonRoom.RoomState.FINISHED ? 0xFF00FF00 : (dungeonRoom.getColor() == 74 ? 0xff000000 : 0xFFFFFFFF));
                GlStateManager.popAttrib();
                GlStateManager.popMatrix();
            }
        }

    }



    private final DynamicTexture mapTexture = new DynamicTexture(128, 128);
    private final ResourceLocation location = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("dungeonmap/map", mapTexture);
    private final int[] mapTextureData = mapTexture.getTextureData();

    private void updateMapTexture(byte[] colors, MapProcessor mapProcessor, List<DungeonRoom> dungeonRooms) {
        for (int i = 0; i < 16384; ++i) {
            int j = colors[i] & 255;

            if (j / 4 == 0) {
                this.mapTextureData[i] = 0x00000000;
            } else {
                this.mapTextureData[i] = MapColor.mapColorArray[j / 4].getMapColor(j & 3);
            }
        }

        if (this.<Boolean>getParameter("showtotalsecrets").getValue()) {
            for (DungeonRoom dungeonRoom : dungeonRooms) {
                for (Point pt : dungeonRoom.getUnitPoints()) {
                    for (int y1 = 0; y1 < mapProcessor.getUnitRoomDimension().height; y1++) {
                        for (int x1 = 0; x1 < mapProcessor.getUnitRoomDimension().width; x1++) {
                            int x = MathHelper.clamp_int(pt.x * (mapProcessor.getUnitRoomDimension().width + mapProcessor.getDoorDimension().height) + x1 + mapProcessor.getTopLeftMapPoint().x, 0, 128);
                            int y = MathHelper.clamp_int(pt.y * (mapProcessor.getUnitRoomDimension().height + mapProcessor.getDoorDimension().height) + y1 + mapProcessor.getTopLeftMapPoint().y, 0, 128);
                            int i = y * 128 + x;
                            int j = dungeonRoom.getColor();

                            if (j / 4 == 0) {
                                this.mapTextureData[i] = 0x00000000;
                            } else {
                                this.mapTextureData[i] = MapColor.mapColorArray[j / 4].getMapColor(j & 3);
                            }
                        }
                    }
                }
            }
        }


        this.mapTexture.updateDynamicTexture();
    }

    private void renderHeads(MapProcessor mapProcessor, MapData mapData, float scale, float postScale, float partialTicks) {
        List<NetworkPlayerInfo> list = field_175252_a.sortedCopy(Minecraft.getMinecraft().thePlayer.sendQueue.getPlayerInfoMap());
        if (list.size() < 40) return;
        for (int i = 1; i < 20; i++) {
            NetworkPlayerInfo networkPlayerInfo = list.get(i);
            String name = networkPlayerInfo.getDisplayName() != null ? networkPlayerInfo.getDisplayName().getFormattedText() : ScorePlayerTeam.formatPlayerName(networkPlayerInfo.getPlayerTeam(), networkPlayerInfo.getGameProfile().getName());
            if (name.trim().equals("§r") || name.startsWith("§r ")) continue;
            String actual = TextUtils.stripColor(name).trim().split(" ")[0];
            EntityPlayer entityplayer = Minecraft.getMinecraft().theWorld.getPlayerEntityByName(actual);
            if (TextUtils.stripColor(name).endsWith("(DEAD)") && entityplayer != Minecraft.getMinecraft().thePlayer) {
                continue;
            }
            Point pt2;
            double yaw2;

            if (entityplayer != null && (!entityplayer.isInvisible() || entityplayer == Minecraft.getMinecraft().thePlayer)) {
                pt2 = mapProcessor.worldPointToMapPoint(entityplayer.getPositionEyes(partialTicks));
                yaw2 = entityplayer.prevRotationYawHead + (entityplayer.rotationYawHead - entityplayer.prevRotationYawHead) * partialTicks;
            } else {
                String iconName = mapProcessor.getMapIconToPlayerMap().get(actual);
                if (iconName == null) continue;
                Vec4b vec = mapData.mapDecorations.get(iconName);
                if (vec == null) {
                    continue;
                } else {
                    pt2 = new Point(vec.func_176112_b() / 2 + 64, vec.func_176113_c() / 2 + 64);
                    yaw2 = vec.func_176111_d() * 360 / 16.0f ;
                }
            }
            GlStateManager.pushMatrix();
            if (entityplayer == Minecraft.getMinecraft().thePlayer || this.<Boolean>getParameter("showotherplayers").getValue())
            {
                boolean flag1 = entityplayer != null && entityplayer.isWearing(EnumPlayerModelParts.CAPE);
                GlStateManager.enableTexture2D();
                Minecraft.getMinecraft().getTextureManager().bindTexture(networkPlayerInfo.getLocationSkin());
                int l2 = 8 + (flag1 ? 8 : 0);
                int i3 = 8 * (flag1 ? -1 : 1);

                GlStateManager.translate(pt2.x, pt2.y, 0);
                GlStateManager.rotate((float) (yaw2 - 180), 0, 0, 1);

                GlStateManager.scale(1 / scale, 1 / scale, 0);
                GlStateManager.scale(1 / postScale, 1 / postScale, 0);
                float s = this.<Float>getParameter("playerheadscale").getValue();
                GlStateManager.scale(s,s,0);
                Gui.drawScaledCustomSizeModalRect(-4, -4, 8.0F, (float) l2, 8, i3, 8, 8, 64.0F, 64.0F);
                GL11.glLineWidth(1);
                RenderUtils.drawUnfilledBox(-4,-4,4, 4, this.<AColor>getParameter("player_color").getValue());
            }
            GlStateManager.popMatrix();
        }
    }
    private static final ResourceLocation mapIcons = new ResourceLocation("textures/map/map_icons.png");

    private void renderArrows(MapProcessor mapProcessor, MapData mapData, float scale, float postScale, float partialTicks) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        int k = 0;
        Minecraft.getMinecraft().getTextureManager().bindTexture(mapIcons);
        for (Vec4b vec4b : mapData.mapDecorations.values()) {
                if (vec4b.func_176110_a() == 1 || this.<Boolean>getParameter("showotherplayers").getValue()) {
                    GlStateManager.pushMatrix();
                    GlStateManager.translate((float)vec4b.func_176112_b() / 2.0F + 64.0F, (float)vec4b.func_176113_c() / 2.0F + 64.0F, -0.02F);
                    GlStateManager.rotate((float)(vec4b.func_176111_d() * 360) / 16.0F, 0.0F, 0.0F, 1.0F);

                    GlStateManager.scale(1 / scale, 1 / scale, 0);
                    GlStateManager.scale(1 / postScale, 1 / postScale, 0);
                    float s = this.<Float>getParameter("playerheadscale").getValue();
                    GlStateManager.scale(s * 5,s * 5,0);

                    GlStateManager.translate(-0.125F, 0.125F, 0.0F);
                    byte b0 = vec4b.func_176110_a();
                    float f1 = (float)(b0 % 4) / 4.0F;
                    float f2 = (float)(b0 / 4) / 4.0F;
                    float f3 = (float)(b0 % 4 + 1) / 4.0F;
                    float f4 = (float)(b0 / 4 + 1) / 4.0F;
                    worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
                    float f5 = -0.001F;
                    worldrenderer.pos(-1.0D, 1.0D, (float)k * -0.001F).tex(f1, f2).endVertex();
                    worldrenderer.pos(1.0D, 1.0D, (float)k * -0.001F).tex(f3, f2).endVertex();
                    worldrenderer.pos(1.0D, -1.0D, (float)k * -0.001F).tex(f3, f4).endVertex();
                    worldrenderer.pos(-1.0D, -1.0D, (float)k * -0.001F).tex(f1, f4).endVertex();
                    tessellator.draw();
                    GlStateManager.popMatrix();
                    ++k;
                }
        }
    }

    private void render(MapData mapData, boolean noOverlayRendering) {
        int i = 0;
        int j = 0;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        float f = 0.0F;
        Minecraft.getMinecraft().getTextureManager().bindTexture(this.location);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(1, 771, 0, 1);
        GlStateManager.disableAlpha();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos((float)(i) + f, (float)(j + 128) - f, -0.009999999776482582D).tex(0.0D, 1.0D).endVertex();
        worldrenderer.pos((float)(i + 128) - f, (float)(j + 128) - f, -0.009999999776482582D).tex(1.0D, 1.0D).endVertex();
        worldrenderer.pos((float)(i + 128) - f, (float)(j) + f, -0.009999999776482582D).tex(1.0D, 0.0D).endVertex();
        worldrenderer.pos((float)(i) + f, (float)(j) + f, -0.009999999776482582D).tex(0.0D, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.0F, -0.04F);
        GlStateManager.scale(1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

}
