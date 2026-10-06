package com.prabhatpie.treeharvester.tree;

import com.prabhatpie.treeharvester.config.TreeHarvesterConfig;
import com.prabhatpie.treeharvester.utility.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import java.util.List;

public final class TreeValidation {
    private TreeValidation() {}

    public static boolean isPlausibleTree(ServerLevel level, BlockPos origin, List<BlockPos> logs, TreeHarvesterConfig config) {
        if (logs.size() < config.minimumTrunkLogs) return false;
        int minY = logs.stream().mapToInt(BlockPos::getY).min().orElse(origin.getY());
        int maxY = logs.stream().mapToInt(BlockPos::getY).max().orElse(origin.getY());
        if (maxY - minY + 1 < config.minimumTrunkHeight) return false;

        long baseLogs = logs.stream().filter(p -> p.getY() == minY).count();
        if (baseLogs == 0 || baseLogs > 16) return false;

        boolean hasVerticalTrunk = false;
        for (BlockPos p : logs) {
            if (p.getY() != minY) continue;
            for (int y = minY; y <= maxY; y++) {
                if (BlockUtils.isLog(level.getBlockState(new BlockPos(p.getX(), y, p.getZ())))) {
                    hasVerticalTrunk = true;
                    break;
                }
            }
            if (hasVerticalTrunk) break;
        }
        if (!hasVerticalTrunk) return false;

        BlockPos ground = new BlockPos(logs.getFirst().getX(), minY - 1, logs.getFirst().getZ());
        BlockState below = level.getBlockState(ground);
        return !BlockUtils.isLog(below) && !below.isAir();
    }
}
