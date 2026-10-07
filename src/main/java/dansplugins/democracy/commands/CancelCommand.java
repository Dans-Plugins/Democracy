package dansplugins.democracy.commands;

import dansplugins.democracy.Democracy;
import dansplugins.democracy.data.PersistentData;
import dansplugins.democracy.objects.Election;
import dansplugins.democracy.services.StorageService;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This command is intended to allow faction leaders to cancel their faction's election without naming a winner.
 * @author Daniel McCoy Stephenson
 */
public class CancelCommand extends AbstractPluginCommand {
    private final Democracy democracy;
    private final PersistentData persistentData;
    private final StorageService storageService;

    public CancelCommand(Democracy democracy, PersistentData persistentData, StorageService storageService) {
        super(new ArrayList<>(Arrays.asList("cancel")), new ArrayList<>(Arrays.asList("d.cancel")));
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
        if (factionName == null || !democracy.getFactionLookup().leadsFaction(player)) {
            player.sendMessage(ChatColor.RED + "You must be the owner of a faction to cancel an election.");
            return false;
        }

        Election election = persistentData.getElectionForFaction(factionName);
        if (election == null) {
            player.sendMessage(ChatColor.RED + "There is no election in progress in your faction.");
            return false;
        }

        persistentData.removeElection(election);
        storageService.save();
        player.sendMessage(ChatColor.GREEN + "The election has been cancelled.");
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        return execute(commandSender);
    }
}
