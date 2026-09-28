package dansplugins.democracy.commands;

import dansplugins.democracy.Democracy;
import dansplugins.democracy.data.PersistentData;
import dansplugins.democracy.factories.CandidateFactory;
import dansplugins.democracy.objects.Election;
import dansplugins.democracy.integrators.FactionLookup;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class InfoCommandTest {
    private FactionLookup factionLookup;
    private InfoCommand infoCommand;
    private PersistentData persistentData;
    private Player player;
    private MockedStatic<Bukkit> bukkit;

    @BeforeEach
    void setUp() {
        UUID playerUUID = UUID.randomUUID();
        player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(playerUUID);

        OfflinePlayer offlinePlayer = mock(OfflinePlayer.class);
        when(offlinePlayer.getName()).thenReturn("PlayerName");
        bukkit = mockStatic(Bukkit.class);
        bukkit.when(() -> Bukkit.getOfflinePlayer(playerUUID)).thenReturn(offlinePlayer);

        factionLookup = mock(FactionLookup.class);
        when(factionLookup.getFactionName(player)).thenReturn("TestFaction");

        Democracy democracy = mock(Democracy.class);
        when(democracy.getFactionLookup()).thenReturn(factionLookup);

        persistentData = new PersistentData();
        infoCommand = new InfoCommand(democracy, persistentData);
    }

    @AfterEach
    void tearDown() {
        bukkit.close();
    }

    @Test
    void failsWhenNoElectionInProgress() {
        assertFalse(infoCommand.execute(player));
    }

    @Test
    void succeedsWhenElectionInProgress() {
        Election election = new Election(player, "TestFaction");
        persistentData.addElection(election);
        new CandidateFactory(persistentData).createCandidate(player, election);

        assertTrue(infoCommand.execute(player));
    }
}
