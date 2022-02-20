package dansplugins.democracy.objects;

import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import preponderous.ponder.misc.abs.Savable;

/**
 * @author Daniel McCoy Stephenson
 * @since Februrary 20th, 2022
 */
public class Voter implements Savable {
    private final UUID playerUUID;
    private final UUID electionUUID;

    public Voter(Player player, Election election) {
        playerUUID = player.getUniqueId();
        electionUUID = election.getUUID();
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public UUID getElectionUUID() {
        return electionUUID;
    }

    @Override
    public Map<String, String> save() {
        // TODO: implement
        return null;
    }

    @Override
    public void load(Map<String, String> data) {
        // TODO: implement
    }
}