package dansplugins.democracy.objects;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import preponderous.ponder.misc.abs.Savable;

/**
 * @author Daniel McCoy Stephenson
 * @since Februrary 20th, 2022
 */
public class Voter implements Savable {
    private UUID playerUUID;
    private UUID electionUUID;

    public Voter(Player player, Election election) {
        playerUUID = player.getUniqueId();
        electionUUID = election.getUUID();
    }

    /**
     * Rebuilds a voter from the map produced by {@link #save()}.
     */
    public Voter(Map<String, String> data) {
        load(data);
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public UUID getElectionUUID() {
        return electionUUID;
    }

    @Override
    public Map<String, String> save() {
        Map<String, String> data = new HashMap<>();
        data.put("playerUUID", playerUUID.toString());
        data.put("electionUUID", electionUUID.toString());
        return data;
    }

    /**
     * Replaces this voter's state with the given data. Every key written by
     * {@link #save()} is required; a record missing one is rejected with an
     * {@link IllegalArgumentException}.
     */
    @Override
    public void load(Map<String, String> data) {
        playerUUID = UUID.fromString(SavableFields.require(data, "playerUUID"));
        electionUUID = UUID.fromString(SavableFields.require(data, "electionUUID"));
    }
}
