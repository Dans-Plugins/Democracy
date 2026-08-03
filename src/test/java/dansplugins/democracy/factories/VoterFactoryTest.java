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

class VoterFactoryTest {
    private PersistentData persistentData;
    private VoterFactory voterFactory;
    private Election election;
    private Player player;
    private UUID playerUUID;

    @BeforeEach
    void setUp() {
        persistentData = new PersistentData();
        voterFactory = new VoterFactory(persistentData);
        playerUUID = UUID.randomUUID();
        player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(playerUUID);
        election = new Election(player, "TestFaction");
    }

    @Test
    void createVoterRegistersInPersistentDataAndElection() {
        UUID result = voterFactory.createVoter(player, election);

        assertEquals(playerUUID, result);
        assertNotNull(persistentData.getVoter(playerUUID));
        assertTrue(election.isVoter(playerUUID));
    }

    @Test
    void createVoterReturnsNullOnDuplicate() {
        voterFactory.createVoter(player, election);
        UUID result = voterFactory.createVoter(player, election);

        assertNull(result);
    }
}
