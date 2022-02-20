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
    private final ArrayList<Candidate> candidates = new ArrayList<Candidate>();
    private final ArrayList<Voter> voters = new ArrayList<Voter>();

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