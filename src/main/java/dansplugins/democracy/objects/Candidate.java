package dansplugins.democracy.objects;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.entity.Player;

/**
 * @author Daniel McCoy Stephenson
 * @since Februrary 20th, 2022
 */
public class Candidate {
    private final UUID playerUUID;
    private final UUID electionUUID;
    private final ArrayList<UUID> voterUUIDs = new ArrayList<>();

    public Candidate(Player player, Election election) {
        playerUUID = player.getUniqueId();
        electionUUID = election.getUUID();
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
}