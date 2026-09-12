package dansplugins.democracy.objects;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VoterTest {
    private Voter voter;
    private Election election;
    private UUID playerUUID;

    @BeforeEach
    void setUp() {
        playerUUID = UUID.randomUUID();
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(playerUUID);
        election = new Election(player, "TestFaction");
        voter = new Voter(player, election);
    }

    @Test
    void constructorSetsPlayerAndElection() {
        assertEquals(playerUUID, voter.getPlayerUUID());
        assertEquals(election.getUUID(), voter.getElectionUUID());
    }

    @Test
    void saveThenLoadRebuildsAnIdenticalVoter() {
        Voter loaded = new Voter(voter.save());

        assertEquals(playerUUID, loaded.getPlayerUUID());
        assertEquals(election.getUUID(), loaded.getElectionUUID());
    }

    @Test
    void saveWritesEveryFieldAsAString() {
        Map<String, String> data = voter.save();

        assertEquals(playerUUID.toString(), data.get("playerUUID"));
        assertEquals(election.getUUID().toString(), data.get("electionUUID"));
        assertEquals(2, data.size());
    }

    @Test
    void loadRejectsARecordMissingAField() {
        Map<String, String> data = voter.save();
        data.remove("playerUUID");

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> new Voter(data));
        assertTrue(thrown.getMessage().contains("playerUUID"));
    }
}
