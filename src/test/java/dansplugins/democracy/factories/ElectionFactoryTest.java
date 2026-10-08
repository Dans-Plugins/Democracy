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

class ElectionFactoryTest {
    private PersistentData persistentData;
    private ElectionFactory electionFactory;
    private Player player;
    private UUID playerUUID;

    @BeforeEach
    void setUp() {
        persistentData = new PersistentData();
        electionFactory = new ElectionFactory(persistentData);
        playerUUID = UUID.randomUUID();
        player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(playerUUID);
    }

    @Test
    void createElectionRegistersInPersistentData() {
        UUID result = electionFactory.createElection(player, "TestFaction");

        assertNotNull(result);
        Election election = persistentData.getElection(result);
        assertNotNull(election);
        assertEquals("TestFaction", election.getFactionName());
        assertEquals(playerUUID, election.getCreator());
    }

    @Test
    void createElectionStartsWithNoCandidatesOrVoters() {
        electionFactory.createElection(player, "TestFaction");

        assertTrue(persistentData.getCandidates().isEmpty());
        assertTrue(persistentData.getVoters().isEmpty());
    }

    // The factory does not guard against a second election for the same faction;
    // StartCommand checks getElectionForFaction before calling it.
    @Test
    void createElectionDoesNotRejectASecondElectionForTheSameFaction() {
        UUID first = electionFactory.createElection(player, "TestFaction");
        UUID second = electionFactory.createElection(player, "TestFaction");

        assertNotNull(second);
        assertNotEquals(first, second);
        assertEquals(2, persistentData.getElections().size());
    }
}
