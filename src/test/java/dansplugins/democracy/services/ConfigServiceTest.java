package dansplugins.democracy.services;

import dansplugins.democracy.Democracy;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

    @BeforeEach
    void setUp() {
        config = mock(FileConfiguration.class);
        sender = mock(CommandSender.class);

        Democracy democracy = mock(Democracy.class);
        when(democracy.getConfig()).thenReturn(config);

        configService = new ConfigService(democracy);
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
