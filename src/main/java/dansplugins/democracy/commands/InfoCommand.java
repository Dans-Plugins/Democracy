package dansplugins.democracy.commands;

import org.bukkit.command.CommandSender;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This command is intended to allow faction members to view information about the current election.
 * @author Daniel McCoy Stephenson
 */
public class InfoCommand extends AbstractPluginCommand {

    public InfoCommand() {
        super(new ArrayList<>(Arrays.asList("info")), new ArrayList<>(Arrays.asList("d.info")));
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        // TODO: implement
        return false;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        // TODO: implement
        return false;
    }
}