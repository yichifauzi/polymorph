package top.theillusivec4.polymorph.common.capability;

import com.mojang.datafixers.util.Pair;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.IRecipePair;
import top.theillusivec4.polymorph.api.common.capability.IPlayerRecipeData;
import top.theillusivec4.polymorph.client.recipe.RecipesWidget;

public class PlayerRecipeData extends AbstractRecipeData<PlayerEntity> implements
    IPlayerRecipeData {

  public PlayerRecipeData(PlayerEntity pOwner) {
    super(pOwner);
  }

  @Override
  public <T extends IRecipe<C>, C extends IInventory> Optional<T> getRecipe(IRecipeType<T> pType,
                                                                            C pInventory,
                                                                            World pWorld,
                                                                            List<T> pRecipes) {
    Optional<T> maybeRecipe = super.getRecipe(pType, pInventory, pWorld, pRecipes);
//
//    if (this.getOwner().world.isRemote()) {
//      Pair<SortedSet<IRecipePair>, ResourceLocation> data = this.getPacketData();
//      RecipesWidget.get()
//          .ifPresent(widget -> widget.setRecipesList(data.getFirst(), data.getSecond()));
//    }
    this.syncPlayerRecipeData();
    return maybeRecipe;
  }

  @Override
  public void selectRecipe(@Nonnull IRecipe<?> pRecipe) {
    super.selectRecipe(pRecipe);
    this.syncPlayerRecipeData();
  }

  private void syncPlayerRecipeData() {

    if (this.getOwner() instanceof ServerPlayerEntity) {
      PolymorphApi.common().getPacketDistributor()
          .sendPlayerSyncS2C((ServerPlayerEntity) this.getOwner(), this.getRecipesList(),
              this.getSelectedRecipe().map(IRecipe::getId).orElse(null));
    }
  }

  @Override
  public Set<ServerPlayerEntity> getListeners() {
    PlayerEntity player = this.getOwner();

    if (player instanceof ServerPlayerEntity) {
      return Collections.singleton((ServerPlayerEntity) player);
    } else {
      return new HashSet<>();
    }
  }
}
