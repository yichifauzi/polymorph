/*
 * Copyright (c) 2020 C4
 *
 * This file is part of Polymorph, a mod made for Minecraft.
 *
 * Polymorph is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Polymorph is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Polymorph.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.polymorph.loader.network;

import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.core.Polymorph;

public class NetworkHandler {

  public static void setup() {
    ServerSidePacketRegistry.INSTANCE
        .register(NetworkPackets.SET_RECIPE, ((packetContext, packetByteBuf) -> {
          String id = packetByteBuf.readString(32767);
          packetContext.getTaskQueue().execute(() -> {
            PlayerEntity player = packetContext.getPlayer();

            if (player != null && player.getServer() != null) {
              ScreenHandler container = player.currentScreenHandler;
              AtomicReference<ItemStack> output = new AtomicReference<>(ItemStack.EMPTY);
              PolymorphApi.getProvider(container).ifPresent(provider -> {
                Slot slot = provider.getOutputSlot();
                Optional<? extends Recipe<?>> result = player.getServer().getRecipeManager()
                    .get(new Identifier(id));
                CraftingInventory craftingInventory = provider.getCraftingInventory();
                result.ifPresent(res -> {

                  if (res instanceof CraftingRecipe && craftingInventory != null) {
                    CraftingRecipe craftingRecipe = (CraftingRecipe) res;

                    if (craftingRecipe.matches(craftingInventory, player.world)) {
                      output.set(craftingRecipe.craft(craftingInventory));
                      slot.inventory.setStack(slot.id, output.get());
                    }
                  }
                });
              });
              PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
              buf.writeItemStack(output.get());
              ServerSidePacketRegistry.INSTANCE
                  .sendToPlayer(player, NetworkPackets.SYNC_OUTPUT, buf);
            }
          });
        }));

    ServerSidePacketRegistry.INSTANCE
        .register(NetworkPackets.TRANSFER_RECIPE, (((packetContext, packetByteBuf) -> {
          String id = packetByteBuf.readString(32767);
          packetContext.getTaskQueue().execute(() -> {
            PlayerEntity player = packetContext.getPlayer();

            if (player != null && player.getServer() != null) {
              ScreenHandler screenHandler = player.currentScreenHandler;
              PolymorphApi.getProvider(screenHandler).ifPresent(provider -> {
                Optional<? extends Recipe<?>> result = player.getServer().getRecipeManager()
                    .get(new Identifier(id));
                result.ifPresent(res -> {

                  if (res instanceof CraftingRecipe) {
                    CraftingRecipe craftingRecipe = (CraftingRecipe) res;
                    provider.transfer(player, craftingRecipe);
                  }
                });
              });
              PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
              buf.writeItemStack(ItemStack.EMPTY);
              ServerSidePacketRegistry.INSTANCE
                  .sendToPlayer(player, NetworkPackets.SYNC_OUTPUT, buf);
            }
          });
        })));

    ServerSidePacketRegistry.INSTANCE.register(NetworkPackets.FETCH_RECIPES,
        (((packetContext, packetByteBuf) -> packetContext.getTaskQueue().execute(() -> {
          PlayerEntity player = packetContext.getPlayer();

          if (player != null && player.getServer() != null) {
            ScreenHandler screenHandler = player.currentScreenHandler;
            List<String> recipes = PolymorphApi.getProvider(screenHandler).map(provider -> {
              CraftingInventory craftingInventory = provider.getCraftingInventory();
              List<CraftingRecipe> result = player.getServer().getRecipeManager()
                  .getAllMatches(RecipeType.CRAFTING, craftingInventory, player.getEntityWorld());
              return result.stream().map(recipe -> recipe.getId().toString())
                  .collect(Collectors.toList());
            }).orElse(new ArrayList<>());
            Polymorph.getLoader().getPacketVendor().sendRecipes(recipes, player);
          }
        }))));
  }

}
