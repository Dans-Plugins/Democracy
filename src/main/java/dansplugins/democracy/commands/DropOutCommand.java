package dansplugins.democracy.commands;

import dansplugins.democracy.Democracy;
import dansplugins.democracy.data.PersistentData;
import dansplugins.democracy.objects.Candidate;
import dansplugins.democracy.objects.Election;
import dansplugins.democracy.services.StorageService;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class is intended to allow candidates to drop out of the current election.
 * @author Daniel McCoy Stephenson
 */
public class DropOutCommand extends AbstractPluginCommand {
    private final Democracy democracy;
    private final PersistentData persistentData;
    private final StorageService storageService;

    public DropOutCommand(Democracy democracy, PersistentData persistentData, StorageService storageService) {
        super(new ArrayList<>(Arrays.asList("dropout")), new ArrayList<>(Arrays.asList("d.dropout")));
        this.democracy = democracy;
        this.persistentData = persistentData;
        this.storageService = storageService;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("This command cannot be used in the console.");
            return false;
        }
        Player player = (Player) commandSender;

        String factionName = democracy.getFactionLookup().getFactionName(player);
        if (factionName == null) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to drop out of an election.");
            return false;
        }

        Election election = persistentData.getElectionForFaction(factionName);
        if (election == null || !election.isCandidate(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are not currently a candidate in an election.");
            return false;
        }

        Candidate candidate = persistentData.getCandidate(election.getUUID(), player.getUniqueId());
        if (candidate == null) {
            player.sendMessage(ChatColor.RED + "Your candidacy record could not be found. You have not been dropped from the election.");
            return false;
        }

        persistentData.removeCandidate(candidate);
        election.removeCandidate(player.getUniqueId());
        storageService.save();
        player.sendMessage(ChatColor.GREEN + "You have dropped out of the election.");
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}
