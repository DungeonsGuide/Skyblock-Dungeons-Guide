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
import kr.syeyoung.dungeonsguide.SkyblockStatus;
import kr.syeyoung.dungeonsguide.features.FeatureParameter;
import kr.syeyoung.dungeonsguide.features.SimpleFeature;
import kr.syeyoung.dungeonsguide.features.listener.EntityLivingRenderListener;
import kr.syeyoung.dungeonsguide.roomprocessor.bossfight.BossfightProcessorThorn;
import net.minecraft.entity.passive.*;
import net.minecraftforge.client.event.RenderLivingEvent;


public class FeatureHideAnimals extends SimpleFeature implements EntityLivingRenderListener {
    public FeatureHideAnimals() {
        super("Bossfight", "Hide animals on f4", "Hide Spirit Animals on F4. \nClick on Edit for precise setting", "bossfight.hideanimals", false);
        parameters.put("sheep", new FeatureParameter<Boolean>("sheep", "Hide Sheeps", "Hide Sheeps", true, "boolean"));
        parameters.put("cow", new FeatureParameter<Boolean>("cow", "Hide Cows", "Hide Cows", true, "boolean"));
        parameters.put("chicken", new FeatureParameter<Boolean>("chicken", "Hide Chickens", "Hide Chickens", true, "boolean"));
        parameters.put("wolf", new FeatureParameter<Boolean>("wolf", "Hide Wolves", "Hide Wolves", true, "boolean"));
        parameters.put("rabbit", new FeatureParameter<Boolean>("rabbit", "Hide Rabbits", "Hide Rabbits", true, "boolean"));
    }


    private final SkyblockStatus skyblockStatus = DungeonsGuide.getDungeonsGuide().getSkyblockStatus();

    @Override
    public void onEntityRenderPre(RenderLivingEvent.Pre renderPlayerEvent) {
        if (!isEnabled()) return;
        if (!skyblockStatus.isOnDungeon()) return;
        if (skyblockStatus.getContext() == null) return;
        if (skyblockStatus.getContext().getBossfightProcessor() == null) return;
        if (!(skyblockStatus.getContext().getBossfightProcessor() instanceof BossfightProcessorThorn)) return;

        if (renderPlayerEvent.entity instanceof EntitySheep && this.<Boolean>getParameter("sheep").getValue()) {
            renderPlayerEvent.setCanceled(true);
        } else if (renderPlayerEvent.entity instanceof EntityCow && this.<Boolean>getParameter("cow").getValue() ) {
            renderPlayerEvent.setCanceled(true);
        } else if (renderPlayerEvent.entity instanceof EntityChicken && this.<Boolean>getParameter("chicken").getValue()) {
            renderPlayerEvent.setCanceled(true);
        } else if (renderPlayerEvent.entity instanceof EntityWolf && this.<Boolean>getParameter("wolf").getValue()) {
            renderPlayerEvent.setCanceled(true);
        } else if (renderPlayerEvent.entity instanceof EntityRabbit && this.<Boolean>getParameter("rabbit").getValue()) {
            renderPlayerEvent.setCanceled(true);
        }
    }

    @Override
    public void onEntityRenderPost(RenderLivingEvent.Post renderPlayerEvent) {

    }
}
