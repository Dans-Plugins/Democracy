package dansplugins.democracy.objects;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.entity.Player;

/**
 * @author Daniel McCoy Stephenson
 * @since Februrary 20th, 2022
 */
public class Voter {
    private final UUID playerUUID;
    private final UUID electionUUID;
    private final ArrayList<UUID> voters = new ArrayList<>();

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

    public int getNumElectors() {
        return voters.size();
    }
}