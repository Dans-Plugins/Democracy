package dansplugins.democracy.integrators;

import org.bukkit.entity.Player;

/**
 * What Democracy needs to know about a player's faction. Elections are keyed by faction name.
 * Kept to these two questions so the commands do not depend on the faction plugin's classes.
 */
public interface FactionLookup {

    /** The name of the faction the player belongs to, or null when they are in none. */
    String getFactionName(Player player);

    /**
     * Whether the player leads their faction, and so may start an election: they hold the
     * permission to disband it, which only the Owner role has by default. False when they are
     * in no faction.
     */
    boolean leadsFaction(Player player);
}
