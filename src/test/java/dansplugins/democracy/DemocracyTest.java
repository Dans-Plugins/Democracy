package dansplugins.democracy;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Pins {@link Democracy#isVersionMismatched()}, which {@code onEnable()} uses to decide whether
 * the missing config defaults are written into an existing config.yml.
 */
class DemocracyTest {
    private Democracy democracy;
    private YamlConfiguration config;

    @BeforeEach
    void setUp() {
        democracy = mock(Democracy.class);
        config = new YamlConfiguration();
        when(democracy.getConfig()).thenReturn(config);
        when(democracy.getVersion()).thenReturn("v0.4.1");
        when(democracy.isVersionMismatched()).thenCallRealMethod();
    }

    @Test
    void configWrittenByTheSameVersionIsNotMismatched() {
        config.set("version", "v0.4.1");

        assertFalse(democracy.isVersionMismatched());
    }

    @Test
    void versionComparisonIgnoresCase() {
        config.set("version", "V0.4.1");

        assertFalse(democracy.isVersionMismatched());
    }

    @Test
    void configWrittenByAnotherVersionIsMismatched() {
        config.set("version", "v0.4.0");

        assertTrue(democracy.isVersionMismatched());
    }

    @Test
    void configWithNoVersionKeyIsNotMismatched() {
        assertFalse(democracy.isVersionMismatched());
    }

    @Test
    void unknownPluginVersionIsNotMismatched() {
        config.set("version", "v0.4.1");
        when(democracy.getVersion()).thenReturn(null);

        assertFalse(democracy.isVersionMismatched());
    }
}
