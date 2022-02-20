package dansplugins.democracy.factories;

import java.util.UUID;

import org.bukkit.entity.Player;

import dansplugins.democracy.data.PersistentData;
import dansplugins.democracy.objects.Candidate;
import dansplugins.democracy.objects.Election;

public class CandidateFactory {
    private static CandidateFactory instance;

    private CandidateFactory() {

    }

    public static CandidateFactory getInstance() {
        if (instance == null) {
            instance = new CandidateFactory();
        }
        return instance;
    }

    public UUID createCandidate(Player player, Election election) {
        Candidate candidate = new Candidate(player, election);
        boolean success = PersistentData.getInstance().addCandidate(candidate);
        if (!success) {
            return null;
        }
        return candidate.getPlayerUUID();
    }
}