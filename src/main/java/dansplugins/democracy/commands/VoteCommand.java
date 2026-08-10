package dansplugins.democracy.commands;

import dansplugins.democracy.Democracy;
import dansplugins.democracy.data.PersistentData;
import dansplugins.democracy.factories.VoterFactory;
import dansplugins.democracy.objects.Candidate;
import dansplugins.democracy.objects.Election;
import dansplugins.factionsystem.externalapi.MF_Faction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This command is intended to allow faction members to vote for a candidate in the current election.
 * @author Daniel McCoy Stephenson
 */
public class VoteCommand extends AbstractPluginCommand {
    private final Democracy democracy;
    private final PersistentData persistentData;
    private final VoterFactory voterFactory;

    public VoteCommand(Democracy democracy, PersistentData persistentData, VoterFactory voterFactory) {
        super(new ArrayList<>(Arrays.asList("vote")), new ArrayList<>(Arrays.asList("d.vote")));
        this.democracy = democracy;
        this.persistentData = persistentData;
        this.voterFactory = voterFactory;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        commandSender.sendMessage(ChatColor.RED + "Usage: /d vote <candidate>");
        return false;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("This command cannot be used in the console.");
            return false;
        }
        Player player = (Player) commandSender;

        if (args.length == 0) {
            return execute(commandSender);
        }

        MF_Faction faction = democracy.getMedievalFactionsAPI().getFaction(player);
        if (faction == null) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to vote in an election.");
            return false;
        }

        Election election = persistentData.getElectionForFaction(faction.getName());
        if (election == null) {
            player.sendMessage(ChatColor.RED + "There is no election currently in progress in your faction.");
            return false;
        }

        if (election.isVoter(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You have already voted in this election.");
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !election.isCandidate(target.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "That player is not a candidate in this election.");
            return false;
        }

        voterFactory.createVoter(player, election);
        Candidate candidate = persistentData.getCandidate(election.getUUID(), target.getUniqueId());
        candidate.addVoter(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "Your vote for " + target.getName() + " has been cast.");
        return true;
    }
}
