package com.prabhatpie.treeharvester;

import com.prabhatpie.treeharvester.config.TreeHarvesterConfig;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TreeHarvester implements ModInitializer {
    public static final String MOD_ID = "treeharvester";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static TreeHarvesterConfig CONFIG;

    @Override
    public void onInitialize() {
        CONFIG = TreeHarvesterConfig.load();
        LOGGER.info("Tree Harvester {} initialized by PrabhatPie", "1.0.0");
    }
}
