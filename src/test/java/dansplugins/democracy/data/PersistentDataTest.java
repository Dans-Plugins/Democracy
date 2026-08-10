package dansplugins.democracy.data;

import dansplugins.democracy.objects.Candidate;
import dansplugins.democracy.objects.Election;
import dansplugins.democracy.objects.Voter;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PersistentDataTest {
    private PersistentData persistentData;
    private Player player;
    private UUID playerUUID;

    @BeforeEach
    void setUp() {
        persistentData = new PersistentData();
        playerUUID = UUID.randomUUID();
        player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(playerUUID);
    }

    @Test
    void getElectionForFactionFindsMatchByNameCaseInsensitively() {
        Election election = new Election(player, "TestFaction");
        persistentData.addElection(election);

        assertEquals(election, persistentData.getElectionForFaction("testfaction"));
    }

    @Test
    void getElectionForFactionReturnsNullWhenNoMatch() {
        assertNull(persistentData.getElectionForFaction("NoSuchFaction"));
    }

    @Test
    void addElectionRejectsDuplicateUUID() {
        Election election = new Election(player, "TestFaction");
        assertTrue(persistentData.addElection(election));
        assertFalse(persistentData.addElection(election));
    }

    @Test
    void candidateCanBeAddedRetrievedAndRemoved() {
        Election election = new Election(player, "TestFaction");
        Candidate candidate = new Candidate(player, election);

        assertTrue(persistentData.addCandidate(candidate));
        assertEquals(candidate, persistentData.getCandidate(election.getUUID(), playerUUID));
        assertTrue(persistentData.removeCandidate(candidate));
        assertNull(persistentData.getCandidate(election.getUUID(), playerUUID));
    }

    @Test
    void voterCanBeAddedRetrievedAndRemoved() {
        Election election = new Election(player, "TestFaction");
        Voter voter = new Voter(player, election);

        assertTrue(persistentData.addVoter(voter));
        assertEquals(voter, persistentData.getVoter(election.getUUID(), playerUUID));
        assertTrue(persistentData.removeVoter(voter));
        assertNull(persistentData.getVoter(election.getUUID(), playerUUID));
    }

    @Test
    void candidateOfOneElectionIsNotFoundUnderAnother() {
        Election firstElection = new Election(player, "FirstFaction");
        Election secondElection = new Election(player, "SecondFaction");
        persistentData.addCandidate(new Candidate(player, firstElection));

        assertNull(persistentData.getCandidate(secondElection.getUUID(), playerUUID));
    }

    @Test
    void samePlayerCanBeACandidateInTwoElections() {
        Election firstElection = new Election(player, "FirstFaction");
        Election secondElection = new Election(player, "SecondFaction");
        Candidate secondCandidate = new Candidate(player, secondElection);

        assertTrue(persistentData.addCandidate(new Candidate(player, firstElection)));
        assertTrue(persistentData.addCandidate(secondCandidate));
        assertEquals(secondCandidate, persistentData.getCandidate(secondElection.getUUID(), playerUUID));
    }

    @Test
    void voterOfOneElectionIsNotFoundUnderAnother() {
        Election firstElection = new Election(player, "FirstFaction");
        Election secondElection = new Election(player, "SecondFaction");
        persistentData.addVoter(new Voter(player, firstElection));

        assertNull(persistentData.getVoter(secondElection.getUUID(), playerUUID));
    }

    @Test
    void samePlayerCanBeAVoterInTwoElections() {
        Election firstElection = new Election(player, "FirstFaction");
        Election secondElection = new Election(player, "SecondFaction");
        Voter secondVoter = new Voter(player, secondElection);

        assertTrue(persistentData.addVoter(new Voter(player, firstElection)));
        assertTrue(persistentData.addVoter(secondVoter));
        assertEquals(secondVoter, persistentData.getVoter(secondElection.getUUID(), playerUUID));
    }
}
