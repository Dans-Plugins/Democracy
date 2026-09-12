package dansplugins.democracy.objects;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CandidateTest {
    private Candidate candidate;
    private Election election;
    private UUID playerUUID;

    @BeforeEach
    void setUp() {
        playerUUID = UUID.randomUUID();
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(playerUUID);
        election = new Election(player, "TestFaction");
        candidate = new Candidate(player, election);
    }

    @Test
    void constructorSetsPlayerAndElection() {
        assertEquals(playerUUID, candidate.getPlayerUUID());
        assertEquals(election.getUUID(), candidate.getElectionUUID());
        assertEquals(0, candidate.getNumVoter());
    }

    @Test
    void addVoterAddsOnlyOnce() {
        UUID voterUUID = UUID.randomUUID();
        assertTrue(candidate.addVoter(voterUUID));
        assertFalse(candidate.addVoter(voterUUID));
        assertTrue(candidate.isVoter(voterUUID));
        assertEquals(1, candidate.getNumVoter());
    }

    @Test
    void removeVoterOnlySucceedsIfPresent() {
        UUID voterUUID = UUID.randomUUID();
        assertFalse(candidate.removeVoter(voterUUID));
        candidate.addVoter(voterUUID);
        assertTrue(candidate.removeVoter(voterUUID));
        assertFalse(candidate.isVoter(voterUUID));
    }

    @Test
    void saveThenLoadRebuildsAnIdenticalCandidate() {
        UUID firstVoter = UUID.randomUUID();
        UUID secondVoter = UUID.randomUUID();
        candidate.addVoter(firstVoter);
        candidate.addVoter(secondVoter);

        Candidate loaded = new Candidate(candidate.save());

        assertEquals(playerUUID, loaded.getPlayerUUID());
        assertEquals(election.getUUID(), loaded.getElectionUUID());
        assertEquals(2, loaded.getNumVoter());
        assertTrue(loaded.isVoter(firstVoter));
        assertTrue(loaded.isVoter(secondVoter));
    }

    @Test
    void saveWritesEveryFieldAsAString() {
        Map<String, String> data = candidate.save();

        assertEquals(playerUUID.toString(), data.get("playerUUID"));
        assertEquals(election.getUUID().toString(), data.get("electionUUID"));
        assertEquals("[]", data.get("voterUUIDs"));
    }

    @Test
    void loadReplacesExistingVoters() {
        UUID staleVoter = UUID.randomUUID();
        candidate.addVoter(staleVoter);
        Map<String, String> data = candidate.save();
        data.put("voterUUIDs", "[]");

        candidate.load(data);

        assertEquals(0, candidate.getNumVoter());
        assertFalse(candidate.isVoter(staleVoter));
    }

    @Test
    void loadRejectsARecordMissingAField() {
        Map<String, String> data = candidate.save();
        data.remove("electionUUID");

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> new Candidate(data));
        assertTrue(thrown.getMessage().contains("electionUUID"));
    }
}
