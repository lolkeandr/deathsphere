package com.example.deathsphere;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRegistry {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(DeathSphere.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(DeathSphere.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, DeathSphere.MODID);

    public static final DeferredBlock<DeathSphereBlock> DEATH_SPHERE = BLOCKS.registerBlock(
            "death_sphere",
            DeathSphereBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(2.0F, 3600000.0F)   // почти не взрывается
                    .noOcclusion()
                    .lightLevel(state -> 8));

    // Предмета в креативе и рецепта нет: сфера появляется только после смерти игрока
    public static final DeferredItem<BlockItem> DEATH_SPHERE_ITEM =
            ITEMS.registerSimpleBlockItem("death_sphere", DEATH_SPHERE);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DeathSphereBlockEntity>> DEATH_SPHERE_BE =
            BLOCK_ENTITIES.register("death_sphere",
                    () -> BlockEntityType.Builder.of(DeathSphereBlockEntity::new, DEATH_SPHERE.get()).build(null));
}
