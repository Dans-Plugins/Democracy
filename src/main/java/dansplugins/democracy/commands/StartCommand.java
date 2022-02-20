package dansplugins.democracy.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dansplugins.democracy.Democracy;
import dansplugins.democracy.factories.ElectionFactory;
import dansplugins.factionsystem.externalapi.MF_Faction;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

/**
 * This command is intended to allow faction leaders to create elections.
 * @author Daniel McCoy Stephenson
 */
public class StartCommand extends AbstractPluginCommand {

    public StartCommand() {
        super(new ArrayList<>(Arrays.asList("start")), new ArrayList<>(Arrays.asList("d.start")));
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("This command cannot be used in the console.");
            return false;
        }
        Player player = (Player) commandSender;

        MF_Faction faction = Democracy.getInstance().getMedievalFactionsAPI().getFaction(player);
        if (faction == null || !faction.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You must be the owner of a faction to start an election.");
            return false;
        }

        UUID electionUUID = ElectionFactory.getInstance().createElection(player);
        if (electionUUID == null) {
            player.sendMessage(ChatColor.RED + "An election is already in progress.");
            return false;
        }
        player.sendMessage(ChatColor.RED + "Election has been started.");
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        return execute(commandSender);
    }
}