package com.prabhatpie.treeharvester.tree;

import net.minecraft.core.BlockPos;
import java.util.List;

public record HarvestResult(List<BlockPos> logs, boolean validTree, String reason) {
    public static HarvestResult invalid(String reason) { return new HarvestResult(List.of(), false, reason); }
}
