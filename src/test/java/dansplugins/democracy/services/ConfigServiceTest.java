package dansplugins.democracy.services;

import dansplugins.democracy.Democracy;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
}
