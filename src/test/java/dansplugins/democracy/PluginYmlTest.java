package dansplugins.democracy;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pins that every permission node a command declares is registered in {@code plugin.yml}, so
 * permission plugins can discover and grant it.
 */
class PluginYmlTest {

    private YamlConfiguration pluginYml;

    @BeforeEach
    void loadPluginYml() throws Exception {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("plugin.yml");
        assertNotNull(stream, "plugin.yml is missing from the jar's resources");
        // permission nodes contain dots, so '.' cannot be the path separator
        pluginYml = new YamlConfiguration();
        pluginYml.options().pathSeparator('/');
        pluginYml.load(new InputStreamReader(stream, StandardCharsets.UTF_8));
    }

    @Test
    void everyCommandPermissionIsRegisteredAndGrantedByDefault() {
        String[] nodes = { "d.default", "d.help", "d.info", "d.vote", "d.run", "d.dropout", "d.start", "d.cancel" };
        for (String node : nodes) {
            String key = "permissions/" + node + "/default";
            assertTrue(pluginYml.isSet(key), node + " is not registered in plugin.yml");
            assertTrue(pluginYml.getBoolean(key), node + " is not granted by default");
        }
    }
}
