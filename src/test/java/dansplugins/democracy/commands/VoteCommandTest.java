package dansplugins.democracy.commands;

import dansplugins.democracy.Democracy;
import dansplugins.democracy.data.PersistentData;
import dansplugins.democracy.factories.CandidateFactory;
import dansplugins.democracy.factories.VoterFactory;
import dansplugins.democracy.objects.Candidate;
import dansplugins.democracy.objects.Election;
import dansplugins.democracy.objects.Voter;
import dansplugins.democracy.integrators.FactionLookup;
import dansplugins.democracy.services.StorageService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VoteCommandTest {
    private FactionLookup factionLookup;
    private VoteCommand voteCommand;
    private PersistentData persistentData;
    private CandidateFactory candidateFactory;
    private VoterFactory voterFactory;
    private Player voter;
    private Player candidatePlayer;
    private UUID candidateUUID;
    private UUID otherCandidateUUID;
    private UUID offlineCandidateUUID;
    private Election election;
    private MockedStatic<Bukkit> bukkit;
    private StorageService storageService;

    @BeforeEach
    void setUp() {
        UUID voterUUID = UUID.randomUUID();
        voter = mock(Player.class);
        when(voter.getUniqueId()).thenReturn(voterUUID);

        candidateUUID = UUID.randomUUID();
        candidatePlayer = mock(Player.class);
        when(candidatePlayer.getUniqueId()).thenReturn(candidateUUID);
        when(candidatePlayer.getName()).thenReturn("CandidateName");

        otherCandidateUUID = UUID.randomUUID();
        Player otherCandidatePlayer = mock(Player.class);
        when(otherCandidatePlayer.getUniqueId()).thenReturn(otherCandidateUUID);
        when(otherCandidatePlayer.getName()).thenReturn("OtherCandidateName");

        offlineCandidateUUID = UUID.randomUUID();
        Player offlineCandidatePlayer = mock(Player.class);
        when(offlineCandidatePlayer.getUniqueId()).thenReturn(offlineCandidateUUID);

        UUID nonCandidateUUID = UUID.randomUUID();
        Player nonCandidatePlayer = mock(Player.class);
        when(nonCandidatePlayer.getUniqueId()).thenReturn(nonCandidateUUID);
        when(nonCandidatePlayer.getName()).thenReturn("NonCandidateName");

        bukkit = mockStatic(Bukkit.class);
        bukkit.when(() -> Bukkit.getPlayer("CandidateName")).thenReturn(candidatePlayer);
        bukkit.when(() -> Bukkit.getPlayer("OtherCandidateName")).thenReturn(otherCandidatePlayer);
        bukkit.when(() -> Bukkit.getPlayer("NonCandidateName")).thenReturn(nonCandidatePlayer);
        bukkit.when(() -> Bukkit.getPlayer("NoSuchPlayer")).thenReturn(null);
        bukkit.when(() -> Bukkit.getPlayer("OfflineCandidateName")).thenReturn(null);
        stubOfflinePlayerName(candidateUUID, "CandidateName");
        stubOfflinePlayerName(otherCandidateUUID, "OtherCandidateName");
        stubOfflinePlayerName(offlineCandidateUUID, "OfflineCandidateName");
        stubOfflinePlayerName(nonCandidateUUID, "NonCandidateName");

        factionLookup = mock(FactionLookup.class);
        when(factionLookup.getFactionName(voter)).thenReturn("TestFaction");

        Democracy democracy = mock(Democracy.class);
        when(democracy.getFactionLookup()).thenReturn(factionLookup);

        persistentData = new PersistentData();
        candidateFactory = new CandidateFactory(persistentData);
        voterFactory = new VoterFactory(persistentData);
        storageService = mock(StorageService.class);
        voteCommand = new VoteCommand(democracy, persistentData, voterFactory, storageService);

        election = new Election(voter, "TestFaction");
        persistentData.addElection(election);
        candidateFactory.createCandidate(candidatePlayer, election);
        candidateFactory.createCandidate(otherCandidatePlayer, election);
        candidateFactory.createCandidate(offlineCandidatePlayer, election);
    }

    private void stubOfflinePlayerName(UUID playerUUID, String name) {
        OfflinePlayer offlinePlayer = mock(OfflinePlayer.class);
        when(offlinePlayer.getName()).thenReturn(name);
        bukkit.when(() -> Bukkit.getOfflinePlayer(playerUUID)).thenReturn(offlinePlayer);
    }

    @AfterEach
    void tearDown() {
        bukkit.close();
    }

    @Test
    void failsWithNoArguments() {
        assertFalse(voteCommand.execute(voter, new String[0]));
    }

    @Test
    void failsWhenTargetIsNotACandidate() {
        assertFalse(voteCommand.execute(voter, new String[] { "NoSuchPlayer" }));
    }

    @Test
    void failsWhenTargetIsAnOnlinePlayerWhoIsNotACandidate() {
        assertFalse(voteCommand.execute(voter, new String[] { "NonCandidateName" }));
        assertFalse(election.isVoter(voter.getUniqueId()));
    }

    @Test
    void succeedsAndRecordsVoteForACandidateWhoIsOffline() {
        assertTrue(voteCommand.execute(voter, new String[] { "OfflineCandidateName" }));

        assertEquals(1, persistentData.getCandidate(election.getUUID(), offlineCandidateUUID).getNumVoter());
    }

    @Test
    void matchesTheCandidateNameCaseInsensitively() {
        assertTrue(voteCommand.execute(voter, new String[] { "candidatename" }));

        assertEquals(1, persistentData.getCandidate(election.getUUID(), candidateUUID).getNumVoter());
    }

    @Test
    void succeedsAndRecordsVoteForCandidate() {
        assertTrue(voteCommand.execute(voter, new String[] { "CandidateName" }));

        Candidate candidate = persistentData.getCandidate(election.getUUID(), candidateUUID);
        assertEquals(1, candidate.getNumVoter());
    }

    @Test
    void votingSavesTheVoteStraightAway() {
        voteCommand.execute(voter, new String[] { "CandidateName" });

        verify(storageService).save();
    }

    @Test
    void aRejectedVoteDoesNotSave() {
        voteCommand.execute(voter, new String[] { "NoSuchPlayer" });

        verify(storageService, never()).save();
    }

    @Test
    void failsOnSecondVoteInSameElection() {
        voteCommand.execute(voter, new String[] { "CandidateName" });

        assertFalse(voteCommand.execute(voter, new String[] { "CandidateName" }));
    }

    @Test
    void failsWhenTheCandidateRecordIsMissingFromPersistentData() {
        Election electionWithoutRecords = new Election(voter, "TestFaction");
        electionWithoutRecords.addCandidate(candidateUUID);
        persistentData.removeElection(election);
        persistentData.addElection(electionWithoutRecords);

        assertFalse(voteCommand.execute(voter, new String[] { "CandidateName" }));
        assertFalse(electionWithoutRecords.isVoter(voter.getUniqueId()));
    }

    @Test
    void failsWhenTheVoterRecordCannotBeCreated() {
        persistentData.addVoter(new Voter(voter, election));

        assertFalse(voteCommand.execute(voter, new String[] { "CandidateName" }));
        assertEquals(0, persistentData.getCandidate(election.getUUID(), candidateUUID).getNumVoter());
    }

    @Test
    void aPlayerWhoVotedInAnEarlierElectionStillGetsOneVoteInTheCurrentOne() {
        Election earlierElection = new Election(voter, "EarlierFaction");
        persistentData.addElection(earlierElection);
        voterFactory.createVoter(voter, earlierElection);

        assertTrue(voteCommand.execute(voter, new String[] { "CandidateName" }));
        assertFalse(voteCommand.execute(voter, new String[] { "OtherCandidateName" }));
        assertEquals(1, persistentData.getCandidate(election.getUUID(), candidateUUID).getNumVoter());
        assertEquals(0, persistentData.getCandidate(election.getUUID(), otherCandidateUUID).getNumVoter());
    }
}
