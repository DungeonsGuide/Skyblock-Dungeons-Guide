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

package kr.syeyoung.dungeonsguide.features.impl.boss;

import kr.syeyoung.dungeonsguide.DungeonsGuide;
import kr.syeyoung.dungeonsguide.features.SimpleFeature;
import kr.syeyoung.dungeonsguide.features.listener.DungeonQuitListener;

public class FeatureAutoReparty extends SimpleFeature implements DungeonQuitListener {
    public FeatureAutoReparty() {
        super("Bossfight", "Auto reparty when dungeon finishes","Auto reparty on dungeon finish\n\nThis automates player chatting action, (disbanding, repartying) Thus it might be against hypixel's rules.\nBut mods like auto-gg exist so I'm leaving this feature.\nThis option is use-at-your-risk and you'll be responsible for ban if you somehow get banned because of this feature\n(Although it is not likely to happen)\nDefaults to off", "bossfight.autoreparty", false);
    }

    @Override
    public void onDungeonQuit() {
        if (isEnabled()) DungeonsGuide.getDungeonsGuide().getCommandReparty().requestReparty();
    }
}
