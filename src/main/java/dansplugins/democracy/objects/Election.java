package dansplugins.democracy.objects;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

/**
 * @author Daniel McCoy Stephenson
 * @since Februrary 20th, 2022
 */
public class Election {
    private final LocalDateTime creationTimestamp;
    private final ArrayList<Candidate> candidates = new ArrayList<Candidate>();

    public Election() {
       creationTimestamp = LocalDateTime.now();
    }

    public LocalDateTime getCreationTimestamp() {
        return creationTimestamp;
    }

    public boolean isCandidate(UUID playerUUID) {
        return getCandidate(playerUUID) != null;
    }

    public Candidate getCandidate(UUID playerUUID) {
        for (Candidate candidate : candidates) {
            if (candidate.getPlayerUUID().equals(playerUUID)) {
                return candidate;
            }
        }
        return null;
    }

    public boolean addCandidate(Candidate candidate) {
        if (isCandidate(candidate.getPlayerUUID())) {
            return false;
        }
        candidates.add(candidate);
        return true;
    }

    public boolean removeCandidate(Candidate candidate) {
        if (!isCandidate(candidate.getPlayerUUID())) {
            return false;
        }
        candidates.remove(candidate);
        return true;
    }
}