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

package kr.syeyoung.dungeonsguide.features.impl.party.playerpreview;

import kr.syeyoung.dungeonsguide.config.guiconfig.FeatureEditPane;
import kr.syeyoung.dungeonsguide.features.impl.party.api.ClassSpecificData;
import kr.syeyoung.dungeonsguide.features.impl.party.api.DungeonClass;
import kr.syeyoung.dungeonsguide.features.impl.party.api.PlayerProfile;
import kr.syeyoung.dungeonsguide.utils.RenderUtils;
import kr.syeyoung.dungeonsguide.utils.TextUtils;
import kr.syeyoung.dungeonsguide.utils.XPUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import java.awt.*;
import java.util.Arrays;

public class DataRendererSelectedClassLv implements DataRenderer {
    @Override
    public Dimension renderData(PlayerProfile playerProfile) {
        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        ClassSpecificData<PlayerProfile.PlayerClassData> dungeonStatDungeonSpecificData = playerProfile.getPlayerClassData().get(playerProfile.getSelectedClass());
        if (dungeonStatDungeonSpecificData == null) {
            fr.drawString("Unknown Selected", 0,0, 0xFF55ffff);
        } else {
            XPUtils.XPCalcResult xpCalcResult = XPUtils.getCataXp(dungeonStatDungeonSpecificData.getData().getExperience());
            fr.drawString(playerProfile.getSelectedClass().getFamilarName(), 0,0, 0xFF55ffff);
            fr.drawString(xpCalcResult.getLevel()+"", fr.getStringWidth(playerProfile.getSelectedClass().getFamilarName()+" "),0,0xFFFFFFFF);
            fr.drawString("★", fr.getStringWidth(playerProfile.getSelectedClass().getFamilarName()+" "+xpCalcResult.getLevel()+" "),0,0xFFAAAAAA);

            RenderUtils.renderBar(0, fr.FONT_HEIGHT, 100,xpCalcResult.getRemainingXp() == 0 ? 1 : (float) (xpCalcResult.getNextLvXp() / xpCalcResult.getRemainingXp()));
        }

        return new Dimension(100, fr.FONT_HEIGHT*2);
    }

    @Override
    public Dimension renderDummy() {
        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        fr.drawString("SelectedClass", 0,0, 0xFF55ffff);
        fr.drawString("99", fr.getStringWidth("SelectedClass "),0,0xFFFFFFFF);
        fr.drawString("★", fr.getStringWidth("SelectedClass 99 "),0,0xFFAAAAAA);
        RenderUtils.renderBar(0, fr.FONT_HEIGHT, 100,1.0f);
        return new Dimension(100, fr.FONT_HEIGHT*2);
    }
    @Override
    public Dimension getDimension() {
        return new Dimension(100, Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT*2);
    }

    @Override
    public void onHover(PlayerProfile playerProfile, int mouseX, int mouseY) {
        ClassSpecificData<PlayerProfile.PlayerClassData> dungeonStatDungeonSpecificData = playerProfile.getPlayerClassData().get(playerProfile.getSelectedClass());
        if (dungeonStatDungeonSpecificData == null) return;
        XPUtils.XPCalcResult xpCalcResult = XPUtils.getCataXp(dungeonStatDungeonSpecificData.getData().getExperience());
        RenderUtils.drawHoveringText(Arrays.asList("§bCurrent Lv§7: §e"+xpCalcResult.getLevel(),"§bExp§7: §e"+ TextUtils.format((long)xpCalcResult.getRemainingXp()) + "§7/§e"+TextUtils.format((long)xpCalcResult.getNextLvXp()), "§bTotal Xp§7: §e"+ TextUtils.format((long)dungeonStatDungeonSpecificData.getData().getExperience())),mouseX, mouseY, Minecraft.getMinecraft().fontRendererObj);
    }
}
