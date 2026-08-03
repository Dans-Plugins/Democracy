package dansplugins.democracy.commands;

import dansplugins.democracy.Democracy;
import dansplugins.democracy.data.PersistentData;
import dansplugins.democracy.factories.ElectionFactory;
import dansplugins.factionsystem.externalapi.MF_Faction;
import dansplugins.factionsystem.externalapi.MedievalFactionsAPI;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StartCommandTest {
    private StartCommand startCommand;
    private PersistentData persistentData;
    private Player player;

    @BeforeEach
    void setUp() {
        UUID playerUUID = UUID.randomUUID();
        player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(playerUUID);

        MF_Faction faction = mock(MF_Faction.class);
        when(faction.getName()).thenReturn("TestFaction");
        when(faction.getOwner()).thenReturn(playerUUID);

        MedievalFactionsAPI medievalFactionsAPI = mock(MedievalFactionsAPI.class);
        when(medievalFactionsAPI.getFaction(player)).thenReturn(faction);

        Democracy democracy = mock(Democracy.class);
        when(democracy.getMedievalFactionsAPI()).thenReturn(medievalFactionsAPI);

        persistentData = new PersistentData();
        ElectionFactory electionFactory = new ElectionFactory(persistentData);
        startCommand = new StartCommand(democracy, electionFactory, persistentData);
    }

    @Test
    void firstElectionInFactionSucceeds() {
        assertTrue(startCommand.execute(player));
        assertTrue(persistentData.getElectionForFaction("TestFaction") != null);
    }

    @Test
    void secondElectionInSameFactionIsRejected() {
        startCommand.execute(player);

        assertFalse(startCommand.execute(player));
    }
}
