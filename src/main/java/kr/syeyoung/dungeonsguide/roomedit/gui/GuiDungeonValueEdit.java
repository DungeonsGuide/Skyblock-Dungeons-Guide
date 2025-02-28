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

package kr.syeyoung.dungeonsguide.roomedit.gui;

import kr.syeyoung.dungeonsguide.dungeon.roomfinder.DungeonRoom;
import kr.syeyoung.dungeonsguide.roomedit.EditingContext;
import kr.syeyoung.dungeonsguide.gui.MPanel;
import kr.syeyoung.dungeonsguide.roomedit.Parameter;
import kr.syeyoung.dungeonsguide.gui.elements.*;
import kr.syeyoung.dungeonsguide.roomedit.valueedit.ValueEdit;
import kr.syeyoung.dungeonsguide.roomedit.valueedit.ValueEditCreator;
import kr.syeyoung.dungeonsguide.roomedit.valueedit.ValueEditRegistry;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;
import java.io.IOException;

public class GuiDungeonValueEdit extends GuiScreen {

    private final MPanel mainPanel = new MPanel() {
        @Override
        public void onBoundsUpdate() {
            for (int i = 0; i < addons.size(); i++) {
                addons.get(i).setBounds(new Rectangle(0, getBounds().height - (i+1) * 20 - 20, getBounds().width, 20));
            }
            save.setBounds(new Rectangle(0 ,getBounds().height - 20, getBounds().width, 20));
        }
    };

    private DungeonRoom dungeonRoom;


    private MPanel currentValueEdit;

    private MButton save;

    @Getter
    private ValueEdit valueEdit;

    private List<MPanel> addons;

    private Object editingObj;

    public GuiDungeonValueEdit(final Object object, final List<MPanel> addons) {
        try {
            dungeonRoom = EditingContext.getEditingContext().getRoom();
            this.addons = addons;
            this.editingObj = object;
            mainPanel.setBackgroundColor(new Color(17, 17, 17, 179));
            {
                currentValueEdit = new MPanel() {
                    @Override
                    public void resize(int parentWidth, int parentHeight) {
                        setBounds(new Rectangle(0, 0, parentWidth, parentHeight - 20 - addons.size() * 20));
                    }
                };
                mainPanel.add(currentValueEdit);
            }

            for (MPanel addon : addons) {
                mainPanel.add(addon);
            }
            {
                save = new MButton() {
                    @Override
                    public void resize(int parentWidth, int parentHeight) {
                        setBounds(new Rectangle(0, parentHeight - 20, parentWidth, 20));
                    }
                };
                save.setText("Go back");
                save.setBackgroundColor(Color.green);
                save.setOnActionPerformed(new Runnable() {
                    @Override
                    public void run() {
                        EditingContext.getEditingContext().goBack();
                    }
                });
                mainPanel.add(save);
            }
            updateClassSelection();
        } catch (Exception e){}
    }

    public void updateClassSelection() {
        currentValueEdit.getChildComponents().clear();

        ValueEditCreator valueEditCreator = ValueEditRegistry.getValueEditMap(editingObj == null ?"null":editingObj.getClass().getName());

        MPanel valueEdit = (MPanel) valueEditCreator.createValueEdit(new Parameter("", editingObj, editingObj));
        if (valueEdit == null) {
            MLabel valueEdit2 = new MLabel() {
                @Override
                public void resize(int parentWidth, int parentHeight) {
                    setBounds(new Rectangle(0, 0, parentWidth,20));
                }
            };
            valueEdit2.setText("No Value Edit");
            valueEdit2.setBounds(new Rectangle(0,0,150,20));
            valueEdit = valueEdit2;
            this.valueEdit = null;
        } else{
            this.valueEdit = (ValueEdit) valueEdit;
        }
        valueEdit.resize0(currentValueEdit.getBounds().width, currentValueEdit.getBounds().height);
        currentValueEdit.add(valueEdit);
    }
    @Override
    public void initGui() {
        super.initGui();
        // update bounds
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        mainPanel.setBounds(new Rectangle(10, Math.min((scaledResolution.getScaledHeight() - 300) / 2, scaledResolution.getScaledHeight()),200,300));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        try {

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
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {

        try {
            super.keyTyped(typedChar, keyCode);
            mainPanel.keyTyped0(typedChar, keyCode);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        try {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        mainPanel.mouseClicked0(mouseX, mouseY,mouseX,mouseY, mouseButton);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        try {
        mainPanel.mouseReleased0(mouseX, mouseY,mouseX,mouseY, state);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        try {
            mainPanel.mouseClickMove0(mouseX,mouseY,mouseX,mouseY,clickedMouseButton,timeSinceLastClick);

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
            try {
                mainPanel.mouseScrolled0(i, j,i,j, wheel);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
}
