package top.theillusivec4.polymorph.common.integration.justenoughitems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGlobalGuiHandler;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.CraftingScreen;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.util.ResourceLocation;
import top.theillusivec4.polymorph.client.recipe.RecipeControllerHub;
import top.theillusivec4.polymorph.client.recipe.controller.AbstractRecipeController;
import top.theillusivec4.polymorph.common.PolymorphMod;

@JeiPlugin
public class PolymorphJeiPlugin implements IModPlugin {

  @Override
  public void registerGuiHandlers(IGuiHandlerRegistration registration) {
    registration.addGlobalGuiHandler(new PolymorphContainer());
  }

  @Nonnull
  @Override
  public ResourceLocation getPluginUid() {
    return new ResourceLocation(PolymorphMod.MOD_ID, "jei");
  }

  private static class PolymorphContainer implements IGlobalGuiHandler {

    @Nonnull
    @Override
    public List<Rectangle2d> getGuiExtraAreas() {
      List<Rectangle2d> list = new ArrayList<>();
      RecipeControllerHub.getController().ifPresent(recipeController -> {
        Screen screen = Minecraft.getInstance().currentScreen;

        if (screen instanceof ContainerScreen<?> && recipeController.getSelectorGui().isActive()) {
          int x = ((ContainerScreen<?>) screen).getGuiLeft() + recipeController.getXPos();
          int y = ((ContainerScreen<?>) screen).getGuiTop() + recipeController.getYPos();
          int size = recipeController.getSelectorGui().getOutputWidgets().size();
          int xOffset = (int) (-25 * Math.floor((size / 2.0F)));

          if (size % 2 == 0) {
            xOffset += 13;
          }
          x = x + AbstractRecipeController.SELECTOR_X_OFFSET + xOffset;
          y = y + AbstractRecipeController.SELECTOR_Y_OFFSET;
          list.add(new Rectangle2d(x, y, 25 * size, 25));
        }
      });
      return list;
    }
  }
}
