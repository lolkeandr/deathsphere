package com.example.deathsphere;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DeathSphereBlock extends BaseEntityBlock {
    public static final MapCodec<DeathSphereBlock> CODEC = simpleCodec(DeathSphereBlock::new);
    private static final VoxelShape SHAPE = Block.box(2, 2, 2, 14, 14, 14);

    public DeathSphereBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DeathSphereBlockEntity(pos, state);
    }

    // BaseEntityBlock по умолчанию делает блок невидимым
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof MenuProvider provider) {
            player.openMenu(provider);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    /** Сфера исчезает, когда хранилище опустело (проверка отложена на 1 тик, чтобы не ломать блок посреди операции со слотом). */
    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getBlockEntity(pos) instanceof DeathSphereBlockEntity be && be.isEmpty()) {
            level.destroyBlock(pos, false);
        }
    }

    /** Если сферу всё-таки сломали, содержимое выпадает, а не пропадает. */
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof Container container) {
                Containers.dropContents(level, pos, container);
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }
}
