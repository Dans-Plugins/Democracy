package dansplugins.democracy.commands;

import org.bukkit.command.CommandSender;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This command is intended to allow faction members to vote for a candidate in the current election.
 * @author Daniel McCoy Stephenson
 */
public class VoteCommand extends AbstractPluginCommand {

    public VoteCommand() {
        super(new ArrayList<>(Arrays.asList("vote")), new ArrayList<>(Arrays.asList("d.vote")));
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        // TODO: implement
        return false;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        // TODO: implement
        return false;
    }
}