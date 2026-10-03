package dansplugins.democracy.commands;

import dansplugins.democracy.Democracy;
import dansplugins.democracy.data.PersistentData;
import dansplugins.democracy.factories.CandidateFactory;
import dansplugins.democracy.objects.Election;
import dansplugins.democracy.integrators.FactionLookup;
import dansplugins.democracy.services.StorageService;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
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
}
