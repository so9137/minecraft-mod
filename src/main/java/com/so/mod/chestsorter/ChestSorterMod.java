package com.so.mod.chestsorter;


import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.Comparator;

@Mod(ChestSorterMod.MODID)
public class ChestSorterMod {
    public static final String MODID = "chestsorter";
    private static final Logger LOGGER = LogUtils.getLogger();

    public ChestSorterMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    @SubscribeEvent
    public void onPlayerOpenContainer(PlayerContainerEvent.Open event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }

        Player player = (Player) event.getEntity();
        if (player.isCrouching() && event.getContainer() instanceof ChestMenu container) {
            if (container.getContainer() instanceof RandomizableContainerBlockEntity) {
                RandomizableContainerBlockEntity chest = (RandomizableContainerBlockEntity) container.getContainer();
                sortInventory(chest);
                player.displayClientMessage(Component.literal("Inventory sorted!"), true);
            }
        }
    }

    private void sortInventory(RandomizableContainerBlockEntity chest) {
        int containerSize = chest.getContainerSize();
        ;
        ItemStack[] items = new ItemStack[containerSize];
        for (int i = 0; i < containerSize; i++) {
            items[i] = chest.getItem(i);
        }

        Arrays.sort(items, Comparator.comparingInt(this::getItemTypeOrder));

        for (int j = 0; j < chest.getContainerSize(); j++) {
            chest.setItem(j, items[j]);
        }
    }

    private int getItemTypeOrder(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return Integer.MAX_VALUE; // Empty slots go to the end
        }
        return Item.getId(itemStack.getItem()); // Use item ID for sorting
    }
}
