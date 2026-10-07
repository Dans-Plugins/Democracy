package dansplugins.democracy.commands;

import dansplugins.democracy.Democracy;
import dansplugins.democracy.data.PersistentData;
import dansplugins.democracy.factories.CandidateFactory;
import dansplugins.democracy.factories.ElectionFactory;
import dansplugins.democracy.factories.VoterFactory;
import dansplugins.democracy.integrators.FactionLookup;
import dansplugins.democracy.objects.Election;
import dansplugins.democracy.services.StorageService;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CancelCommandTest {
    private Democracy democracy;
    private CancelCommand cancelCommand;
    private PersistentData persistentData;
    private Player player;
    private FactionLookup factionLookup;
    private StorageService storageService;
    private Election election;

    @BeforeEach
    void setUp() {
        UUID playerUUID = UUID.randomUUID();
        player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(playerUUID);

        factionLookup = mock(FactionLookup.class);
        when(factionLookup.getFactionName(player)).thenReturn("TestFaction");
        when(factionLookup.leadsFaction(player)).thenReturn(true);

        democracy = mock(Democracy.class);
        when(democracy.getFactionLookup()).thenReturn(factionLookup);

        persistentData = new PersistentData();
        storageService = mock(StorageService.class);
        cancelCommand = new CancelCommand(democracy, persistentData, storageService);

        election = new Election(player, "TestFaction");
        persistentData.addElection(election);
    }

    @Test
    void theLeaderCanCancelTheirFactionsElection() {
        assertTrue(cancelCommand.execute(player));

        assertNull(persistentData.getElectionForFaction("TestFaction"));
        verify(player).sendMessage(ChatColor.GREEN + "The election has been cancelled.");
    }

    @Test
    void cancellingRemovesTheElectionsCandidateAndVoterRecords() {
        new CandidateFactory(persistentData).createCandidate(player, election);
        new VoterFactory(persistentData).createVoter(player, election);

        cancelCommand.execute(player);

        assertTrue(persistentData.getCandidates().isEmpty());
        assertTrue(persistentData.getVoters().isEmpty());
    }

    @Test
    void cancellingSavesStraightAway() {
        cancelCommand.execute(player);

        verify(storageService).save();
    }

    @Test
    void aMemberWhoDoesNotLeadTheFactionCannotCancel() {
        when(factionLookup.leadsFaction(player)).thenReturn(false);

        assertFalse(cancelCommand.execute(player));
        assertNotNull(persistentData.getElectionForFaction("TestFaction"));
        verify(storageService, never()).save();
    }

    @Test
    void aPlayerInNoFactionCannotCancel() {
        when(factionLookup.getFactionName(player)).thenReturn(null);

        assertFalse(cancelCommand.execute(player));
        assertNotNull(persistentData.getElectionForFaction("TestFaction"));
    }

    @Test
    void failsWhenTheFactionHasNoElection() {
        persistentData.removeElection(election);

        assertFalse(cancelCommand.execute(player));
        verify(player).sendMessage(ChatColor.RED + "There is no election in progress in your faction.");
        verify(storageService, never()).save();
    }

    @Test
    void anotherElectionCanBeStartedOnceTheFirstIsCancelled() {
        cancelCommand.execute(player);

        StartCommand startCommand = new StartCommand(democracy, new ElectionFactory(persistentData), persistentData, storageService);
        assertTrue(startCommand.execute(player));
    }

    @Test
    void theConsoleCannotCancel() {
        CommandSender console = mock(CommandSender.class);

        assertFalse(cancelCommand.execute(console));
        assertNotNull(persistentData.getElectionForFaction("TestFaction"));
    }
}
