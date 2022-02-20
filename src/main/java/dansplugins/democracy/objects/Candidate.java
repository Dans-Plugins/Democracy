package dansplugins.democracy.objects;

import java.util.ArrayList;
import java.util.UUID;

/**
 * @author Daniel McCoy Stephenson
 * @since Februrary 20th, 2022
 */
public class Candidate {
    private final UUID playerUUID;
    private final ArrayList<Voter> voters = new ArrayList<>();

    public Candidate(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public int getNumVoter() {
        return voters.size();
    }

    public boolean isVoter(UUID playerUUID) {
        return getVoter(playerUUID) != null;
    }

    public Voter getVoter(UUID playerUUID) {
        for (Voter voter : voters) {
            if (voter.getPlayerUUID().equals(playerUUID)) {
                return voter;
            }
        }
        return null;
    }

    public boolean addVoter(Voter voter) {
        if (isVoter(voter.getPlayerUUID())) {
            return false;
        }
        voters.add(voter);
        return true;
    }

    public boolean removeVoter(Voter voter) {
        if (!isVoter(voter.getPlayerUUID())) {
            return false;
        }
        voters.remove(voter);
        return true;
    }
}