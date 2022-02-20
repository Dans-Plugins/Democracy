package dansplugins.democracy.factories;

import java.util.UUID;

import org.bukkit.entity.Player;

import dansplugins.democracy.data.PersistentData;
import dansplugins.democracy.objects.Election;
import dansplugins.democracy.objects.Voter;

public class VoterFactory {
    private static VoterFactory instance;

    private VoterFactory() {

    }

    public static VoterFactory getInstance() {
        if (instance == null) {
            instance = new VoterFactory();
        }
        return instance;
    }

    public UUID createVoter(Player player, Election election) {
        Voter voter = new Voter(player, election);
        boolean success = PersistentData.getInstance().addVoter(voter);
        if (!success) {
            return null;
        }
        return voter.getPlayerUUID();
    }
}