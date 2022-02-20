package dansplugins.democracy.objects;

import java.util.ArrayList;
import java.util.UUID;

/**
 * @author Daniel McCoy Stephenson
 * @since Februrary 20th, 2022
 */
public class Voter {
    private final UUID playerUUID;
    private final ArrayList<UUID> voters = new ArrayList<>();

    public Voter(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public int getNumElectors() {
        return voters.size();
    }
}