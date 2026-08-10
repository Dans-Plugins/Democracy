package dansplugins.democracy.commands;

import dansplugins.democracy.Democracy;
import dansplugins.democracy.data.PersistentData;
import dansplugins.democracy.objects.Candidate;
import dansplugins.democracy.objects.Election;
import dansplugins.factionsystem.externalapi.MF_Faction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

/**
 * This command is intended to allow faction members to view information about the current election.
 * @author Daniel McCoy Stephenson
 */
public class InfoCommand extends AbstractPluginCommand {
    private final Democracy democracy;
    private final PersistentData persistentData;

    public InfoCommand(Democracy democracy, PersistentData persistentData) {
        super(new ArrayList<>(Arrays.asList("info")), new ArrayList<>(Arrays.asList("d.info")));
        this.democracy = democracy;
        this.persistentData = persistentData;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("This command cannot be used in the console.");
            return false;
        }
        Player player = (Player) commandSender;

        MF_Faction faction = democracy.getMedievalFactionsAPI().getFaction(player);
        if (faction == null) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to view election information.");
            return false;
        }

        Election election = persistentData.getElectionForFaction(faction.getName());
        if (election == null) {
            player.sendMessage(ChatColor.RED + "There is no election currently in progress in your faction.");
            return false;
        }

        player.sendMessage(ChatColor.AQUA + "=== Election Info ===");
        player.sendMessage(ChatColor.AQUA + "Started by: " + getPlayerName(election.getCreator()));
        for (UUID candidateUUID : election.getCandidateUUIDs()) {
            Candidate candidate = persistentData.getCandidate(election.getUUID(), candidateUUID);
            int votes = candidate == null ? 0 : candidate.getNumVoter();
            player.sendMessage(ChatColor.AQUA + getPlayerName(candidateUUID) + ": " + votes + " vote(s)");
        }
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        return execute(commandSender);
    }

    private String getPlayerName(UUID playerUUID) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
        String name = offlinePlayer.getName();
        return name == null ? playerUUID.toString() : name;
    }
}
