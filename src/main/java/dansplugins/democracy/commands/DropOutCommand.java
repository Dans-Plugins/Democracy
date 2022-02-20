package dansplugins.democracy.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class is intended to allow candidates to drop out of the current election.
 * @author Daniel McCoy Stephenson
 */
public class DropOutCommand extends AbstractPluginCommand {

    public DropOutCommand() {
        super(new ArrayList<>(Arrays.asList("dropout")), new ArrayList<>(Arrays.asList("d.dropout")));
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        // TODO: implement
        commandSender.sendMessage(ChatColor.RED + "This command isn't implemented yet.");
        return false;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        // TODO: implement
        commandSender.sendMessage(ChatColor.RED + "This command isn't implemented yet.");
        return false;
    }
}