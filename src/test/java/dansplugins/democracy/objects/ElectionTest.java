package dansplugins.democracy.objects;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ElectionTest {
    private Election election;
    private UUID creatorUUID;

    @BeforeEach
    void setUp() {
        creatorUUID = UUID.randomUUID();
        Player creator = mock(Player.class);
        when(creator.getUniqueId()).thenReturn(creatorUUID);
        election = new Election(creator, "TestFaction");
    }

    @Test
    void constructorSetsCreatorAndFactionName() {
        assertEquals(creatorUUID, election.getCreator());
        assertEquals("TestFaction", election.getFactionName());
    }

    @Test
    void addCandidateAddsOnlyOnce() {
        UUID candidateUUID = UUID.randomUUID();
        assertTrue(election.addCandidate(candidateUUID));
        assertFalse(election.addCandidate(candidateUUID));
        assertTrue(election.isCandidate(candidateUUID));
        assertEquals(1, election.getCandidateUUIDs().size());
    }

    @Test
    void removeCandidateOnlySucceedsIfPresent() {
        UUID candidateUUID = UUID.randomUUID();
        assertFalse(election.removeCandidate(candidateUUID));
        election.addCandidate(candidateUUID);
        assertTrue(election.removeCandidate(candidateUUID));
        assertFalse(election.isCandidate(candidateUUID));
    }

    @Test
    void addVoterAddsOnlyOnce() {
        UUID voterUUID = UUID.randomUUID();
        assertTrue(election.addVoter(voterUUID));
        assertFalse(election.addVoter(voterUUID));
        assertTrue(election.isVoter(voterUUID));
    }

    @Test
    void removeVoterOnlySucceedsIfPresent() {
        UUID voterUUID = UUID.randomUUID();
        assertFalse(election.removeVoter(voterUUID));
        election.addVoter(voterUUID);
        assertTrue(election.removeVoter(voterUUID));
        assertFalse(election.isVoter(voterUUID));
    }

    @Test
    void getCandidateUUIDsIsUnmodifiable() {
        assertThrows(UnsupportedOperationException.class, () -> election.getCandidateUUIDs().add(UUID.randomUUID()));
    }

    @Test
    void saveThenLoadRebuildsAnIdenticalElection() {
        UUID candidateUUID = UUID.randomUUID();
        UUID voterUUID = UUID.randomUUID();
        election.addCandidate(candidateUUID);
        election.addVoter(voterUUID);

        Election loaded = new Election(election.save());

        assertEquals(election.getUUID(), loaded.getUUID());
        assertEquals(election.getCreationTimestamp(), loaded.getCreationTimestamp());
        assertEquals(creatorUUID, loaded.getCreator());
        assertEquals("TestFaction", loaded.getFactionName());
        assertEquals(election.getCandidateUUIDs(), loaded.getCandidateUUIDs());
        assertTrue(loaded.isVoter(voterUUID));
    }

    @Test
    void saveWritesEveryFieldAsAString() {
        Map<String, String> data = election.save();

        assertEquals(election.getUUID().toString(), data.get("electionUUID"));
        assertEquals(election.getCreationTimestamp().toString(), data.get("creationTimestamp"));
        assertEquals(creatorUUID.toString(), data.get("creatorUUID"));
        assertEquals("TestFaction", data.get("factionName"));
        assertEquals("[]", data.get("candidateUUIDs"));
        assertEquals("[]", data.get("voterUUIDs"));
    }

    @Test
    void loadReplacesExistingParticipants() {
        election.addCandidate(UUID.randomUUID());
        election.addVoter(UUID.randomUUID());
        Player otherCreator = mock(Player.class);
        when(otherCreator.getUniqueId()).thenReturn(UUID.randomUUID());
        Election other = new Election(otherCreator, "OtherFaction");
        UUID otherCandidate = UUID.randomUUID();
        other.addCandidate(otherCandidate);

        election.load(other.save());

        assertEquals("OtherFaction", election.getFactionName());
        assertEquals(1, election.getCandidateUUIDs().size());
        assertTrue(election.isCandidate(otherCandidate));
        assertFalse(election.isVoter(creatorUUID));
    }

    @Test
    void loadRejectsARecordMissingAField() {
        Map<String, String> data = election.save();
        data.remove("factionName");

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> new Election(data));
        assertTrue(thrown.getMessage().contains("factionName"));
    }

    @Test
    void loadRejectsAMalformedUUID() {
        Map<String, String> data = election.save();
        data.put("creatorUUID", "not-a-uuid");

        assertThrows(IllegalArgumentException.class, () -> new Election(data));
    }
}
