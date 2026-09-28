package dansplugins.democracy.integrators;

import com.dansplugins.factionsystem.MedievalFactions;
import com.dansplugins.factionsystem.faction.MfFaction;
import com.dansplugins.factionsystem.faction.role.MfFactionRole;
import com.dansplugins.factionsystem.player.MfPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * {@link FactionLookup} backed by the running Medieval Factions (5.x/6.x API,
 * {@code com.dansplugins.factionsystem}). Medieval Factions is a provided dependency: its
 * classes come from the installed plugin, never from a copy bundled in Democracy's jar.
 */
public class MedievalFactionsLookup implements FactionLookup {

    private final MedievalFactions medievalFactions;

    MedievalFactionsLookup(MedievalFactions medievalFactions) {
        this.medievalFactions = medievalFactions;
    }

    /**
     * The lookup over the enabled Medieval Factions, or null when it is not enabled (or is a
     * build whose classes do not match). Call from onEnable(): depend: [MedievalFactions] means
     * it has been enabled by then.
     */
    public static FactionLookup lookUp(Plugin plugin) {
        if (plugin == null || !plugin.isEnabled()) {
            return null;
        }
        try {
            return plugin instanceof MedievalFactions ? new MedievalFactionsLookup((MedievalFactions) plugin) : null;
        } catch (NoClassDefFoundError e) {
            return null;
        }
    }

    private MfFaction factionOf(MfPlayer mfPlayer) {
        return medievalFactions.getServices().getFactionService().getFactionByPlayerId(mfPlayer.getId());
    }

    private MfPlayer mfPlayerOf(Player player) {
        return medievalFactions.getServices().getPlayerService().getPlayerByBukkitPlayer(player);
    }

    @Override
    public String getFactionName(Player player) {
        MfPlayer mfPlayer = mfPlayerOf(player);
        if (mfPlayer == null) {
            return null;
        }
        MfFaction faction = factionOf(mfPlayer);
        return faction == null ? null : faction.getName();
    }

    @Override
    public boolean leadsFaction(Player player) {
        MfPlayer mfPlayer = mfPlayerOf(player);
        if (mfPlayer == null) {
            return false;
        }
        MfFaction faction = factionOf(mfPlayer);
        if (faction == null) {
            return false;
        }
        MfFactionRole role = faction.getRoleByPlayerId(mfPlayer.getId());
        return role != null && role.hasPermission(faction, medievalFactions.getFactionPermissions().getDisband());
    }
}
