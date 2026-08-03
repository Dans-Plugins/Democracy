package dansplugins.democracy.commands;

import dansplugins.democracy.Democracy;
import dansplugins.democracy.data.PersistentData;
import dansplugins.democracy.factories.CandidateFactory;
import dansplugins.democracy.objects.Election;
import dansplugins.factionsystem.externalapi.MF_Faction;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This command is intended to allow faction members to run as a candidate in the current election.
 * @author Daniel McCoy Stephenson
 */
public class RunCommand extends AbstractPluginCommand {
    private final Democracy democracy;
    private final PersistentData persistentData;
    private final CandidateFactory candidateFactory;

    public RunCommand(Democracy democracy, PersistentData persistentData, CandidateFactory candidateFactory) {
        super(new ArrayList<>(Arrays.asList("run")), new ArrayList<>(Arrays.asList("d.run")));
        this.democracy = democracy;
        this.persistentData = persistentData;
        this.candidateFactory = candidateFactory;
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
            player.sendMessage(ChatColor.RED + "You must be in a faction to run in an election.");
            return false;
        }

        Election election = persistentData.getElectionForFaction(faction.getName());
        if (election == null) {
            player.sendMessage(ChatColor.RED + "There is no election currently in progress in your faction.");
            return false;
        }

        if (election.isCandidate(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are already a candidate in this election.");
            return false;
        }

        candidateFactory.createCandidate(player, election);
        player.sendMessage(ChatColor.GREEN + "You are now a candidate in the election.");
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}
