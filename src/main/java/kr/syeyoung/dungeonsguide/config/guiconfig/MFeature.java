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
import kr.syeyoung.dungeonsguide.features.GuiFeature;
import kr.syeyoung.dungeonsguide.gui.MPanel;
import kr.syeyoung.dungeonsguide.gui.elements.MButton;
import kr.syeyoung.dungeonsguide.gui.elements.MLabel;
import kr.syeyoung.dungeonsguide.gui.elements.MStringSelectionButton;
import kr.syeyoung.dungeonsguide.gui.elements.MToggleButton;
import kr.syeyoung.dungeonsguide.utils.RenderUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class MFeature extends MPanel {

    @Getter
    private final AbstractFeature feature;

    private final List<MPanel> addons =  new ArrayList<MPanel>();

    @Getter @Setter
    private Color hover;

    private final GuiConfig config;

    public MFeature(final AbstractFeature abstractFeature, final GuiConfig config) {
        this.config = config;
        this.feature = abstractFeature;

        if (abstractFeature.isDisyllable()) {
            final MToggleButton mStringSelectionButton = new MToggleButton();
            mStringSelectionButton.setOnToggle(new Runnable() {
                @Override
                public void run() {
                    boolean selected = mStringSelectionButton.isEnabled();
                    feature.setEnabled(selected);
                }
            });
            addons.add(mStringSelectionButton);
            mStringSelectionButton.setEnabled(feature.isEnabled());
            mStringSelectionButton.setSize(new Dimension(30, 15));
            add(mStringSelectionButton);
        }
        if (abstractFeature.getParameters().size() != 0) {
            MButton button = new MButton();
            button.setText("Edit");
            button.setOnActionPerformed(new Runnable() {
                @Override
                public void run() {
                    config.getTabbedPane().setCurrentPage(abstractFeature.getEditRoute(config));
                }
            });
            addons.add(button);
            button.setSize(new Dimension(50, 15));
            add(button);
        }
        if (abstractFeature instanceof GuiFeature) {
            MButton button = new MButton();
            button.setText("GUI");
            button.setOnActionPerformed(new Runnable() {
                @Override
                public void run() {
                    Minecraft.getMinecraft().displayGuiScreen(new GuiGuiLocationConfig(config, abstractFeature));
                }
            });
            addons.add(button);
            button.setSize(new Dimension(50, 15));
            add(button);
        }
    }

    @Override
    public void render(int absMousex, int absMousey, int relMousex0, int relMousey0, float partialTicks, Rectangle scissor) {
        Gui.drawRect(0,0,getBounds().width, getBounds().height,0xFF444444);
        if (hover != null && new Rectangle(new Point(0,0),getBounds().getSize()).contains(relMousex0, relMousey0)) {
            Gui.drawRect(1,18,getBounds().width -1, getBounds().height-1, hover.getRGB());
        } else {
            Gui.drawRect(1,18,getBounds().width -1, getBounds().height-1, 0xFF545454);
        }
        Gui.drawRect(0,17,getBounds().width, 18,0xFF444444);


        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        GlStateManager.pushMatrix();
        GlStateManager.translate(5,5,0);
        GlStateManager.scale(1.0,1.0,0);
        GlStateManager.enableBlend();
        GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        fr.drawString(feature.getName(), 0,0, 0xFFFFFFFF);
        GlStateManager.popMatrix();

        fr.drawSplitString(feature.getDescription(), 5, 23, getBounds().width -10, 0xFFBFBFBF);
    }

    @Override
    public void resize(int parentWidth, int parentHeight) {
        this.setSize(new Dimension(parentWidth, getBounds().height));
    }

    @Override
    public Dimension getPreferredSize() {
        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        int descriptionHeight = fr.listFormattedStringToWidth(feature.getDescription(), Math.max(100, getBounds().width - 10)).size() * fr.FONT_HEIGHT;

        return new Dimension(100, descriptionHeight + 28);
    }

    @Override
    public void onBoundsUpdate() {
        int x = getBounds().width - 5;
        for (MPanel panel : addons) {
            panel.setBounds(new Rectangle(x - panel.getPreferredSize().width, 3, panel.getPreferredSize().width, 12));
            x -= panel.getPreferredSize().width + 5;
        }
    }
}
