package dansplugins.democracy.services;

import dansplugins.democracy.Democracy;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
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
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConfigServiceTest {
    private ConfigService configService;
    private FileConfiguration config;
    private CommandSender sender;
    private Democracy democracy;

    @BeforeEach
    void setUp() {
        config = mock(FileConfiguration.class);
        sender = mock(CommandSender.class);

        democracy = mock(Democracy.class);
        when(democracy.getConfig()).thenReturn(config);

        configService = new ConfigService(democracy);
    }

    /**
     * Runs {@link ConfigService#saveMissingConfigDefaultsIfNotPresent()} against a real
     * {@link YamlConfiguration} and returns what it would write to disk, read back in.
     */
    private YamlConfiguration saveDefaultsAndReadBack(YamlConfiguration onDisk) throws InvalidConfigurationException {
        when(democracy.getConfig()).thenReturn(onDisk);
        when(democracy.getVersion()).thenReturn("v0.4.1");

        configService.saveMissingConfigDefaultsIfNotPresent();

        verify(democracy).saveConfig();
        YamlConfiguration written = new YamlConfiguration();
        written.loadFromString(onDisk.saveToString());
        return written;
    }

    @Test
    void saveDefaults_writesTheVersionAndDebugModeOffIntoAnEmptyConfig() throws InvalidConfigurationException {
        YamlConfiguration written = saveDefaultsAndReadBack(new YamlConfiguration());

        assertEquals("v0.4.1", written.get("version", null));
        assertEquals(false, written.get("debugMode", null));
    }

    @Test
    void saveDefaults_replacesAnOlderVersion() throws InvalidConfigurationException {
        YamlConfiguration onDisk = new YamlConfiguration();
        onDisk.set("version", "v0.1");

        YamlConfiguration written = saveDefaultsAndReadBack(onDisk);

        assertEquals("v0.4.1", written.get("version", null));
    }

    @Test
    void saveDefaults_keepsDebugModeWhenItIsAlreadySet() throws InvalidConfigurationException {
        YamlConfiguration onDisk = new YamlConfiguration();
        onDisk.set("version", "v0.4.1");
        onDisk.set("debugMode", true);

        YamlConfiguration written = saveDefaultsAndReadBack(onDisk);

        assertEquals(true, written.get("debugMode", null));
    }

    @Test
    void saveDefaults_writesTheBundledUsageReportingBlockAndKeepsExistingKeys() throws InvalidConfigurationException {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("config.yml");
        assertNotNull(stream, "config.yml is missing from the jar's resources");
        YamlConfiguration bundled = YamlConfiguration.loadConfiguration(new InputStreamReader(stream, StandardCharsets.UTF_8));
        YamlConfiguration onDisk = new YamlConfiguration();
        onDisk.set("version", "v0.1");
        onDisk.set("debugMode", true);
        onDisk.set("usage-reporting.enabled", false);
        onDisk.setDefaults(bundled);

        YamlConfiguration written = saveDefaultsAndReadBack(onDisk);

        assertEquals(false, written.get("usage-reporting.enabled", null), "an opt-out on disk must survive");
        assertEquals(bundled.getString("usage-reporting.endpoint"), written.get("usage-reporting.endpoint", null));
        assertEquals(bundled.getString("usage-reporting.key"), written.get("usage-reporting.key", null));
        assertEquals(true, written.get("debugMode", null));
    }

    @Test
    void debugModeIsStoredAsABoolean() {
        when(config.isSet("debugMode")).thenReturn(true);

        configService.setConfigOption("debugMode", "true", sender);

        verify(config).set("debugMode", true);
        verify(sender).sendMessage(ChatColor.GREEN + "Boolean set.");
    }

    @Test
    void versionCannotBeSet() {
        when(config.isSet("version")).thenReturn(true);

        configService.setConfigOption("version", "v9.9.9", sender);

        verify(config, never()).set("version", "v9.9.9");
        verify(sender).sendMessage(ChatColor.RED + "Cannot set version.");
    }

    @Test
    void anOptionWithNoDedicatedBranchIsStoredAsAString() {
        when(config.isSet("A")).thenReturn(true);

        configService.setConfigOption("A", "5", sender);

        verify(config).set("A", "5");
        verify(sender).sendMessage(ChatColor.GREEN + "String set.");
    }

    @Test
    void anUnknownOptionIsReported() {
        when(config.isSet("nonexistent")).thenReturn(false);

        configService.setConfigOption("nonexistent", "value", sender);

        verify(sender).sendMessage(ChatColor.RED + "That config option wasn't found.");
    }

    @Test
    void usageReporting_readsThroughToTheBundledDefaultsWhenTheFileHasNoBlock() {
        // A server upgraded from before usage reporting has no usage-reporting
        // block in its config.yml. Bukkit's one-argument getters fall through
        // to the jar's defaults; the two-argument ones would return their
        // fallback and turn reporting off on every existing installation.
        when(config.getBoolean("usage-reporting.enabled")).thenReturn(true);
        when(config.getString("usage-reporting.endpoint")).thenReturn("https://trace.danielstephenson.dev");
        when(config.getString("usage-reporting.key")).thenReturn("bundled-key");

        assertTrue(configService.isUsageReportingEnabled());
        assertEquals("https://trace.danielstephenson.dev", configService.getUsageReportingEndpoint());
        assertEquals("bundled-key", configService.getUsageReportingKey());
        verify(config, never()).getString(eq("usage-reporting.key"), anyString());
        verify(config, never()).getString(eq("usage-reporting.endpoint"), anyString());
        verify(config, never()).getBoolean(eq("usage-reporting.enabled"), anyBoolean());
    }

    @Test
    void usageReporting_isOffWithNoKeyAnywhere() {
        when(config.getString("usage-reporting.key")).thenReturn(null);
        when(config.getString("usage-reporting.endpoint")).thenReturn(null);

        assertEquals("", configService.getUsageReportingKey(), "no key anywhere must read as off, not as null");
        assertEquals("https://trace.danielstephenson.dev", configService.getUsageReportingEndpoint());
    }

    @Test
    void usageReporting_readsTheConfiguredValues() {
        when(config.getBoolean("usage-reporting.enabled")).thenReturn(false);
        when(config.getString("usage-reporting.endpoint")).thenReturn("http://localhost:8080");
        when(config.getString("usage-reporting.key")).thenReturn("abc");

        assertFalse(configService.isUsageReportingEnabled());
        assertEquals("http://localhost:8080", configService.getUsageReportingEndpoint());
        assertEquals("abc", configService.getUsageReportingKey());
    }
}
