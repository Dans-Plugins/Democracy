package dansplugins.democracy.data;

import java.util.ArrayList;
import java.util.UUID;

import dansplugins.democracy.objects.Candidate;
import dansplugins.democracy.objects.Election;
import dansplugins.democracy.objects.Voter;

public class PersistentData {
    private final ArrayList<Election> elections = new ArrayList<>();
    private final ArrayList<Candidate> candidates = new ArrayList<>();
    private final ArrayList<Voter> voters = new ArrayList<>();

    public Election getElection(UUID electionUUID) {
        for (Election election : elections) {
            if (election.getUUID().equals(electionUUID)) {
                return election;
            }
        }
        return null;
    }

    public Election getElectionForFaction(String factionName) {
        for (Election election : elections) {
            if (election.getFactionName().equalsIgnoreCase(factionName)) {
                return election;
            }
        }
        return null;
    }

    public boolean addElection(Election election) {
        if (isElection(election)) {
            return false;
        }
        elections.add(election);
        return true;
    }

    public boolean removeElection(Election election) {
        if (!isElection(election)) {
            return false;
        }
        elections.remove(election);
        return true;
    }

    public Candidate getCandidate(UUID electionUUID, UUID playerUUID) {
        for (Candidate candidate : candidates) {
            if (candidate.getElectionUUID().equals(electionUUID) && candidate.getPlayerUUID().equals(playerUUID)) {
                return candidate;
            }
        }
        return null;
    }

    public boolean addCandidate(Candidate candidate) {
        if (isCandidate(candidate)) {
            return false;
        }
        candidates.add(candidate);
        return true;
    }

    public boolean removeCandidate(Candidate candidate) {
        if (!isCandidate(candidate)) {
            return false;
        }
        candidates.remove(candidate);
        return true;
    }

    public Voter getVoter(UUID electionUUID, UUID playerUUID) {
        for (Voter voter : voters) {
            if (voter.getElectionUUID().equals(electionUUID) && voter.getPlayerUUID().equals(playerUUID)) {
                return voter;
            }
        }
        return null;
    }

    public boolean addVoter(Voter voter) {
        if (isVoter(voter)) {
            return false;
        }
        voters.add(voter);
        return true;
    }

    public boolean removeVoter(Voter voter) {
        if (!isVoter(voter)) {
            return false;
        }
        voters.remove(voter);
        return true;
    }

    private boolean isElection(Election election) {
        return getElection(election.getUUID()) != null;
    }

    private boolean isCandidate(Candidate candidate) {
        return getCandidate(candidate.getElectionUUID(), candidate.getPlayerUUID()) != null;
    }

    private boolean isVoter(Voter voter) {
        return getVoter(voter.getElectionUUID(), voter.getPlayerUUID()) != null;
    }

}