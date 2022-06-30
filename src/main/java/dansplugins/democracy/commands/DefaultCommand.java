package dansplugins.democracy.commands;

import dansplugins.democracy.Democracy;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This command is intended to allow players to get information about the plugin.
 * @author Daniel McCoy Stephenson
 */
public class DefaultCommand extends AbstractPluginCommand {
    private final Democracy democracy;

    public DefaultCommand(Democracy democracy) {
        super(new ArrayList<>(Arrays.asList("default")), new ArrayList<>(Arrays.asList("d.default")));
        this.democracy = democracy;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        commandSender.sendMessage(ChatColor.AQUA + "Democracy " + democracy.getVersion());
        commandSender.sendMessage(ChatColor.AQUA + "Developed by: Daniel Stephenson");
        commandSender.sendMessage(ChatColor.AQUA + "Wiki: https://github.com/Dans-Plugins/Democracy/wiki");
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        return execute(commandSender);
    }
}