package dansplugins.democracy;

import dansplugins.democracy.data.PersistentData;
import dansplugins.democracy.factories.CandidateFactory;
import dansplugins.democracy.factories.ElectionFactory;
import dansplugins.democracy.factories.VoterFactory;
import dansplugins.factionsystem.MedievalFactions;
import dansplugins.factionsystem.externalapi.MedievalFactionsAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import dansplugins.democracy.commands.StartCommand;
import dansplugins.democracy.commands.DefaultCommand;
import dansplugins.democracy.commands.DropOutCommand;
import dansplugins.democracy.commands.HelpCommand;
import dansplugins.democracy.commands.InfoCommand;
import dansplugins.democracy.commands.RunCommand;
import dansplugins.democracy.commands.VoteCommand;
import dansplugins.democracy.services.ConfigService;
import dansplugins.democracy.services.StorageService;
import dansplugins.democracy.trace.TraceClient;
import dansplugins.democracy.utils.Logger;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;
import preponderous.ponder.minecraft.bukkit.abs.PonderBukkitPlugin;
import preponderous.ponder.minecraft.bukkit.services.CommandService;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author Daniel McCoy Stephenson
 */
public final class Democracy extends PonderBukkitPlugin {
    private final String pluginVersion = "v" + getDescription().getVersion();
    
    private final MedievalFactionsAPI medievalFactionsAPI = MedievalFactions.getInstance().getAPI();
    private final CommandService commandService = new CommandService(getPonder());
    private final ConfigService configService = new ConfigService(this);
    private final PersistentData persistentData = new PersistentData();
    private final StorageService storageService = new StorageService(this, persistentData);
    private final ElectionFactory electionFactory = new ElectionFactory(persistentData);
    private final CandidateFactory candidateFactory = new CandidateFactory(persistentData);
    private final VoterFactory voterFactory = new VoterFactory(persistentData);
    private final Logger logger = new Logger(this);

    // A no-op until the config has been read, so a command arriving before
    // onEnable() finishes has something safe to report to.
    private TraceClient trace = TraceClient.disabled();

    /**
     * This runs when the server starts.
     */
    @Override
    public void onEnable() {
        // create/load config
        if (!(new File("./plugins/Democracy/config.yml").exists())) {
            // write the bundled config.yml (with its comments) before the
            // programmatic defaults are added to it
            saveDefaultConfig();
            configService.saveMissingConfigDefaultsIfNotPresent();
        }
        else {
            // pre load compatibility checks
            if (isVersionMismatched()) {
                configService.saveMissingConfigDefaultsIfNotPresent();
            }
            reloadConfig();
        }

        // restore any election that was in progress when the server last stopped. Without it
        // the commands would confirm votes that could never be saved (save() is refused after
        // a failed load), so the plugin stops here rather than run on state it cannot keep.
        if (!storageService.load()) {
            getLogger().severe("Democracy is being disabled because its election data could not be loaded.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        initializeCommandService();

        // usage reporting: one event now, one per command; see config.yml
        trace = TraceClient.builder(configService.getUsageReportingEndpoint(), getName())
                .key(configService.getUsageReportingKey())
                .enabled(configService.isUsageReportingEnabled())
                .logger(getLogger())
                .build();
        trace.report("startup", null, Collections.singletonMap("version", getDescription().getVersion()));

        logger.log("Democracy " + getVersion() + " has been enabled.");
    }

    /**
     * This runs when the server stops.
     */
    @Override
    public void onDisable() {
        storageService.save();
        trace.close();
        logger.log("Democracy " + getVersion() + " has been disabled.");
    }

    /**
     * This method handles commands sent to the minecraft server and interprets them if the label matches one of the core commands.
     * @param sender The sender of the command.
     * @param cmd The command that was sent. Only its name is used, for usage reporting.
     * @param label The core command that has been invoked.
     * @param args Arguments of the core command. Often sub-commands.
     * @return A boolean indicating whether the execution of the command was successful.
     */
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        trace.report("command", null, Collections.singletonMap("name", cmd.getName()));
        if (args.length == 0) {
            DefaultCommand defaultCommand = new DefaultCommand(this);
            return defaultCommand.execute(sender);
        }

        return commandService.interpretAndExecuteCommand(sender, label, args);
    }

    /**
     * This can be used to get the version of the plugin.
     * @return A string containing the version preceded by 'v'
     */
    public String getVersion() {
        return pluginVersion;
    }

    /**
     * Checks if the version is mismatched.
     * @return A boolean indicating if the version is mismatched.
     */
    public boolean isVersionMismatched() {
        String configVersion = this.getConfig().getString("version");
        if (configVersion == null || this.getVersion() == null) {
            return false;
        } else {
            return !configVersion.equalsIgnoreCase(this.getVersion());
        }
    }

    /**
     * Checks if debug is enabled.
     * @return Whether debug is enabled.
     */
    public boolean isDebugEnabled() {
        return configService.getBoolean("debugMode");
    }

    public MedievalFactionsAPI getMedievalFactionsAPI() {
        return medievalFactionsAPI;
    }

    /**
     * Initializes Ponder's command service with the plugin's commands.
     */
    private void initializeCommandService() {
        ArrayList<AbstractPluginCommand> commands = new ArrayList<>(Arrays.asList(
                new HelpCommand(),
                new StartCommand(this, electionFactory, persistentData),
                new DropOutCommand(this, persistentData),
                new InfoCommand(this, persistentData),
                new RunCommand(this, persistentData, candidateFactory),
                new VoteCommand(this, persistentData, voterFactory)
        ));
        commandService.initialize(commands, "That command wasn't found.");
    }
}
