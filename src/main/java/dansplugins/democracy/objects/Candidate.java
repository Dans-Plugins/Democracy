package dansplugins.democracy.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import preponderous.ponder.misc.abs.Savable;

/**
 * @author Daniel McCoy Stephenson
 * @since Februrary 20th, 2022
 */
public class Candidate implements Savable {
    private UUID playerUUID;
    private UUID electionUUID;
    private final ArrayList<UUID> voterUUIDs = new ArrayList<>();

    public Candidate(Player player, Election election) {
        playerUUID = player.getUniqueId();
        electionUUID = election.getUUID();
    }

    /**
     * Rebuilds a candidate from the map produced by {@link #save()}.
     */
    public Candidate(Map<String, String> data) {
        load(data);
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public UUID getElectionUUID() {
        return electionUUID;
    }

    public int getNumVoter() {
        return voterUUIDs.size();
    }

    public boolean isVoter(UUID playerUUID) {
        for (UUID voter : voterUUIDs) {
            if (voter.equals(playerUUID)) {
                return true;
            }
        }
        return false;
    }

    public boolean addVoter(UUID playerUUID) {
        if (isVoter(playerUUID)) {
            return false;
        }
        voterUUIDs.add(playerUUID);
        return true;
    }

    public boolean removeVoter(UUID playerUUID) {
        if (!isVoter(playerUUID)) {
            return false;
        }
        voterUUIDs.remove(playerUUID);
        return true;
    }

    @Override
    public Map<String, String> save() {
        Map<String, String> data = new HashMap<>();
        data.put("playerUUID", playerUUID.toString());
        data.put("electionUUID", electionUUID.toString());
        data.put("voterUUIDs", SavableFields.uuidListToJson(voterUUIDs));
        return data;
    }

    /**
     * Replaces this candidate's state with the given data. Every key written by
     * {@link #save()} is required; a record missing one is rejected with an
     * {@link IllegalArgumentException}.
     */
    @Override
    public void load(Map<String, String> data) {
        playerUUID = UUID.fromString(SavableFields.require(data, "playerUUID"));
        electionUUID = UUID.fromString(SavableFields.require(data, "electionUUID"));
        voterUUIDs.clear();
        voterUUIDs.addAll(SavableFields.uuidListFromJson(SavableFields.require(data, "voterUUIDs")));
    }
}
