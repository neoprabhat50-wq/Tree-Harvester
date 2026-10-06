package com.prabhatpie.treeharvester.utility;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;

public final class BlockUtils {
    private BlockUtils() {}

    public static boolean isLog(BlockState state) {
        return state.is(BlockTags.LOGS);
    }

    public static boolean isLeaf(BlockState state) {
        return state.is(BlockTags.LEAVES);
    }
}
