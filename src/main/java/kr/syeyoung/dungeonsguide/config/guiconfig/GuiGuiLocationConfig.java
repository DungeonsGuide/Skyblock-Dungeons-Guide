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

package kr.syeyoung.dungeonsguide.config.guiconfig;

import kr.syeyoung.dungeonsguide.features.AbstractFeature;
import kr.syeyoung.dungeonsguide.features.FeatureRegistry;
import kr.syeyoung.dungeonsguide.features.GuiFeature;
import kr.syeyoung.dungeonsguide.gui.MPanel;
import kr.syeyoung.dungeonsguide.gui.elements.MButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;

public class GuiGuiLocationConfig extends GuiScreen {

    private final MPanel mainPanel = new MPanel();
    private final GuiScreen before;

    public GuiGuiLocationConfig(final GuiScreen before, AbstractFeature featureWhitelist) {
        this.before = before;
        for (AbstractFeature feature : FeatureRegistry.getFeatureList()) {
            if (feature instanceof GuiFeature && feature.isEnabled()) {
                mainPanel.add(new PanelDelegate((GuiFeature) feature, featureWhitelist == null || feature == featureWhitelist));
            }
        }

        mainPanel.setBackgroundColor(new Color(0,0,0, 60));
        {
            MButton button = new MButton() {
                @Override
                public void resize(int parentWidth, int parentHeight) {
                    setBounds(new Rectangle(parentWidth-50,parentHeight-20,50,20));
                }
            };
            button.setText("back");
            button.setOnActionPerformed(new Runnable() {
                @Override
                public void run() {
                    Minecraft.getMinecraft().displayGuiScreen(before);
                }
            });
            mainPanel.add(button);
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        mainPanel.setBounds(new Rectangle(0,0,scaledResolution.getScaledWidth(),scaledResolution.getScaledHeight()));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GL11.glDisable(GL11.GL_FOG);
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        mainPanel.render0(scaledResolution, new Point(0,0), new Rectangle(0,0,scaledResolution.getScaledWidth(),scaledResolution.getScaledHeight()), mouseX, mouseY, mouseX, mouseY, partialTicks);
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
        GlStateManager.enableBlend();
        GlStateManager.enableLighting();
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        mainPanel.keyTyped0(typedChar, keyCode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        try {
            mainPanel.mouseClicked0(mouseX, mouseY, mouseX, mouseY, mouseButton);
        } catch (IllegalArgumentException ignored) {
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        mainPanel.mouseReleased0(mouseX, mouseY,mouseX,mouseY, state);
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        try {
        mainPanel.mouseClickMove0(mouseX,mouseY,mouseX,mouseY,clickedMouseButton,timeSinceLastClick);
        } catch (IllegalArgumentException ignored) {
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        int i = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int j = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

        int wheel = Mouse.getDWheel();
        if (wheel != 0) {
            mainPanel.mouseScrolled0(i, j,i,j, wheel);
        }
    }
}
