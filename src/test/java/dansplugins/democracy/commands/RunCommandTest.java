package dansplugins.democracy.commands;

import dansplugins.democracy.Democracy;
import dansplugins.democracy.data.PersistentData;
import dansplugins.democracy.factories.CandidateFactory;
import dansplugins.democracy.objects.Candidate;
import dansplugins.democracy.objects.Election;
import dansplugins.democracy.integrators.FactionLookup;
import dansplugins.democracy.services.StorageService;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
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
    void argumentsAreIgnored() {
        Election election = new Election(player, "TestFaction");
        persistentData.addElection(election);

        assertTrue(runCommand.execute(player, new String[] { "anything" }));
        assertTrue(election.isCandidate(playerUUID));
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

    @Test
    void aStaleCandidateRecordIsReportedRatherThanConfirmed() {
        Election election = electionWithStaleCandidateRecord();

        assertFalse(runCommand.execute(player));

        assertFalse(election.isCandidate(playerUUID));
        verify(player).sendMessage(ChatColor.RED + "Your candidacy could not be recorded. You are not a candidate in this election.");
        verify(player, never()).sendMessage(ChatColor.GREEN + "You are now a candidate in the election.");
    }

    @Test
    void aCandidacyThatWasNotRecordedDoesNotSave() {
        electionWithStaleCandidateRecord();

        runCommand.execute(player);

        verify(storageService, never()).save();
    }

    /**
     * A candidate record the election does not list, which is what a crash between the writes of
     * elections.json and candidates.json leaves behind after a /d dropout.
     */
    private Election electionWithStaleCandidateRecord() {
        Election election = new Election(player, "TestFaction");
        persistentData.addElection(election);
        persistentData.addCandidate(new Candidate(player, election));
        return election;
    }

    @Test
    void theConsoleCannotRun() {
        CommandSender console = mock(CommandSender.class);

        assertFalse(runCommand.execute(console));
        verify(console).sendMessage("This command cannot be used in the console.");
        verify(storageService, never()).save();
    }
}
