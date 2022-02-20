package dansplugins.democracy.commands;

import org.bukkit.command.CommandSender;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This command is intended to allow faction leaders to create elections.
 * @author Daniel McCoy Stephenson
 */
public class CreateCommand extends AbstractPluginCommand {

    public CreateCommand() {
        super(new ArrayList<>(Arrays.asList("create")), new ArrayList<>(Arrays.asList("d.create")));
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