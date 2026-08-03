package dansplugins.democracy.commands;

import dansplugins.democracy.Democracy;
import dansplugins.democracy.data.PersistentData;
import dansplugins.democracy.factories.CandidateFactory;
import dansplugins.democracy.factories.VoterFactory;
import dansplugins.democracy.objects.Candidate;
import dansplugins.democracy.objects.Election;
import dansplugins.factionsystem.externalapi.MF_Faction;
import dansplugins.factionsystem.externalapi.MedievalFactionsAPI;
import org.bukkit.Bukkit;
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
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class VoteCommandTest {
    private VoteCommand voteCommand;
    private PersistentData persistentData;
    private CandidateFactory candidateFactory;
    private Player voter;
    private Player candidatePlayer;
    private UUID candidateUUID;
    private Election election;
    private MockedStatic<Bukkit> bukkit;

    @BeforeEach
    void setUp() {
        UUID voterUUID = UUID.randomUUID();
        voter = mock(Player.class);
        when(voter.getUniqueId()).thenReturn(voterUUID);

        candidateUUID = UUID.randomUUID();
        candidatePlayer = mock(Player.class);
        when(candidatePlayer.getUniqueId()).thenReturn(candidateUUID);
        when(candidatePlayer.getName()).thenReturn("CandidateName");

        bukkit = mockStatic(Bukkit.class);
        bukkit.when(() -> Bukkit.getPlayer("CandidateName")).thenReturn(candidatePlayer);
        bukkit.when(() -> Bukkit.getPlayer("NoSuchPlayer")).thenReturn(null);

        MF_Faction faction = mock(MF_Faction.class);
        when(faction.getName()).thenReturn("TestFaction");

        MedievalFactionsAPI medievalFactionsAPI = mock(MedievalFactionsAPI.class);
        when(medievalFactionsAPI.getFaction(voter)).thenReturn(faction);

        Democracy democracy = mock(Democracy.class);
        when(democracy.getMedievalFactionsAPI()).thenReturn(medievalFactionsAPI);

        persistentData = new PersistentData();
        candidateFactory = new CandidateFactory(persistentData);
        VoterFactory voterFactory = new VoterFactory(persistentData);
        voteCommand = new VoteCommand(democracy, persistentData, voterFactory);

        election = new Election(voter, "TestFaction");
        persistentData.addElection(election);
        candidateFactory.createCandidate(candidatePlayer, election);
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
    void succeedsAndRecordsVoteForCandidate() {
        assertTrue(voteCommand.execute(voter, new String[] { "CandidateName" }));

        Candidate candidate = persistentData.getCandidate(candidateUUID);
        assertEquals(1, candidate.getNumVoter());
    }

    @Test
    void failsOnSecondVoteInSameElection() {
        voteCommand.execute(voter, new String[] { "CandidateName" });

        assertFalse(voteCommand.execute(voter, new String[] { "CandidateName" }));
    }
}
