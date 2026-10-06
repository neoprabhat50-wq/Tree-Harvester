package com.prabhatpie.treeharvester.tree;

import com.prabhatpie.treeharvester.TreeHarvester;
import com.prabhatpie.treeharvester.config.TreeHarvesterConfig;
import com.prabhatpie.treeharvester.utility.ToolUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import java.util.HashSet;
import java.util.Set;

public final class TreeHarvesterService {
    private static final ThreadLocal<Set<ServerPlayer>> ACTIVE = ThreadLocal.withInitial(HashSet::new);
    private TreeHarvesterService() {}

    public static void onLogBroken(ServerPlayer player, BlockPos origin) {
        TreeHarvesterConfig config = TreeHarvester.CONFIG;
        if (config == null || !config.enabled || player.level().isClientSide()) return;
        if (!config.creativeMode && player.isCreative()) return;
        if (config.sneakToDisable && player.isShiftKeyDown() && !config.sneakToHarvest) return;
        if (config.sneakToHarvest && !player.isShiftKeyDown()) return;
        if (ACTIVE.get().contains(player)) return;

        ItemStack tool = player.getMainHandItem();
        if (config.requireAxe && !ToolUtils.isAxe(tool)) return;
        if (!(player.level() instanceof ServerLevel level)) return;

        HarvestResult result = TreeDetector.detect(level, origin, config);
        if (!result.validTree() || result.logs().size() <= 1) return;

        Set<ServerPlayer> active = ACTIVE.get();
        active.add(player);
        try {
            int harvested = 0;
            for (BlockPos pos : result.logs()) {
                if (pos.equals(origin)) continue;
                if (!ToolUtils.isAxe(player.getMainHandItem()) && config.requireAxe) break;
                if (!level.isLoaded(pos)) continue;
                if (!player.gameMode.destroyBlock(pos)) continue;
                harvested++;
                if (harvested >= config.maxBlocks - 1) break;
            }
            if (config.instantLeafDecay && !config.harvestLeaves && harvested > 0) removeNearbyLeaves(level, result.logs(), config.maxRadius);
            if (config.enableSound && harvested > 0) level.playSound(null, origin, SoundEvents.WOOD_BREAK, player.getSoundSource(), 0.7f, 1.0f);
        } finally {
            active.remove(player);
            if (active.isEmpty()) ACTIVE.remove();
        }
    }

    private static void removeNearbyLeaves(ServerLevel level, java.util.List<BlockPos> logs, int radius) {
        Set<BlockPos> candidates = new HashSet<>();
        for (BlockPos log : logs) {
            for (int dx = -2; dx <= 2; dx++) for (int dy = -2; dy <= 2; dy++) for (int dz = -2; dz <= 2; dz++) {
                BlockPos p = log.offset(dx, dy, dz);
                if (p.distSqr(log) <= 9 && level.getBlockState(p).is(net.minecraft.tags.BlockTags.LEAVES)) candidates.add(p);
            }
        }
        for (BlockPos p : candidates) {
            if (level.getBlockState(p).is(net.minecraft.tags.BlockTags.LEAVES)) level.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
        }
    }
}
