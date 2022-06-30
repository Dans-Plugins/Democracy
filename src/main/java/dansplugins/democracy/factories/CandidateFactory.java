package dansplugins.democracy.factories;

import java.util.UUID;

import org.bukkit.entity.Player;

import dansplugins.democracy.data.PersistentData;
import dansplugins.democracy.objects.Candidate;
import dansplugins.democracy.objects.Election;

public class CandidateFactory {
    private final PersistentData persistentData;

    public CandidateFactory(PersistentData persistentData) {
        this.persistentData = persistentData;
    }

    public UUID createCandidate(Player player, Election election) {
        Candidate candidate = new Candidate(player, election);
        boolean success = persistentData.addCandidate(candidate);
        if (!success) {
            return null;
        }
        return candidate.getPlayerUUID();
    }
}