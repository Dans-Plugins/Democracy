package dansplugins.democracy.factories;

import java.util.UUID;

import org.bukkit.entity.Player;

import dansplugins.democracy.data.PersistentData;
import dansplugins.democracy.objects.Election;

public class ElectionFactory {
    private static ElectionFactory instance;

    private ElectionFactory() {

    }
    
    public static ElectionFactory getInstance() {
        if (instance == null) {
            instance = new ElectionFactory();
        }
        return instance;
    }

    public UUID createElection(Player player) {
        Election election = new Election(player);
        boolean success = PersistentData.getInstance().addElection(election);
        if (!success) {
            return null;
        }
        return election.getUUID();
    }
}