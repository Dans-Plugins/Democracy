package dansplugins.democracy;

import dansplugins.democracy.data.PersistentData;
import dansplugins.democracy.factories.CandidateFactory;
import dansplugins.democracy.factories.ElectionFactory;
import dansplugins.democracy.factories.VoterFactory;
import dansplugins.factionsystem.MedievalFactions;
import dansplugins.factionsystem.externalapi.MedievalFactionsAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

import dansplugins.democracy.commands.StartCommand;
import dansplugins.democracy.commands.DefaultCommand;
import dansplugins.democracy.commands.DropOutCommand;
import dansplugins.democracy.commands.HelpCommand;
import dansplugins.democracy.commands.InfoCommand;
import dansplugins.democracy.commands.RunCommand;
import dansplugins.democracy.commands.VoteCommand;
import dansplugins.democracy.listeners.JoinListener;
import dansplugins.democracy.services.ConfigService;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;
import preponderous.ponder.minecraft.bukkit.abs.PonderBukkitPlugin;
import preponderous.ponder.minecraft.bukkit.services.CommandService;
import preponderous.ponder.minecraft.bukkit.tools.EventHandlerRegistry;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 */
public final class Democracy extends PonderBukkitPlugin {
    private final String pluginVersion = "v" + getDescription().getVersion();
    
    private final MedievalFactionsAPI medievalFactionsAPI = MedievalFactions.getInstance().getAPI();
    private final CommandService commandService = new CommandService(getPonder());
    private final ConfigService configService = new ConfigService(this);
    private final PersistentData persistentData = new PersistentData();
    private final ElectionFactory electionFactory = new ElectionFactory(persistentData);
    private final CandidateFactory candidateFactory = new CandidateFactory(persistentData);
    private final VoterFactory voterFactory = new VoterFactory(persistentData);

    /**
     * This runs when the server starts.
     */
    @Override
    public void onEnable() {
        // create/load config
        if (!(new File("./plugins/ExamplePonderPlugin/config.yml").exists())) {
            configService.saveMissingConfigDefaultsIfNotPresent();
        }
        else {
            // pre load compatibility checks
            if (isVersionMismatched()) {
                configService.saveMissingConfigDefaultsIfNotPresent();
            }
            reloadConfig();
        }

        registerEventHandlers();
        initializeCommandService();
    }

    /**
     * This runs when the server stops.
     */
    @Override
    public void onDisable() {

    }

    /**
     * This method handles commands sent to the minecraft server and interprets them if the label matches one of the core commands.
     * @param sender The sender of the command.
     * @param cmd The command that was sent. This is unused.
     * @param label The core command that has been invoked.
     * @param args Arguments of the core command. Often sub-commands.
     * @return A boolean indicating whether the execution of the command was successful.
     */
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
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
     * Registers the event handlers of the plugin using Ponder.
     */
    private void registerEventHandlers() {
        EventHandlerRegistry eventHandlerRegistry = new EventHandlerRegistry();
        ArrayList<Listener> listeners = new ArrayList<>(Arrays.asList(
                new JoinListener()
        ));
        eventHandlerRegistry.registerEventHandlers(listeners, this);
    }

    /**
     * Initializes Ponder's command service with the plugin's commands.
     */
    private void initializeCommandService() {
        ArrayList<AbstractPluginCommand> commands = new ArrayList<>(Arrays.asList(
                new HelpCommand(),
                new StartCommand(this, electionFactory),
                new DropOutCommand(),
                new InfoCommand(),
                new RunCommand(),
                new VoteCommand()
        ));
        commandService.initialize(commands, "That command wasn't found.");
    }
}
