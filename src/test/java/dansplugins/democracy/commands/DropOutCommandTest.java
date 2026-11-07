package dansplugins.democracy.commands;

import dansplugins.democracy.Democracy;
import dansplugins.democracy.data.PersistentData;
import dansplugins.democracy.factories.CandidateFactory;
import dansplugins.democracy.factories.VoterFactory;
import dansplugins.democracy.objects.Election;
import dansplugins.democracy.integrators.FactionLookup;
import dansplugins.democracy.services.StorageService;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DropOutCommandTest {
    private FactionLookup factionLookup;
    private DropOutCommand dropOutCommand;
    private PersistentData persistentData;
    private CandidateFactory candidateFactory;
    private Player player;
    private UUID playerUUID;
    private Election election;
    private StorageService storageService;

    @BeforeEach
    void setUp() {
        playerUUID = UUID.randomUUID();
        player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(playerUUID);

        factionLookup = mock(FactionLookup.class);
        when(factionLookup.getFactionName(player)).thenReturn("TestFaction");

        Democracy democracy = mock(Democracy.class);
        when(democracy.getFactionLookup()).thenReturn(factionLookup);

        persistentData = new PersistentData();
        candidateFactory = new CandidateFactory(persistentData);
        storageService = mock(StorageService.class);
        dropOutCommand = new DropOutCommand(democracy, persistentData, storageService);

        election = new Election(player, "TestFaction");
        persistentData.addElection(election);
    }

    @Test
    void failsWhenNotACandidate() {
        assertFalse(dropOutCommand.execute(player));
    }

    @Test
    void failsWhenTheCandidateRecordIsMissingFromPersistentData() {
        election.addCandidate(playerUUID);

        assertFalse(dropOutCommand.execute(player));
        assertTrue(election.isCandidate(playerUUID));
    }

    @Test
    void droppingOutSavesStraightAway() {
        candidateFactory.createCandidate(player, election);

        dropOutCommand.execute(player);

        verify(storageService).save();
    }

    @Test
    void aRejectedDropOutDoesNotSave() {
        dropOutCommand.execute(player);

        verify(storageService, never()).save();
    }

    @Test
    void succeedsAndRemovesCandidate() {
        candidateFactory.createCandidate(player, election);

        assertTrue(dropOutCommand.execute(player));
        assertFalse(election.isCandidate(playerUUID));
        assertFalse(dropOutCommand.execute(player));
    }

    @Test
    void aPlayerInNoFactionCannotDropOut() {
        candidateFactory.createCandidate(player, election);
        when(factionLookup.getFactionName(player)).thenReturn(null);

        assertFalse(dropOutCommand.execute(player));
        verify(player).sendMessage(ChatColor.RED + "You must be in a faction to drop out of an election.");
        assertTrue(election.isCandidate(playerUUID));
    }

    @Test
    void failsWhenTheFactionHasNoElection() {
        persistentData.removeElection(election);

        assertFalse(dropOutCommand.execute(player));
        verify(player).sendMessage(ChatColor.RED + "You are not currently a candidate in an election.");
        verify(storageService, never()).save();
    }

    @Test
    void anotherFactionsElectionIsNotDroppedOutOf() {
        candidateFactory.createCandidate(player, election);
        when(factionLookup.getFactionName(player)).thenReturn("OtherFaction");

        assertFalse(dropOutCommand.execute(player));
        assertTrue(election.isCandidate(playerUUID));
    }

    @Test
    void argumentsAreIgnored() {
        candidateFactory.createCandidate(player, election);

        assertTrue(dropOutCommand.execute(player, new String[] { "anything" }));
        assertFalse(election.isCandidate(playerUUID));
    }

    /** The behaviour USER_GUIDE.md describes; whether it is intended is the open question in #62. */
    @Test
    void votesForACandidateWhoDropsOutAreDiscardedAndTheirVotersStayCounted() {
        UUID voterUUID = UUID.randomUUID();
        Player voter = mock(Player.class);
        when(voter.getUniqueId()).thenReturn(voterUUID);
        candidateFactory.createCandidate(player, election);
        new VoterFactory(persistentData).createVoter(voter, election);
        persistentData.getCandidate(election.getUUID(), playerUUID).addVoter(voterUUID);

        assertTrue(dropOutCommand.execute(player));

        assertNull(persistentData.getCandidate(election.getUUID(), playerUUID));
        assertTrue(election.isVoter(voterUUID));
        assertNotNull(persistentData.getVoter(election.getUUID(), voterUUID));

        candidateFactory.createCandidate(player, election);
        assertEquals(0, persistentData.getCandidate(election.getUUID(), playerUUID).getNumVoter());
    }

    @Test
    void theConsoleCannotDropOut() {
        CommandSender console = mock(CommandSender.class);

        assertFalse(dropOutCommand.execute(console));
        verify(console).sendMessage("This command cannot be used in the console.");
        verify(storageService, never()).save();
    }
}
