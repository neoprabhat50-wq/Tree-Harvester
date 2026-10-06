package com.prabhatpie.treeharvester.tree;

import com.prabhatpie.treeharvester.config.TreeHarvesterConfig;
import com.prabhatpie.treeharvester.utility.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class TreeDetector {
    private TreeDetector() {}

    public static HarvestResult detect(ServerLevel level, BlockPos origin, TreeHarvesterConfig config) {
        if (!BlockUtils.isLog(level.getBlockState(origin))) return HarvestResult.invalid("not a log");
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();
        List<BlockPos> logs = new ArrayList<>();
        queue.add(origin.immutable());
        visited.add(origin.immutable());
        final int radius = config.maxRadius;

        while (!queue.isEmpty() && logs.size() < config.maxBlocks) {
            BlockPos current = queue.removeFirst();
            if (!BlockUtils.isLog(level.getBlockState(current))) continue;
            if (Math.abs(current.getX() - origin.getX()) > radius || Math.abs(current.getZ() - origin.getZ()) > radius || Math.abs(current.getY() - origin.getY()) > radius) continue;
            logs.add(current);

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;
                        if (!config.branchDetection && dx != 0 && dz != 0) continue;
                        BlockPos next = current.offset(dx, dy, dz).immutable();
                        if (visited.add(next) && Math.abs(next.getX() - origin.getX()) <= radius && Math.abs(next.getZ() - origin.getZ()) <= radius && Math.abs(next.getY() - origin.getY()) <= radius) {
                            if (BlockUtils.isLog(level.getBlockState(next))) queue.addLast(next);
                        }
                    }
                }
            }
        }

        if (!queue.isEmpty()) return HarvestResult.invalid("block limit reached during detection");
        if (!TreeValidation.isPlausibleTree(level, origin, logs, config)) return HarvestResult.invalid("structure rejected");
        return new HarvestResult(List.copyOf(logs), true, "valid");
    }
}
