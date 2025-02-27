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

package kr.syeyoung.dungeonsguide.utils;

import kr.syeyoung.dungeonsguide.config.types.AColor;
import kr.syeyoung.dungeonsguide.dungeon.doorfinder.DungeonDoor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import javax.vecmath.Vector3f;
import java.awt.*;
import java.util.List;

public class RenderUtils {
    public static final ResourceLocation icons = new ResourceLocation("textures/gui/icons.png");

    public static void drawTexturedRect(float x, float y, float width, float height, int filter) {
        drawTexturedRect(x, y, width, height, 0.0F, 1.0F, 0.0F, 1.0F, filter);
    }
    private static float zLevel = 0;
    public static int scrollY = 0;
    public static boolean allowScrolling;
    public static int scrollX = 0;

    public static void drawHoveringText(List<String> textLines, int x, int y, FontRenderer font)
    {
        if (!textLines.isEmpty())
        {
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int i = 0;

            for (String s : textLines)
            {
                int j = font.getStringWidth(s);

                if (j > i)
                {
                    i = j;
                }
            }

            int l1 = x + 12;
            int i2 = y - 12;
            int k = 8;

            if (textLines.size() > 1)
            {
                k += 2 + (textLines.size() - 1) * 10;
            }

            zLevel = 300.0F;
            int l = -267386864;


            if (!allowScrolling) {
                scrollX = 0;
                scrollY = 0;
            }
            allowScrolling = (i2 < 0);
            GlStateManager.pushMatrix();
            if (allowScrolling) {
                int eventDWheel = Mouse.getDWheel();
                if (Keyboard.isKeyDown(42)) {
                    if (eventDWheel < 0) {
                        scrollX += 10;
                    } else if (eventDWheel > 0) {
                        scrollX -= 10;
                    }
                } else if (eventDWheel < 0) {
                    scrollY -= 10;
                } else if (eventDWheel > 0) {
                    scrollY += 10;
                }
            }
            GlStateManager.translate(scrollX, scrollY, 0.0F);

            drawGradientRect(l1 - 3, i2 - 4, l1 + i + 3, i2 - 3, l, l);
            drawGradientRect(l1 - 3, i2 + k + 3, l1 + i + 3, i2 + k + 4, l, l);
            drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 + k + 3, l, l);
            drawGradientRect(l1 - 4, i2 - 3, l1 - 3, i2 + k + 3, l, l);
            drawGradientRect(l1 + i + 3, i2 - 3, l1 + i + 4, i2 + k + 3, l, l);
            int i1 = 1347420415;
            int j1 = (i1 & 16711422) >> 1 | i1 & -16777216;
            drawGradientRect(l1 - 3, i2 - 3 + 1, l1 - 3 + 1, i2 + k + 3 - 1, i1, j1);
            drawGradientRect(l1 + i + 2, i2 - 3 + 1, l1 + i + 3, i2 + k + 3 - 1, i1, j1);
            drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 - 3 + 1, i1, i1);
            drawGradientRect(l1 - 3, i2 + k + 2, l1 + i + 3, i2 + k + 3, j1, j1);

            GlStateManager.enableBlend();
            GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
            for (int k1 = 0; k1 < textLines.size(); ++k1)
            {
                String s1 = textLines.get(k1);
                font.drawStringWithShadow(s1, (float)l1, (float)i2, -1);

                if (k1 == 0)
                {
                    i2 += 2;
                }

                i2 += 10;
            }

            zLevel = 0.0F;
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
            GlStateManager.popMatrix();
        }
    }
    protected static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor)
    {
        float f = (float)(startColor >> 24 & 255) / 255.0F;
        float f1 = (float)(startColor >> 16 & 255) / 255.0F;
        float f2 = (float)(startColor >> 8 & 255) / 255.0F;
        float f3 = (float)(startColor & 255) / 255.0F;
        float f4 = (float)(endColor >> 24 & 255) / 255.0F;
        float f5 = (float)(endColor >> 16 & 255) / 255.0F;
        float f6 = (float)(endColor >> 8 & 255) / 255.0F;
        float f7 = (float)(endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(right, top, zLevel).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos(left, top, zLevel).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos(left, bottom, zLevel).color(f5, f6, f7, f4).endVertex();
        worldrenderer.pos(right, bottom, zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
    public static void drawTexturedRect(float x, float y, float width, float height, float uMin, float uMax, float vMin, float vMax, int filter) {
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 771);
        GL11.glTexParameteri(3553, 10241, filter);
        GL11.glTexParameteri(3553, 10240, filter);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + height, 0.0D).tex(uMin, vMax).endVertex();
        worldrenderer.pos(x + width, y + height, 0.0D).tex(uMax, vMax).endVertex();
        worldrenderer.pos(x + width, y, 0.0D).tex(uMax, vMin).endVertex();
        worldrenderer.pos(x, y, 0.0D).tex(uMin, vMin).endVertex();
        tessellator.draw();
        GL11.glTexParameteri(3553, 10241, 9728);
        GL11.glTexParameteri(3553, 10240, 9728);
        GlStateManager.disableBlend();
    }
    public static void renderBar(float x, float y, float xSize, float completed) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(icons);
        completed = (float)Math.round(completed / 0.05F) * 0.05F;
        float notcompleted = 1.0F - completed;
        int displayNum = 0;
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        float width = 0.0F;
        if (completed < 0.5F && (displayNum == 1 || displayNum == 0)) {
            width = (0.5F - completed) * xSize;
            drawTexturedRect(x + xSize * completed, y, width, 5.0F, xSize * completed / 256.0F, xSize / 2.0F / 256.0F, 0.2890625F, 0.30859375F, 9728);
        }

        if (completed < 1.0F && (displayNum == 2 || displayNum == 0)) {
            width = Math.min(xSize * notcompleted, xSize / 2.0F);
            drawTexturedRect(x + xSize / 2.0F + Math.max(xSize * (completed - 0.5F), 0.0F), y, width, 5.0F, (182.0F - xSize / 2.0F + Math.max(xSize * (completed - 0.5F), 0.0F)) / 256.0F, 0.7109375F, 0.2890625F, 0.30859375F, 9728);
        }

        if (completed > 0.0F && (displayNum == 3 || displayNum == 0)) {
            width = Math.min(xSize * completed, xSize / 2.0F);
            drawTexturedRect(x, y, width, 5.0F, 0.0F, width / 256.0F, 0.30859375F, 0.328125F, 9728);
        }

        if (completed > 0.5F && (displayNum == 4 || displayNum == 0)) {
            width = Math.min(xSize * (completed - 0.5F), xSize / 2.0F);
            drawTexturedRect(x + xSize / 2.0F, y, width, 5.0F, (182.0F - xSize / 2.0F) / 256.0F, (182.0F - xSize / 2.0F + width) / 256.0F, 0.30859375F, 0.328125F, 9728);
        }

    }

    public static void drawUnfilledBox(int left, int top, int right, int bottom, int color, boolean chroma)
    {
        if (left < right)
        {
            int i = left;
            left = right;
            right = i;
        }

        if (top < bottom)
        {
            int j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float)(color >> 24 & 255) / 255.0F;
        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;
        if (!chroma && f3 == 0) return;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        if (!chroma) {
            GlStateManager.color(f, f1, f2, f3);
            worldrenderer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION);
            worldrenderer.pos(left, bottom, 0.0D).endVertex();
            worldrenderer.pos(right, bottom, 0.0D).endVertex();
            worldrenderer.pos(right, top, 0.0D).endVertex();
            worldrenderer.pos(left, top, 0.0D).endVertex();
        } else {
            worldrenderer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
            float blah = (System.currentTimeMillis()  / 10) % 360;
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
            color(worldrenderer.pos(left, bottom, 0.0D), Color.HSBtoRGB((((blah + 20) % 360) / 360.0f), 1, 1)).endVertex();
            color(worldrenderer.pos(right, bottom, 0.0D), Color.HSBtoRGB((((blah + 40) % 360)  / 360.0f), 1, 1)).endVertex();
            color(worldrenderer.pos(right, top, 0.0D), Color.HSBtoRGB((((blah + 20) % 360) / 360.0f), 1, 1)).endVertex();
            color(worldrenderer.pos(left, top, 0.0D), Color.HSBtoRGB(blah / 360.0f, 1, 1)).endVertex();
        }
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }


    public static void drawUnfilledBox(int left, int top, int right, int bottom, AColor color)
    {
        if (left < right)
        {
            int i = left;
            left = right;
            right = i;
        }

        if (top < bottom)
        {
            int j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float)(color.getRGB() >> 24 & 255) / 255.0F;
        float f = (float)(color.getRGB() >> 16 & 255) / 255.0F;
        float f1 = (float)(color.getRGB() >> 8 & 255) / 255.0F;
        float f2 = (float)(color.getRGB() & 255) / 255.0F;
        if (!color.isChroma() && f3 == 0) return;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        if (!color.isChroma()) {
            GlStateManager.color(f, f1, f2, f3);
            worldrenderer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION);
            worldrenderer.pos(left, bottom, 0.0D).endVertex();
            worldrenderer.pos(right, bottom, 0.0D).endVertex();
            worldrenderer.pos(right, top, 0.0D).endVertex();
            worldrenderer.pos(left, top, 0.0D).endVertex();
        } else {
            worldrenderer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
            color(worldrenderer.pos(left, bottom, 0.0D), getColorAt(left, bottom, color)).endVertex();
            color(worldrenderer.pos(right, bottom, 0.0D), getColorAt(right, bottom, color)).endVertex();
            color(worldrenderer.pos(right, top, 0.0D), getColorAt(right, top, color)).endVertex();
            color(worldrenderer.pos(left, top, 0.0D), getColorAt(left, top, color)).endVertex();
        }
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static int getChromaColorAt(int x, int y, float speed, float s, float b, float alpha) {
        double blah = ((double)(speed) * (System.currentTimeMillis() / 2)) % 360;
        return (Color.HSBtoRGB((float) (((blah - (x + y) / 2.0f) % 360) / 360.0f), s,b) & 0xffffff)
                | (((int)(alpha * 255)<< 24) & 0xff000000);
    }
    public static int getColorAt(double x, double y, AColor color) {
        if (!color.isChroma())
            return color.getRGB();

        double blah = ((double)(color.getChromaSpeed()) * (System.currentTimeMillis() / 2)) % 360;
        float[] hsv = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getBlue(), color.getGreen(), hsv);


        return (Color.HSBtoRGB((float) (((blah - (x + y) / 2.0f) % 360) / 360.0f), hsv[1],hsv[2]) & 0xffffff)
                | ((color.getAlpha() << 24) & 0xff000000);
    }
    public static int getColorAt(double x, double y,double z, AColor color) {
        if (!color.isChroma())
            return color.getRGB();

        double blah = ((double)(color.getChromaSpeed()) * (System.currentTimeMillis() / 2)) % 360;
        float[] hsv = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getBlue(), color.getGreen(), hsv);


        return (Color.HSBtoRGB((float) (((blah - ((x + y+z) / 2.0f) % 360)) / 360.0f), hsv[1],hsv[2]) & 0xffffff)
                | ((color.getAlpha() << 24) & 0xff000000);
    }

    public static WorldRenderer color(WorldRenderer worldRenderer, int color ){
        return worldRenderer.color(((color >> 16) & 0xFF) / 255.0f, ((color >> 8) & 0xFF) / 255.0f, (color &0xFF) / 255.0f, ((color >> 24) & 0xFF) / 255.0f);
    }

    public static void renderDoor(DungeonDoor dungeonDoor, float partialTicks) {
        Entity player = Minecraft.getMinecraft().thePlayer;
        double playerX = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
        double playerY = player.prevPosY + (player.posY - player.prevPosY) * partialTicks;
        double playerZ = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;
//because of the way 3D rendering is done, all coordinates are relative to the camera.  This "resets" the "0,0,0" position to the location that is (0,0,0) in the world.

        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();
        GlStateManager.translate(-playerX, -playerY, -playerZ);
        GlStateManager.disableTexture2D();
        GlStateManager.disableCull();
        GlStateManager.enableAlpha();

        if (dungeonDoor.isExist())
            GlStateManager.color(0,1,0,1);
        else
            GlStateManager.color(1,0,0,1);

        double x = dungeonDoor.getPosition().getX() + 0.5;
        double y = dungeonDoor.getPosition().getY() -0.99;
        double z = dungeonDoor.getPosition().getZ() + 0.5;
        GL11.glBegin(GL11.GL_QUADS);

        GL11.glVertex3d(x - 2.5, y, z - 2.5);
        GL11.glVertex3d(x - 2.5, y, z + 2.5);
        GL11.glVertex3d(x + 2.5, y, z + 2.5);
        GL11.glVertex3d(x + 2.5, y, z - 2.5);

        GL11.glEnd();

        if (dungeonDoor.isExist()) {
            GL11.glBegin(GL11.GL_QUADS);

            GlStateManager.color(0,0,1,1);
            if (dungeonDoor.isZDir()) {
                GL11.glVertex3d(x - 0.5, y + 0.1, z - 2.5);
                GL11.glVertex3d(x - 0.5, y+ 0.1, z + 2.5);
                GL11.glVertex3d(x + 0.5, y+ 0.1, z + 2.5);
                GL11.glVertex3d(x + 0.5, y+ 0.1, z - 2.5);
            } else {
                GL11.glVertex3d(x - 2.5, y+ 0.1, z - 0.5);
                GL11.glVertex3d(x - 2.5, y+ 0.1, z + 0.5);
                GL11.glVertex3d(x + 2.5, y+ 0.1, z + 0.5);
                GL11.glVertex3d(x + 2.5, y+ 0.1, z - 0.5);
            }

            GL11.glEnd();
        } else {
            GL11.glLineWidth(5);
            GL11.glBegin(GL11.GL_LINE_LOOP);
            GL11.glVertex3d(x - 2.5, y, z - 2.5);
            GL11.glVertex3d(x + 2.5, y + 5, z - 2.5);
            GL11.glVertex3d(x + 2.5, y, z + 2.5);
            GL11.glVertex3d(x - 2.5, y + 5, z + 2.5);
            GL11.glEnd();
            GL11.glBegin(GL11.GL_LINE_LOOP);
            GL11.glVertex3d(x - 2.5, y +5, z - 2.5);
            GL11.glVertex3d(x + 2.5, y, z - 2.5);
            GL11.glVertex3d(x + 2.5, y + 5, z + 2.5);
            GL11.glVertex3d(x - 2.5, y, z + 2.5);
            GL11.glEnd();
            GL11.glLineWidth(1);
        }
//        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();

        GlStateManager.enableTexture2D();
        GlStateManager.enableCull();

        GlStateManager.popAttrib();
        GlStateManager.popMatrix();

    }

    public static void drawLine(Vec3 pos1, Vec3 pos2, Color colour, float partialTicks , boolean depth) {
        Entity render = Minecraft.getMinecraft().getRenderViewEntity();
        WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();

        double realX = render.lastTickPosX + (render.posX - render.lastTickPosX) * partialTicks;
        double realY = render.lastTickPosY + (render.posY - render.lastTickPosY) * partialTicks;
        double realZ = render.lastTickPosZ + (render.posZ - render.lastTickPosZ) * partialTicks;

        GlStateManager.pushMatrix();
        GlStateManager.translate(-realX, -realY, -realZ);
        GlStateManager.disableTexture2D();
        if (!depth) {
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
        }
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glLineWidth(2);
        GlStateManager.color(colour.getRed() / 255f, colour.getGreen() / 255f, colour.getBlue()/ 255f, colour.getAlpha() / 255f);
        worldRenderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);

        worldRenderer.pos(pos1.xCoord, pos1.yCoord, pos1.zCoord).endVertex();
        worldRenderer.pos(pos2.xCoord, pos2.yCoord, pos2.zCoord).endVertex();
        Tessellator.getInstance().draw();

        GlStateManager.translate(realX, realY, realZ);
        GlStateManager.disableBlend();
        if (!depth) {
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
        }
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    public static void drawLines(List<BlockPos> poses, AColor colour, float thickness, float partialTicks, boolean depth) {
        Entity render = Minecraft.getMinecraft().getRenderViewEntity();
        WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();

        double realX = render.lastTickPosX + (render.posX - render.lastTickPosX) * partialTicks;
        double realY = render.lastTickPosY + (render.posY - render.lastTickPosY) * partialTicks;
        double realZ = render.lastTickPosZ + (render.posZ - render.lastTickPosZ) * partialTicks;

        GlStateManager.pushMatrix();
        GlStateManager.translate(-realX, -realY, -realZ);
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GL11.glLineWidth(thickness);
        if (!depth) {
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
        }
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

//        GlStateManager.color(colour.getRed() / 255f, colour.getGreen() / 255f, colour.getBlue()/ 255f, colour.getAlpha() / 255f);
        GlStateManager.color(1,1,1,1);
        worldRenderer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        int num = 0;
        for (BlockPos pos:poses) {
            int i = getColorAt(num++ * 10,0, colour);
            worldRenderer.pos(pos.getX() +0.5, pos.getY() +0.5, pos.getZ() +0.5).color(
                    ((i >> 16) &0xFF)/255.0f,
                    ((i >> 8) &0xFF)/255.0f,
                    (i &0xFF)/255.0f,
                    ((i >> 24) &0xFF)/255.0f
            ).endVertex();
        }
        Tessellator.getInstance().draw();

        GlStateManager.translate(realX, realY, realZ);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        if (!depth) {
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
        GL11.glLineWidth(1);
    }

    public static void drawLines(List<BlockPos> poses, Color colour, float thickness, float partialTicks, boolean depth) {
        if (colour instanceof AColor) drawLines(poses, (AColor)colour, thickness, partialTicks,depth);
        Entity render = Minecraft.getMinecraft().getRenderViewEntity();
        WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();

        double realX = render.lastTickPosX + (render.posX - render.lastTickPosX) * partialTicks;
        double realY = render.lastTickPosY + (render.posY - render.lastTickPosY) * partialTicks;
        double realZ = render.lastTickPosZ + (render.posZ - render.lastTickPosZ) * partialTicks;

        GlStateManager.pushMatrix();
        GlStateManager.translate(-realX, -realY, -realZ);
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GL11.glLineWidth(thickness);
        if (!depth) {
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
        }
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        GlStateManager.color(colour.getRed() / 255f, colour.getGreen() / 255f, colour.getBlue()/ 255f, colour.getAlpha() / 255f);
        worldRenderer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        for (BlockPos pos:poses) {
            worldRenderer.pos(pos.getX() +0.5, pos.getY() +0.5, pos.getZ() +0.5).endVertex();
        }
        Tessellator.getInstance().draw();

        GlStateManager.translate(realX, realY, realZ);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        if (!depth) {
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    public static void highlightBlock(BlockPos blockpos, Color c, float partialTicks) {
        highlightBlock(blockpos,c,partialTicks,false);
    }
    public static void highlightBlock(BlockPos blockpos, Color c, float partialTicks, boolean depth) {
        Entity viewing_from = Minecraft.getMinecraft().getRenderViewEntity();

        double x_fix = viewing_from.lastTickPosX + ((viewing_from.posX - viewing_from.lastTickPosX) * partialTicks);
        double y_fix = viewing_from.lastTickPosY + ((viewing_from.posY - viewing_from.lastTickPosY) * partialTicks);
        double z_fix = viewing_from.lastTickPosZ + ((viewing_from.posZ - viewing_from.lastTickPosZ) * partialTicks);

        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        GlStateManager.translate(-x_fix, -y_fix, -z_fix);

        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableTexture2D();

        if (!depth) {
            GlStateManager.disableDepth(); GL11.glDisable(GL11.GL_DEPTH_TEST);
            GlStateManager.depthMask(false);
        }
        GlStateManager.color(c.getRed() /255.0f, c.getGreen() / 255.0f, c.getBlue()/ 255.0f, c.getAlpha()/ 255.0f);

        GlStateManager.translate(blockpos.getX(), blockpos.getY(), blockpos.getZ());

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3d(0, 0, 0);
        GL11.glVertex3d(0, 0, 1);
        GL11.glVertex3d(0, 1, 1);
        GL11.glVertex3d(0, 1, 0); // TOP LEFT / BOTTOM LEFT / TOP RIGHT/ BOTTOM RIGHT

        GL11.glVertex3d(1, 0, 1);
        GL11.glVertex3d(1, 0, 0);
        GL11.glVertex3d(1, 1, 0);
        GL11.glVertex3d(1, 1, 1);

        GL11.glVertex3d(0, 1, 1);
        GL11.glVertex3d(0, 0, 1);
        GL11.glVertex3d(1, 0, 1);
        GL11.glVertex3d(1, 1, 1); // TOP LEFT / BOTTOM LEFT / TOP RIGHT/ BOTTOM RIGHT

        GL11.glVertex3d(0, 0, 0);
        GL11.glVertex3d(0, 1, 0);
        GL11.glVertex3d(1, 1, 0);
        GL11.glVertex3d(1, 0, 0);

        GL11.glVertex3d(0,1,0);
        GL11.glVertex3d(0,1,1);
        GL11.glVertex3d(1,1,1);
        GL11.glVertex3d(1,1,0);

        GL11.glVertex3d(0,0,1);
        GL11.glVertex3d(0,0,0);
        GL11.glVertex3d(1,0,0);
        GL11.glVertex3d(1,0,1);



        GL11.glEnd();


        if (!depth) {
            GlStateManager.disableDepth();
            GlStateManager.depthMask(true);
        }
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();


//...

    }

    public static void highlightBox(Entity entity, AxisAlignedBB  axisAlignedBB, AColor c, float partialTicks, boolean depth) {
        Entity viewing_from = Minecraft.getMinecraft().getRenderViewEntity();

        double x_fix = viewing_from.lastTickPosX + ((viewing_from.posX - viewing_from.lastTickPosX) * partialTicks);
        double y_fix = viewing_from.lastTickPosY + ((viewing_from.posY - viewing_from.lastTickPosY) * partialTicks);
        double z_fix = viewing_from.lastTickPosZ + ((viewing_from.posZ - viewing_from.lastTickPosZ) * partialTicks);

        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        GlStateManager.translate(-x_fix, -y_fix, -z_fix);

        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableTexture2D();

        if (!depth) {
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
        }
        int rgb = RenderUtils.getColorAt(entity.posX * 10,entity.posY * 10,c);
        GlStateManager.color(((rgb >> 16) &0XFF)/ 255.0f, ((rgb>>8) &0XFF)/ 255.0f, (rgb & 0xff)/ 255.0f, ((rgb >> 24) & 0xFF) / 255.0f);
        if (axisAlignedBB == null) {
            if (entity instanceof EntityArmorStand) {
                axisAlignedBB = AxisAlignedBB.fromBounds(-0.4, -1.5, -0.4, 0.4, 0, 0.4);
            } else if (entity instanceof EntityBat) {
                axisAlignedBB = AxisAlignedBB.fromBounds(-0.4, -1.4, -0.4, 0.4, 0.4, 0.4);
            } else {
                axisAlignedBB = AxisAlignedBB.fromBounds(-0.4, -1.5, -0.4, 0.4, 0, 0.4);
            }
        }

        Vec3 renderPos = new Vec3(
                (float) (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks),
                (float) (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks),
                (float) (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks)
        );
        GlStateManager.translate(axisAlignedBB.minX + renderPos.xCoord, axisAlignedBB.minY + renderPos.yCoord, axisAlignedBB.minZ + renderPos.zCoord);

        double x = axisAlignedBB.maxX - axisAlignedBB.minX;
        double y = axisAlignedBB.maxY - axisAlignedBB.minY;
        double z = axisAlignedBB.maxZ - axisAlignedBB.minZ;
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3d(0, 0, 0);
        GL11.glVertex3d(0, 0, z);
        GL11.glVertex3d(0, y, z);
        GL11.glVertex3d(0, y, 0); // TOP LEFT / BOTTOM LEFT / TOP RIGHT/ BOTTOM RIGHT

        GL11.glVertex3d(x, 0, z);
        GL11.glVertex3d(x, 0, 0);
        GL11.glVertex3d(x, y, 0);
        GL11.glVertex3d(x, y, z);

        GL11.glVertex3d(0, y, z);
        GL11.glVertex3d(0, 0, z);
        GL11.glVertex3d(x, 0, z);
        GL11.glVertex3d(x, y, z); // TOP LEFT / BOTTOM LEFT / TOP RIGHT/ BOTTOM RIGHT

        GL11.glVertex3d(0, 0, 0);
        GL11.glVertex3d(0, y, 0);
        GL11.glVertex3d(x, y, 0);
        GL11.glVertex3d(x, 0, 0);

        GL11.glVertex3d(0,y,0);
        GL11.glVertex3d(0,y,z);
        GL11.glVertex3d(x,y,z);
        GL11.glVertex3d(x,y,0);

        GL11.glVertex3d(0,0,z);
        GL11.glVertex3d(0,0,0);
        GL11.glVertex3d(x,0,0);
        GL11.glVertex3d(x,0,z);



        GL11.glEnd();


        if (!depth) {
            GlStateManager.disableDepth();
            GlStateManager.depthMask(true);
        }
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    public static void highlightBox(Entity entity, AxisAlignedBB  axisAlignedBB, Color c, float partialTicks, boolean depth) {
        Entity viewing_from = Minecraft.getMinecraft().getRenderViewEntity();

        double x_fix = viewing_from.lastTickPosX + ((viewing_from.posX - viewing_from.lastTickPosX) * partialTicks);
        double y_fix = viewing_from.lastTickPosY + ((viewing_from.posY - viewing_from.lastTickPosY) * partialTicks);
        double z_fix = viewing_from.lastTickPosZ + ((viewing_from.posZ - viewing_from.lastTickPosZ) * partialTicks);

        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        GlStateManager.translate(-x_fix, -y_fix, -z_fix);

        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableTexture2D();

        if (!depth) {
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
        }
        GlStateManager.color(c.getRed()/ 255.0f, c.getGreen()/ 255.0f, c.getBlue()/ 255.0f, c.getAlpha()/ 255.0f);
        if (axisAlignedBB == null) {
            if (entity instanceof EntityArmorStand) {
                axisAlignedBB = AxisAlignedBB.fromBounds(-0.4, -1.5, -0.4, 0.4, 0, 0.4);
            } else if (entity instanceof EntityBat) {
                axisAlignedBB = AxisAlignedBB.fromBounds(-0.4, -1.4, -0.4, 0.4, 0.4, 0.4);
            } else {
                axisAlignedBB = AxisAlignedBB.fromBounds(-0.4, -1.5, -0.4, 0.4, 0, 0.4);
            }
        }

        Vec3 renderPos = new Vec3(
                (float) (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks),
                (float) (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks),
                (float) (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks)
        );
        GlStateManager.translate(axisAlignedBB.minX + renderPos.xCoord, axisAlignedBB.minY + renderPos.yCoord, axisAlignedBB.minZ + renderPos.zCoord);

        double x = axisAlignedBB.maxX - axisAlignedBB.minX;
        double y = axisAlignedBB.maxY - axisAlignedBB.minY;
        double z = axisAlignedBB.maxZ - axisAlignedBB.minZ;
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3d(0, 0, 0);
        GL11.glVertex3d(0, 0, z);
        GL11.glVertex3d(0, y, z);
        GL11.glVertex3d(0, y, 0); // TOP LEFT / BOTTOM LEFT / TOP RIGHT/ BOTTOM RIGHT

        GL11.glVertex3d(x, 0, z);
        GL11.glVertex3d(x, 0, 0);
        GL11.glVertex3d(x, y, 0);
        GL11.glVertex3d(x, y, z);

        GL11.glVertex3d(0, y, z);
        GL11.glVertex3d(0, 0, z);
        GL11.glVertex3d(x, 0, z);
        GL11.glVertex3d(x, y, z); // TOP LEFT / BOTTOM LEFT / TOP RIGHT/ BOTTOM RIGHT

        GL11.glVertex3d(0, 0, 0);
        GL11.glVertex3d(0, y, 0);
        GL11.glVertex3d(x, y, 0);
        GL11.glVertex3d(x, 0, 0);

        GL11.glVertex3d(0,y,0);
        GL11.glVertex3d(0,y,z);
        GL11.glVertex3d(x,y,z);
        GL11.glVertex3d(x,y,0);

        GL11.glVertex3d(0,0,z);
        GL11.glVertex3d(0,0,0);
        GL11.glVertex3d(x,0,0);
        GL11.glVertex3d(x,0,z);



        GL11.glEnd();


        if (!depth) {
            GlStateManager.disableDepth();
            GlStateManager.depthMask(true);
        }
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }
    public static void highlightBox(Entity entity, Color c, float partialTicks, boolean depth) {
        Entity viewing_from = Minecraft.getMinecraft().getRenderViewEntity();

        double x_fix = viewing_from.lastTickPosX + ((viewing_from.posX - viewing_from.lastTickPosX) * partialTicks);
        double y_fix = viewing_from.lastTickPosY + ((viewing_from.posY - viewing_from.lastTickPosY) * partialTicks);
        double z_fix = viewing_from.lastTickPosZ + ((viewing_from.posZ - viewing_from.lastTickPosZ) * partialTicks);

        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        GlStateManager.translate(-x_fix, -y_fix, -z_fix);

        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableTexture2D();

        if (!depth) {
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
        }
        GlStateManager.color(c.getRed()/ 255.0f, c.getGreen()/ 255.0f, c.getBlue()/ 255.0f, c.getAlpha()/ 255.0f);
        AxisAlignedBB axisAlignedBB;
        if (entity instanceof EntityArmorStand) {
            axisAlignedBB = AxisAlignedBB.fromBounds(-0.4, -1.5, -0.4, 0.4, 0, 0.4);
        } else if (entity instanceof EntityBat) {
            axisAlignedBB = AxisAlignedBB.fromBounds(-0.4, -1.4, -0.4, 0.4, 0.4, 0.4);
        } else {
            axisAlignedBB = AxisAlignedBB.fromBounds(-0.4, -1.5, -0.4, 0.4, 0, 0.4);
        }

        Vec3 renderPos = new Vec3(
                (float) (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks),
                (float) (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks),
                (float) (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks)
        );
        GlStateManager.translate(axisAlignedBB.minX + renderPos.xCoord, axisAlignedBB.minY + renderPos.yCoord, axisAlignedBB.minZ + renderPos.zCoord);

        double x = axisAlignedBB.maxX - axisAlignedBB.minX;
        double y = axisAlignedBB.maxY - axisAlignedBB.minY;
        double z = axisAlignedBB.maxZ - axisAlignedBB.minZ;
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3d(0, 0, 0);
        GL11.glVertex3d(0, 0, z);
        GL11.glVertex3d(0, y, z);
        GL11.glVertex3d(0, y, 0); // TOP LEFT / BOTTOM LEFT / TOP RIGHT/ BOTTOM RIGHT

        GL11.glVertex3d(x, 0, z);
        GL11.glVertex3d(x, 0, 0);
        GL11.glVertex3d(x, y, 0);
        GL11.glVertex3d(x, y, z);

        GL11.glVertex3d(0, y, z);
        GL11.glVertex3d(0, 0, z);
        GL11.glVertex3d(x, 0, z);
        GL11.glVertex3d(x, y, z); // TOP LEFT / BOTTOM LEFT / TOP RIGHT/ BOTTOM RIGHT

        GL11.glVertex3d(0, 0, 0);
        GL11.glVertex3d(0, y, 0);
        GL11.glVertex3d(x, y, 0);
        GL11.glVertex3d(x, 0, 0);

        GL11.glVertex3d(0,y,0);
        GL11.glVertex3d(0,y,z);
        GL11.glVertex3d(x,y,z);
        GL11.glVertex3d(x,y,0);

        GL11.glVertex3d(0,0,z);
        GL11.glVertex3d(0,0,0);
        GL11.glVertex3d(x,0,0);
        GL11.glVertex3d(x,0,z);



        GL11.glEnd();


        if (!depth) {
            GlStateManager.disableDepth();
            GlStateManager.depthMask(true);
        }
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();


//...

    }

    public static void highlightBox(Entity entity, AColor c, float partialTicks, boolean depth) {
        Entity viewing_from = Minecraft.getMinecraft().getRenderViewEntity();

        double x_fix = viewing_from.lastTickPosX + ((viewing_from.posX - viewing_from.lastTickPosX) * partialTicks);
        double y_fix = viewing_from.lastTickPosY + ((viewing_from.posY - viewing_from.lastTickPosY) * partialTicks);
        double z_fix = viewing_from.lastTickPosZ + ((viewing_from.posZ - viewing_from.lastTickPosZ) * partialTicks);

        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        GlStateManager.translate(-x_fix, -y_fix, -z_fix);

        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableTexture2D();

        if (!depth) {
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
        }

        int rgb = RenderUtils.getColorAt(entity.posX % 20,entity.posY % 20,c);
        GlStateManager.color(((rgb >> 16) &0XFF)/ 255.0f, ((rgb>>8) &0XFF)/ 255.0f, (rgb & 0xff)/ 255.0f, ((rgb >> 24) & 0xFF) / 255.0f);

        AxisAlignedBB axisAlignedBB;
        if (entity instanceof EntityArmorStand) {
            axisAlignedBB = AxisAlignedBB.fromBounds(-0.4, -1.5, -0.4, 0.4, 0, 0.4);
        } else if (entity instanceof EntityBat) {
            axisAlignedBB = AxisAlignedBB.fromBounds(-0.4, -1.4, -0.4, 0.4, 0.4, 0.4);
        } else {
            axisAlignedBB = AxisAlignedBB.fromBounds(-0.4, -1.5, -0.4, 0.4, 0, 0.4);
        }

        Vec3 renderPos = new Vec3(
                 (float) (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks),
                 (float) (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks),
                 (float) (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks)
        );
        GlStateManager.translate(axisAlignedBB.minX + renderPos.xCoord, axisAlignedBB.minY + renderPos.yCoord, axisAlignedBB.minZ + renderPos.zCoord);

        double x = axisAlignedBB.maxX - axisAlignedBB.minX;
        double y = axisAlignedBB.maxY - axisAlignedBB.minY;
        double z = axisAlignedBB.maxZ - axisAlignedBB.minZ;
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3d(0, 0, 0);
        GL11.glVertex3d(0, 0, z);
        GL11.glVertex3d(0, y, z);
        GL11.glVertex3d(0, y, 0); // TOP LEFT / BOTTOM LEFT / TOP RIGHT/ BOTTOM RIGHT

        GL11.glVertex3d(x, 0, z);
        GL11.glVertex3d(x, 0, 0);
        GL11.glVertex3d(x, y, 0);
        GL11.glVertex3d(x, y, z);

        GL11.glVertex3d(0, y, z);
        GL11.glVertex3d(0, 0, z);
        GL11.glVertex3d(x, 0, z);
        GL11.glVertex3d(x, y, z); // TOP LEFT / BOTTOM LEFT / TOP RIGHT/ BOTTOM RIGHT

        GL11.glVertex3d(0, 0, 0);
        GL11.glVertex3d(0, y, 0);
        GL11.glVertex3d(x, y, 0);
        GL11.glVertex3d(x, 0, 0);

        GL11.glVertex3d(0,y,0);
        GL11.glVertex3d(0,y,z);
        GL11.glVertex3d(x,y,z);
        GL11.glVertex3d(x,y,0);

        GL11.glVertex3d(0,0,z);
        GL11.glVertex3d(0,0,0);
        GL11.glVertex3d(x,0,0);
        GL11.glVertex3d(x,0,z);



        GL11.glEnd();


        if (!depth) {
            GlStateManager.disableDepth();
            GlStateManager.depthMask(true);
        }
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();


//...

    }
    public static void drawTextAtWorld(String text, float x, float y, float z, int color, float scale, boolean increase, boolean renderBlackBox, float partialTicks) {
        float lScale = scale;

        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;

        Vector3f renderPos = getRenderPos(x, y, z, partialTicks);

        if (increase) {
            double distance = Math.sqrt(renderPos.x * renderPos.x + renderPos.y * renderPos.y + renderPos.z * renderPos.z);
            double multiplier = distance / 120f; //mobs only render ~120 blocks away
            lScale *= 0.45f * multiplier;
        }

        GlStateManager.color(1f, 1f, 1f, 0.5f);
        GlStateManager.pushMatrix();
        GlStateManager.translate(renderPos.x, renderPos.y, renderPos.z);
        GlStateManager.rotate(-renderManager.playerViewY, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(renderManager.playerViewX, 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(-lScale, -lScale, lScale);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false); GL11.glDisable(GL11.GL_DEPTH_TEST);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        int textWidth = fontRenderer.getStringWidth(text);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        if (renderBlackBox) {
            double j = textWidth / 2;
            GlStateManager.disableTexture2D();
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            worldRenderer.pos(-j - 1, -1, 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
            worldRenderer.pos(-j - 1, 8, 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
            worldRenderer.pos(j + 1, 8, 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
            worldRenderer.pos(j + 1, -1, 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
            tessellator.draw();
            GlStateManager.enableTexture2D();
        }

        GlStateManager.enableBlend();
        GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        fontRenderer.drawString(text, -textWidth / 2, 0, color);

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }

    public static Vector3f getRenderPos(float x, float y, float z, float partialTicks) {
        EntityPlayerSP sp = Minecraft.getMinecraft().thePlayer;
        return new Vector3f(
                x - (float) (sp.lastTickPosX + (sp.posX - sp.lastTickPosX) * partialTicks),
                y - (float) (sp.lastTickPosY + (sp.posY - sp.lastTickPosY) * partialTicks),
                z - (float) (sp.lastTickPosZ + (sp.posZ - sp.lastTickPosZ) * partialTicks)
        );
    }
}
