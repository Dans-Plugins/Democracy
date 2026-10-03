package dansplugins.democracy.commands;

import dansplugins.democracy.data.PersistentData;
import dansplugins.democracy.factories.ElectionFactory;
import dansplugins.democracy.services.StorageService;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dansplugins.democracy.Democracy;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

/**
 * This command is intended to allow faction leaders to create elections.
 * @author Daniel McCoy Stephenson
 */
public class StartCommand extends AbstractPluginCommand {
    private final Democracy democracy;
    private final ElectionFactory electionFactory;
    private final PersistentData persistentData;
    private final StorageService storageService;

    public StartCommand(Democracy democracy, ElectionFactory electionFactory, PersistentData persistentData, StorageService storageService) {
        super(new ArrayList<>(Arrays.asList("start")), new ArrayList<>(Arrays.asList("d.start")));
        this.democracy = democracy;
        this.electionFactory = electionFactory;
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
            player.sendMessage(ChatColor.RED + "You must be the owner of a faction to start an election.");
            return false;
        }

        if (persistentData.getElectionForFaction(factionName) != null) {
            player.sendMessage(ChatColor.RED + "An election is already in progress.");
            return false;
        }

        UUID electionUUID = electionFactory.createElection(player, factionName);
        if (electionUUID == null) {
            player.sendMessage(ChatColor.RED + "An election is already in progress.");
            return false;
        }
        storageService.save();
        player.sendMessage(ChatColor.GREEN + "Election has been started.");
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        return execute(commandSender);
    }
}