/*
 * Copyright (c) 2020 C4
 *
 * This file is part of Polymorph, a mod made for Minecraft.
 *
 * Polymorph is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * Polymorph is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Polymorph.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.polymorph.mixin;

import java.util.List;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class IntegratedMixinPlugin implements IMixinConfigPlugin {

  @Override
  public void onLoad(String mixinPackage) {

  }

  @Override
  public String getRefMapperConfig() {
    return null;
  }

  @Override
  public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
    FabricLoader loader = FabricLoader.getInstance();

    if (targetClassName.equals("me.shedaniel.rei.impl.InternalWidgets") &&
        mixinClassName.equals(
            "top.theillusivec4.polymorph.mixin.integration.MixinRoughlyEnoughItems")) {
      return loader.isModLoaded("roughlyenoughitems-runtime");
    } else if (
        targetClassName.equals("me.shedaniel.istations.containers.CraftingStationScreenHandler") &&
            mixinClassName.equals(
                "top.theillusivec4.polymorph.mixin.integration.MixinImprovedStations")) {
      return loader.isModLoaded("improved-stations");
    } else if ((targetClassName.equals("appeng.container.me.items.CraftingTermContainer") ||
        (targetClassName.equals("appeng.container.me.items.PatternTermContainer"))) &&
        (mixinClassName.equals(
            "top.theillusivec4.polymorph.mixin.integration.MixinCraftingTermContainer") ||
            mixinClassName.equals(
                "top.theillusivec4.polymorph.mixin.integration.AccessorCraftingTermContainer") ||
            mixinClassName.equals(
                "top.theillusivec4.polymorph.mixin.integration.MixinPatternTermContainer") ||
            mixinClassName.equals(
                "top.theillusivec4.polymorph.mixin.integration.AccessorPatternTermContainer"))) {
      return loader.isModLoaded("appliedenergistics2");
    }
    return true;
  }

  @Override
  public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

  }

  @Override
  public List<String> getMixins() {
    return null;
  }

  @Override
  public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName,
                       IMixinInfo mixinInfo) {

  }

  @Override
  public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName,
                        IMixinInfo mixinInfo) {

  }
}