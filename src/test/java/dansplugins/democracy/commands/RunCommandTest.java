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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RunCommandTest {
    private RunCommand runCommand;
    private PersistentData persistentData;
    private CandidateFactory candidateFactory;
    private Player player;
    private UUID playerUUID;
    private FactionLookup factionLookup;
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
        runCommand = new RunCommand(democracy, persistentData, candidateFactory, storageService);
    }

    @Test
    void failsWhenNotInFaction() {
        when(factionLookup.getFactionName(player)).thenReturn(null);

        assertFalse(runCommand.execute(player));
    }

    @Test
    void failsWhenNoElectionInProgress() {
        assertFalse(runCommand.execute(player));
    }

    @Test
    void succeedsAndRegistersCandidate() {
        Election election = new Election(player, "TestFaction");
        persistentData.addElection(election);

        assertTrue(runCommand.execute(player));
        assertTrue(election.isCandidate(playerUUID));
    }

    @Test
    void runningSavesTheCandidacyStraightAway() {
        persistentData.addElection(new Election(player, "TestFaction"));

        runCommand.execute(player);

        verify(storageService).save();
    }

    @Test
    void aRejectedRunDoesNotSave() {
        runCommand.execute(player);

        verify(storageService, never()).save();
    }

    @Test
    void runningTwiceSavesOnlyOnce() {
        persistentData.addElection(new Election(player, "TestFaction"));

        runCommand.execute(player);
        runCommand.execute(player);

        verify(storageService, times(1)).save();
    }

    @Test
    void failsWhenAlreadyACandidate() {
        Election election = new Election(player, "TestFaction");
        persistentData.addElection(election);
        runCommand.execute(player);

        assertFalse(runCommand.execute(player));
    }
}
