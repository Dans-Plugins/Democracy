package dansplugins.democracy.integrators;

import com.dansplugins.factionsystem.MedievalFactions;
import com.dansplugins.factionsystem.faction.MfFaction;
import com.dansplugins.factionsystem.faction.MfFactionService;
import com.dansplugins.factionsystem.faction.permission.MfFactionPermission;
import com.dansplugins.factionsystem.faction.permission.MfFactionPermissions;
import com.dansplugins.factionsystem.faction.role.MfFactionRole;
import com.dansplugins.factionsystem.player.MfPlayer;
import com.dansplugins.factionsystem.player.MfPlayerService;
import com.dansplugins.factionsystem.service.Services;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MedievalFactionsLookupTest {

    private static final String PLAYER_ID = "7f1e3c2a-0000-4000-8000-000000000001";

    private MedievalFactions medievalFactions;
    private MfPlayerService playerService;
    private MfFactionService factionService;
    private MfFactionPermission disband;
    private Player player;
    private MfPlayer mfPlayer;
    private FactionLookup lookup;

    @BeforeEach
    void setUp() {
        medievalFactions = mock(MedievalFactions.class);
        when(medievalFactions.isEnabled()).thenReturn(true);
        Services services = mock(Services.class);
        playerService = mock(MfPlayerService.class);
        factionService = mock(MfFactionService.class);
        when(medievalFactions.getServices()).thenReturn(services);
        when(services.getPlayerService()).thenReturn(playerService);
        when(services.getFactionService()).thenReturn(factionService);
        MfFactionPermissions permissions = mock(MfFactionPermissions.class);
        disband = mock(MfFactionPermission.class);
        when(permissions.getDisband()).thenReturn(disband);
        when(medievalFactions.getFactionPermissions()).thenReturn(permissions);

        player = mock(Player.class);
        mfPlayer = mock(MfPlayer.class);
        when(mfPlayer.getId()).thenReturn(PLAYER_ID);
        when(playerService.getPlayerByBukkitPlayer(player)).thenReturn(mfPlayer);

        lookup = MedievalFactionsLookup.lookUp(medievalFactions);
    }

    private MfFaction inFaction(String name, boolean mayDisband) {
        MfFaction faction = mock(MfFaction.class);
        when(faction.getName()).thenReturn(name);
        MfFactionRole role = mock(MfFactionRole.class);
        when(role.hasPermission(faction, disband)).thenReturn(mayDisband);
        when(faction.getRoleByPlayerId(PLAYER_ID)).thenReturn(role);
        when(factionService.getFactionByPlayerId(PLAYER_ID)).thenReturn(faction);
        return faction;
    }

    @Test
    void lookUpNeedsAnEnabledMedievalFactions() {
        assertNotNull(lookup);
        assertNull(MedievalFactionsLookup.lookUp(null));
        when(medievalFactions.isEnabled()).thenReturn(false);
        assertNull(MedievalFactionsLookup.lookUp(medievalFactions));
        Plugin other = mock(Plugin.class);
        when(other.isEnabled()).thenReturn(true);
        assertNull(MedievalFactionsLookup.lookUp(other));
    }

    @Test
    void aPlayerInNoFactionHasNoFactionNameAndLeadsNothing() {
        assertNull(lookup.getFactionName(player));
        assertFalse(lookup.leadsFaction(player));
    }

    @Test
    void aPlayerUnknownToMedievalFactionsHasNoFaction() {
        when(playerService.getPlayerByBukkitPlayer(player)).thenReturn(null);
        assertNull(lookup.getFactionName(player));
        assertFalse(lookup.leadsFaction(player));
    }

    @Test
    void aMemberHasTheFactionNameButDoesNotLeadIt() {
        inFaction("Alpha", false);
        assertEquals("Alpha", lookup.getFactionName(player));
        assertFalse(lookup.leadsFaction(player));
    }

    @Test
    void theHolderOfTheDisbandPermissionLeadsTheFaction() {
        inFaction("Alpha", true);
        assertTrue(lookup.leadsFaction(player));
    }
}
