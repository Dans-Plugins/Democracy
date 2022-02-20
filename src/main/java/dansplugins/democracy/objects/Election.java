package dansplugins.democracy.objects;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

/**
 * @author Daniel McCoy Stephenson
 * @since Februrary 20th, 2022
 */
public class Election {
    private final UUID electionUUID;
    private final LocalDateTime creationTimestamp;
    private final ArrayList<UUID> candidateUUIDs = new ArrayList<>();
    private final ArrayList<UUID> voterUUIDs = new ArrayList<>();

    public Election() {
        electionUUID = UUID.randomUUID();
        creationTimestamp = LocalDateTime.now();
    }

    public UUID getUUID() {
        return electionUUID;
    }

    public LocalDateTime getCreationTimestamp() {
        return creationTimestamp;
    }

    public boolean isCandidate(UUID playerUUID) {
        for (UUID candidate : candidateUUIDs) {
            if (candidate.equals(playerUUID)) {
                return true;
            }
        }
        return false;
    }

    public boolean addCandidate(UUID playerUUID) {
        if (isCandidate(playerUUID)) {
            return false;
        }
        candidateUUIDs.add(playerUUID);
        return true;
    }

    public boolean removeCandidate(UUID playerUUID) {
        if (!isCandidate(playerUUID)) {
            return false;
        }
        candidateUUIDs.remove(playerUUID);
        return true;
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