package dansplugins.democracy;

import dansplugins.democracy.commands.CancelCommand;
import dansplugins.democracy.commands.DefaultCommand;
import dansplugins.democracy.commands.DropOutCommand;
import dansplugins.democracy.commands.HelpCommand;
import dansplugins.democracy.commands.InfoCommand;
import dansplugins.democracy.commands.RunCommand;
import dansplugins.democracy.commands.StartCommand;
import dansplugins.democracy.commands.VoteCommand;
import dansplugins.democracy.data.PersistentData;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Pins the reference tables in the Markdown docs against the commands, {@code plugin.yml} and
 * {@code config.yml}, so that adding, renaming or removing one of those without updating the
 * docs fails the build.
 */
class DocumentationTest {
    private static final Pattern COMMAND_ROW = Pattern.compile("^\\| `(/d[^`]*)` \\|.*\\| `(d\\.\\w+)`.*\\|$");
    private static final Pattern PERMISSION_ROW = Pattern.compile("^\\| `(d\\.\\w+)` \\| `(\\w+)` \\|.*\\|$");
    private static final Pattern CONFIG_ROW = Pattern.compile("^\\| `([\\w.-]+)` \\|.*\\|$");

    /** Written by ConfigService rather than shipped in the bundled config.yml. */
    private static final List<String> PROGRAMMATIC_CONFIG_KEYS = Arrays.asList("version", "debugMode");

    private Map<String, String> documentedCommands;

    @BeforeEach
    void readCommandsTable() throws IOException {
        documentedCommands = new TreeMap<>();
        for (String line : Files.readAllLines(Paths.get("COMMANDS.md"), StandardCharsets.UTF_8)) {
            Matcher row = COMMAND_ROW.matcher(line);
            if (row.matches()) {
                documentedCommands.put(row.group(1), row.group(2));
            }
        }
    }

    @Test
    void commandsTableMatchesTheCommandsAndTheirPermissions() {
        Democracy democracy = mock(Democracy.class);
        PersistentData persistentData = new PersistentData();
        List<AbstractPluginCommand> commands = Arrays.asList(
                new HelpCommand(),
                new StartCommand(democracy, null, persistentData, null),
                new CancelCommand(democracy, persistentData, null),
                new DropOutCommand(democracy, persistentData, null),
                new InfoCommand(democracy, persistentData),
                new RunCommand(democracy, persistentData, null, null),
                new VoteCommand(democracy, persistentData, null, null));

        Map<String, String> expected = new TreeMap<>();
        expected.put("/d", new DefaultCommand(democracy).getPermissions().get(0));
        for (AbstractPluginCommand command : commands) {
            expected.put("/d " + command.getNames().get(0), command.getPermissions().get(0));
        }

        Map<String, String> documented = new TreeMap<>();
        documentedCommands.forEach((usage, permission) -> documented.put(usage.split(" <")[0], permission));
        assertEquals(expected, documented);
    }

    @Test
    void helpListsEveryDocumentedSubcommandAsItIsWrittenInTheTable() {
        CommandSender sender = mock(CommandSender.class);
        new HelpCommand().execute(sender);

        ArgumentCaptor<String> lines = ArgumentCaptor.forClass(String.class);
        verify(sender, atLeastOnce()).sendMessage(lines.capture());
        Set<String> help = new TreeSet<>();
        for (String line : lines.getAllValues()) {
            help.add(ChatColor.stripColor(line));
        }

        Set<String> subcommands = new TreeSet<>(documentedCommands.keySet());
        subcommands.remove("/d");
        assertEquals(subcommands, help);
    }

    @Test
    void userGuidePermissionsTableMatchesPluginYml() throws Exception {
        YamlConfiguration pluginYml = new YamlConfiguration();
        // permission nodes contain dots, so '.' cannot be the path separator
        pluginYml.options().pathSeparator('/');
        pluginYml.load(resource("plugin.yml"));
        Map<String, String> registered = new TreeMap<>();
        for (String node : pluginYml.getConfigurationSection("permissions").getKeys(false)) {
            registered.put(node, String.valueOf(pluginYml.get("permissions/" + node + "/default")));
        }

        Map<String, String> documented = new TreeMap<>();
        for (String line : Files.readAllLines(Paths.get("USER_GUIDE.md"), StandardCharsets.UTF_8)) {
            Matcher row = PERMISSION_ROW.matcher(line);
            if (row.matches()) {
                documented.put(row.group(1), row.group(2));
            }
        }

        assertEquals(registered, documented);
    }

    @Test
    void configTableMatchesTheKeysInConfigYml() throws Exception {
        YamlConfiguration bundled = new YamlConfiguration();
        bundled.load(resource("config.yml"));
        Set<String> keys = new TreeSet<>(PROGRAMMATIC_CONFIG_KEYS);
        for (String key : bundled.getKeys(true)) {
            if (!bundled.isConfigurationSection(key)) {
                keys.add(key);
            }
        }

        Set<String> documented = new HashSet<>();
        for (String line : Files.readAllLines(Paths.get("CONFIG.md"), StandardCharsets.UTF_8)) {
            Matcher row = CONFIG_ROW.matcher(line);
            if (row.matches()) {
                documented.add(row.group(1));
            }
        }

        assertEquals(keys, new TreeSet<>(documented));
    }

    private InputStreamReader resource(String name) {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(name);
        assertNotNull(stream, name + " is missing from the jar's resources");
        return new InputStreamReader(stream, StandardCharsets.UTF_8);
    }
}
