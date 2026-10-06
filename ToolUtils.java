package com.prabhatpie.treeharvester.utility;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;

public final class ToolUtils {
    private ToolUtils() {}
    public static boolean isAxe(ItemStack stack) { return !stack.isEmpty() && stack.getItem() instanceof AxeItem; }
}
