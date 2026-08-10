package dansplugins.democracy.factories;

import dansplugins.democracy.data.PersistentData;
import dansplugins.democracy.objects.Election;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CandidateFactoryTest {
    private PersistentData persistentData;
    private CandidateFactory candidateFactory;
    private Election election;
    private Player player;
    private UUID playerUUID;

    @BeforeEach
    void setUp() {
        persistentData = new PersistentData();
        candidateFactory = new CandidateFactory(persistentData);
        playerUUID = UUID.randomUUID();
        player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(playerUUID);
        election = new Election(player, "TestFaction");
    }

    @Test
    void createCandidateRegistersInPersistentDataAndElection() {
        UUID result = candidateFactory.createCandidate(player, election);

        assertEquals(playerUUID, result);
        assertNotNull(persistentData.getCandidate(election.getUUID(), playerUUID));
        assertTrue(election.isCandidate(playerUUID));
    }

    @Test
    void createCandidateReturnsNullOnDuplicate() {
        candidateFactory.createCandidate(player, election);
        UUID result = candidateFactory.createCandidate(player, election);

        assertNull(result);
    }

    @Test
    void createCandidateSucceedsForTheSamePlayerInAnotherElection() {
        candidateFactory.createCandidate(player, election);
        Election otherElection = new Election(player, "OtherFaction");

        assertEquals(playerUUID, candidateFactory.createCandidate(player, otherElection));
        assertTrue(otherElection.isCandidate(playerUUID));
    }
}
