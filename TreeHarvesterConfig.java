package com.prabhatpie.treeharvester.config;

import com.prabhatpie.treeharvester.TreeHarvester;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class TreeHarvesterConfig {
    public boolean enabled = true;
    public boolean requireAxe = true;
    public int maxBlocks = 128;
    public int maxRadius = 16;
    public boolean harvestLeaves = false;
    public boolean instantLeafDecay = true;
    public boolean creativeMode = false;
    public boolean sneakToDisable = true;
    public boolean sneakToHarvest = false;
    public boolean enableParticles = true;
    public boolean enableSound = true;
    public int minimumTrunkHeight = 2;
    public int minimumTrunkLogs = 2;
    public boolean branchDetection = true;

    private static final String FILE_NAME = "treeharvester.properties";

    public static TreeHarvesterConfig load() {
        TreeHarvesterConfig config = new TreeHarvesterConfig();
        Path path = Path.of("config", FILE_NAME);
        Properties p = new Properties();
        try {
            Files.createDirectories(path.getParent());
            if (Files.exists(path)) {
                try (InputStream in = Files.newInputStream(path)) { p.load(in); }
                config.read(p);
            } else {
                config.write(p);
                try (OutputStream out = Files.newOutputStream(path)) { p.store(out, "Tree Harvester configuration - PrabhatPie"); }
            }
        } catch (IOException | RuntimeException e) {
            TreeHarvester.LOGGER.warn("Could not load config; using safe defaults", e);
        }
        config.sanitize();
        return config;
    }

    private void read(Properties p) {
        enabled = bool(p, "enabled", enabled);
        requireAxe = bool(p, "requireAxe", requireAxe);
        maxBlocks = integer(p, "maxBlocks", maxBlocks);
        maxRadius = integer(p, "maxRadius", maxRadius);
        harvestLeaves = bool(p, "harvestLeaves", harvestLeaves);
        instantLeafDecay = bool(p, "instantLeafDecay", instantLeafDecay);
        creativeMode = bool(p, "creativeMode", creativeMode);
        sneakToDisable = bool(p, "sneakToDisable", sneakToDisable);
        sneakToHarvest = bool(p, "sneakToHarvest", sneakToHarvest);
        enableParticles = bool(p, "enableParticles", enableParticles);
        enableSound = bool(p, "enableSound", enableSound);
        minimumTrunkHeight = integer(p, "minimumTrunkHeight", minimumTrunkHeight);
        minimumTrunkLogs = integer(p, "minimumTrunkLogs", minimumTrunkLogs);
        branchDetection = bool(p, "branchDetection", branchDetection);
    }

    private void write(Properties p) {
        p.setProperty("enabled", Boolean.toString(enabled));
        p.setProperty("requireAxe", Boolean.toString(requireAxe));
        p.setProperty("maxBlocks", Integer.toString(maxBlocks));
        p.setProperty("maxRadius", Integer.toString(maxRadius));
        p.setProperty("harvestLeaves", Boolean.toString(harvestLeaves));
        p.setProperty("instantLeafDecay", Boolean.toString(instantLeafDecay));
        p.setProperty("creativeMode", Boolean.toString(creativeMode));
        p.setProperty("sneakToDisable", Boolean.toString(sneakToDisable));
        p.setProperty("sneakToHarvest", Boolean.toString(sneakToHarvest));
        p.setProperty("enableParticles", Boolean.toString(enableParticles));
        p.setProperty("enableSound", Boolean.toString(enableSound));
        p.setProperty("minimumTrunkHeight", Integer.toString(minimumTrunkHeight));
        p.setProperty("minimumTrunkLogs", Integer.toString(minimumTrunkLogs));
        p.setProperty("branchDetection", Boolean.toString(branchDetection));
    }

    private void sanitize() {
        maxBlocks = Math.max(1, Math.min(maxBlocks, 4096));
        maxRadius = Math.max(2, Math.min(maxRadius, 64));
        minimumTrunkHeight = Math.max(1, Math.min(minimumTrunkHeight, 64));
        minimumTrunkLogs = Math.max(1, Math.min(minimumTrunkLogs, 64));
    }

    private static boolean bool(Properties p, String k, boolean d) {
        String v = p.getProperty(k); return v == null ? d : Boolean.parseBoolean(v);
    }
    private static int integer(Properties p, String k, int d) {
        try { return Integer.parseInt(p.getProperty(k, Integer.toString(d)).trim()); }
        catch (NumberFormatException e) { return d; }
    }
}
