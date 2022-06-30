package dansplugins.democracy.factories;

import java.util.UUID;

import org.bukkit.entity.Player;

import dansplugins.democracy.data.PersistentData;
import dansplugins.democracy.objects.Election;
import dansplugins.democracy.objects.Voter;

public class VoterFactory {
    private final PersistentData persistentData;

    public VoterFactory(PersistentData persistentData) {
        this.persistentData = persistentData;
    }

    public UUID createVoter(Player player, Election election) {
        Voter voter = new Voter(player, election);
        boolean success = persistentData.addVoter(voter);
        if (!success) {
            return null;
        }
        return voter.getPlayerUUID();
    }
}