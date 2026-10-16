package com.example.deathsphere;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = DeathSphere.MODID)
public class DeathHandler {
    private static final int SEARCH_RADIUS = 4;

    @SubscribeEvent
    public static void onPlayerDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        List<ItemStack> stacks = new ArrayList<>();
        for (ItemEntity drop : event.getDrops()) {
            if (!drop.getItem().isEmpty()) stacks.add(drop.getItem().copy());
        }
        if (stacks.isEmpty()) return;

        ServerLevel level = player.serverLevel();
        BlockPos spot = findSpot(level, player.blockPosition());
        if (spot == null) return; // места нет — вещи выпадут как обычно

        level.setBlock(spot, ModRegistry.DEATH_SPHERE.get().defaultBlockState(), 3);
        if (!(level.getBlockEntity(spot) instanceof DeathSphereBlockEntity sphere)) return;

        List<ItemStack> overflow = sphere.fill(stacks);
        event.setCanceled(true); // обычный дроп отменяем только после успешного создания сферы

        for (ItemStack extra : overflow) { // если вещей больше 54 стаков
            level.addFreshEntity(new ItemEntity(level, spot.getX() + 0.5, spot.getY() + 0.5, spot.getZ() + 0.5, extra));
        }

        player.sendSystemMessage(Component.translatable("message.deathsphere.created",
                spot.getX(), spot.getY(), spot.getZ()));
    }

    /** Ближайшая к месту смерти свободная клетка (воздух, вода, трава и т.п.). */
    private static BlockPos findSpot(ServerLevel level, BlockPos origin) {
        int y = Math.max(level.getMinBuildHeight(), Math.min(level.getMaxBuildHeight() - 1, origin.getY()));
        BlockPos base = new BlockPos(origin.getX(), y, origin.getZ());

        BlockPos best = null;
        int bestDist = Integer.MAX_VALUE;
        for (int dx = -SEARCH_RADIUS; dx <= SEARCH_RADIUS; dx++) {
            for (int dy = -SEARCH_RADIUS; dy <= SEARCH_RADIUS; dy++) {
                for (int dz = -SEARCH_RADIUS; dz <= SEARCH_RADIUS; dz++) {
                    int dist = dx * dx + dy * dy + dz * dz;
                    if (dist >= bestDist) continue;
                    BlockPos pos = base.offset(dx, dy, dz);
                    if (!level.isInWorldBounds(pos)) continue;
                    BlockState state = level.getBlockState(pos);
                    if ((state.isAir() || state.canBeReplaced()) && level.getBlockEntity(pos) == null) {
                        best = pos;
                        bestDist = dist;
                    }
                }
            }
        }
        return best;
    }
}
