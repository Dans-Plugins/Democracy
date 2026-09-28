package dansplugins.democracy.commands;

import dansplugins.democracy.Democracy;
import dansplugins.democracy.data.PersistentData;
import dansplugins.democracy.factories.ElectionFactory;
import dansplugins.democracy.integrators.FactionLookup;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StartCommandTest {
    private StartCommand startCommand;
    private PersistentData persistentData;
    private Player player;
    private FactionLookup factionLookup;

    @BeforeEach
    void setUp() {
        UUID playerUUID = UUID.randomUUID();
        player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(playerUUID);

        factionLookup = mock(FactionLookup.class);
        when(factionLookup.getFactionName(player)).thenReturn("TestFaction");
        when(factionLookup.leadsFaction(player)).thenReturn(true);

        Democracy democracy = mock(Democracy.class);
        when(democracy.getFactionLookup()).thenReturn(factionLookup);

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
    void successMessageIsGreenRatherThanTheColourUsedForErrors() {
        startCommand.execute(player);

        verify(player).sendMessage(ChatColor.GREEN + "Election has been started.");
    }

    @Test
    void secondElectionInSameFactionIsRejected() {
        startCommand.execute(player);

        assertFalse(startCommand.execute(player));
    }

    @Test
    void aMemberWhoDoesNotLeadTheFactionCannotStartAnElection() {
        when(factionLookup.leadsFaction(player)).thenReturn(false);

        assertFalse(startCommand.execute(player));
        assertTrue(persistentData.getElectionForFaction("TestFaction") == null);
    }
}
