package dansplugins.democracy.services;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pins the way the usage-reporting block reaches an upgraded server's {@code config.yml}.
 *
 * <p>{@code Democracy.onEnable()} cannot run outside a server, so the two facts it relies on are
 * measured here against the same {@link YamlConfiguration} Bukkit hands the plugin: an on-disk
 * file that predates the block, with the jar's {@code config.yml} registered as its defaults,
 * exactly as {@code JavaPlugin.reloadConfig()} sets it up. First, {@code isSet("usage-reporting")}
 * is {@code false} for such a file even though the defaults carry the block -- which is what makes
 * it usable as the "write the block out" trigger. Second, {@code copyDefaults(true)} followed by a
 * save (what {@link ConfigService#saveMissingConfigDefaultsIfNotPresent()} does) is what writes the
 * block, with the bundled values, into the file.
 */
class UsageReportingDefaultsTest {

    private YamlConfiguration bundled;

    @BeforeEach
    void loadBundledConfig() {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("config.yml");
        assertNotNull(stream, "config.yml is missing from the jar's resources");
        bundled = YamlConfiguration.loadConfiguration(new InputStreamReader(stream, StandardCharsets.UTF_8));
    }

    @Test
    void bundledConfigCarriesTheUsageReportingBlock() {
        assertTrue(bundled.getBoolean("usage-reporting.enabled"));
        assertEquals("https://trace.danielstephenson.dev", bundled.getString("usage-reporting.endpoint"));
        String key = bundled.getString("usage-reporting.key");
        assertNotNull(key);
        assertFalse(key.isEmpty(), "the bundled key is empty, which turns reporting off everywhere");
    }

    @Test
    void anOlderConfigDoesNotCountTheDefaultsAsTheBlockBeingOnDisk() {
        YamlConfiguration onDisk = new YamlConfiguration();
        onDisk.set("version", "v0.1");
        onDisk.set("debugMode", false);
        onDisk.setDefaults(bundled);

        assertFalse(onDisk.isSet("usage-reporting"), "the on-enable write would never trigger");
        // ...while the one-argument getters still read through to the jar
        assertTrue(onDisk.getBoolean("usage-reporting.enabled"));
        assertEquals(bundled.getString("usage-reporting.key"), onDisk.getString("usage-reporting.key"));
    }

    @Test
    void copyingTheDefaultsWritesTheBlockWithTheBundledValues() {
        YamlConfiguration onDisk = new YamlConfiguration();
        onDisk.set("version", "v0.1");
        onDisk.setDefaults(bundled);
        onDisk.options().copyDefaults(true);

        YamlConfiguration written = new YamlConfiguration();
        assertTrue(written.getKeys(false).isEmpty());
        String saved = onDisk.saveToString();
        try {
            written.loadFromString(saved);
        } catch (org.bukkit.configuration.InvalidConfigurationException e) {
            throw new AssertionError("saved config.yml does not parse:\n" + saved, e);
        }

        assertTrue(written.isSet("usage-reporting"));
        assertEquals(true, written.get("usage-reporting.enabled", null));
        assertEquals(bundled.getString("usage-reporting.endpoint"), written.get("usage-reporting.endpoint", null));
        assertEquals(bundled.getString("usage-reporting.key"), written.get("usage-reporting.key", null));
        assertEquals("v0.1", written.getString("version"), "the existing keys survive the write");
    }

    @Test
    void anExplicitOptOutOnDiskWinsOverTheBundledDefault() {
        YamlConfiguration onDisk = new YamlConfiguration();
        onDisk.set("usage-reporting.enabled", false);
        onDisk.setDefaults(bundled);

        assertTrue(onDisk.isSet("usage-reporting"), "a file that has the block is left alone");
        assertFalse(onDisk.getBoolean("usage-reporting.enabled"));
    }
}
