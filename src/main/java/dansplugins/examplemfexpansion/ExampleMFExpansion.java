package dansplugins.examplemfexpansion;

import dansplugins.factionsystem.MedievalFactions;
import dansplugins.factionsystem.externalapi.MedievalFactionsAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import dansplugins.examplemfexpansion.commands.DefaultCommand;
import dansplugins.examplemfexpansion.commands.HelpCommand;
import dansplugins.examplemfexpansion.eventhandlers.JoinHandler;
import dansplugins.examplemfexpansion.services.LocalConfigService;
import preponderous.ponder.minecraft.abs.AbstractPluginCommand;
import preponderous.ponder.minecraft.abs.PonderPlugin;
import preponderous.ponder.minecraft.spigot.tools.EventHandlerRegistry;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 */
public final class ExampleMFExpansion extends PonderPlugin {
    private static ExampleMFExpansion instance;
    private final String pluginVersion = "v" + getDescription().getVersion();
    private final MedievalFactionsAPI medievalFactionsAPI = MedievalFactions.getInstance().getAPI();

    /**
     * This can be used to get the instance of the main class that is managed by itself.
     * @return The managed instance of the main class.
     */
    public static ExampleMFExpansion getInstance() {
        return instance;
    }

    /**
     * This runs when the server starts.
     */
    @Override
    public void onEnable() {
        instance = this;

        // create/load config
        if (!(new File("./plugins/ExamplePonderPlugin/config.yml").exists())) {
            LocalConfigService.getInstance().saveMissingConfigDefaultsIfNotPresent();
        }
        else {
            // pre load compatibility checks
            if (isVersionMismatched()) {
                LocalConfigService.getInstance().saveMissingConfigDefaultsIfNotPresent();
            }
            reloadConfig();
        }

        registerEventHandlers();
        initializeCommandService();
        getPonderAPI().setDebug(false);
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
            DefaultCommand defaultCommand = new DefaultCommand();
            return defaultCommand.execute(sender);
        }

        return getPonderAPI().getCommandService().interpretCommand(sender, label, args);
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
        return LocalConfigService.getInstance().getBoolean("debugMode");
    }

    public MedievalFactionsAPI getMedievalFactionsAPI() {
        return medievalFactionsAPI;
    }

    /**
     * Registers the event handlers of the plugin using Ponder.
     */
    private void registerEventHandlers() {
        EventHandlerRegistry eventHandlerRegistry = new EventHandlerRegistry(getPonderAPI());
        ArrayList<Listener> listeners = new ArrayList<>(Arrays.asList(
                new JoinHandler()
        ));
        eventHandlerRegistry.registerEventHandlers(listeners, this);
    }

    /**
     * Initializes Ponder's command service with the plugin's commands.
     */
    private void initializeCommandService() {
        ArrayList<AbstractPluginCommand> commands = new ArrayList<>(Arrays.asList(
                new HelpCommand()
        ));
        getPonderAPI().getCommandService().initialize(commands, "That command wasn't found.");
    }
}
