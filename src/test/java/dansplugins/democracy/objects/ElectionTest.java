package dansplugins.democracy.objects;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
}
