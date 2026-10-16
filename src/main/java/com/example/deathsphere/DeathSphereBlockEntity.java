package com.example.deathsphere;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class DeathSphereBlockEntity extends RandomizableContainerBlockEntity {
    public static final int SIZE = 54; // 6 рядов
    private NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);

    public DeathSphereBlockEntity(BlockPos pos, BlockState state) {
        super(ModRegistry.DEATH_SPHERE_BE.get(), pos, state);
    }

    /** Кладёт предметы в хранилище. Возвращает то, что не поместилось. */
    public List<ItemStack> fill(List<ItemStack> stacks) {
        List<ItemStack> overflow = new ArrayList<>();
        int slot = 0;
        for (ItemStack stack : stacks) {
            if (stack.isEmpty()) continue;
            if (slot < SIZE) {
                items.set(slot++, stack);
            } else {
                overflow.add(stack);
            }
        }
        setChanged();
        return overflow;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null && !level.isClientSide && isEmpty()) {
            level.scheduleTick(getBlockPos(), getBlockState().getBlock(), 1);
        }
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.deathsphere.death_sphere");
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return ChestMenu.sixRows(id, inventory, this);
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public int getContainerSize() {
        return SIZE;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        if (!tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, items, registries);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, items, registries);
        }
    }
}
