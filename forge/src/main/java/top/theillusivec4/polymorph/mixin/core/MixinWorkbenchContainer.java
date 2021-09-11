package top.theillusivec4.polymorph.mixin.core;

import java.util.Optional;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.theillusivec4.polymorph.mixin.util.MixinHooks;

@Mixin(WorkbenchContainer.class)
public class MixinWorkbenchContainer {

  @Redirect(at = @At(value = "INVOKE", target = "net/minecraft/item/crafting/RecipeManager.getRecipe(Lnet/minecraft/item/crafting/IRecipeType;Lnet/minecraft/inventory/IInventory;Lnet/minecraft/world/World;)Ljava/util/Optional;"), method = "updateCraftingResult")
  private static <C extends IInventory, T extends IRecipe<C>> Optional<T> polymorph$getRecipe(
      RecipeManager recipeManager, IRecipeType<T> type, C inventory, World world, int syncId,
      World unused, PlayerEntity player, CraftingInventory craftingInventory,
      CraftResultInventory resultInventory) {
    return MixinHooks.getRecipe(recipeManager, type, inventory, world, player);
  }
}