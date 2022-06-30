package dansplugins.democracy.factories;

import java.util.UUID;

import org.bukkit.entity.Player;

import dansplugins.democracy.data.PersistentData;
import dansplugins.democracy.objects.Election;

public class ElectionFactory {
    private final PersistentData persistentData;

    public ElectionFactory(PersistentData persistentData) {
        this.persistentData = persistentData;
    }

    public UUID createElection(Player player) {
        Election election = new Election(player);
        boolean success = persistentData.addElection(election);
        if (!success) {
            return null;
        }
        // TODO: add faction members to election as voters
        // TODO: inform faction that election has begun
        // TODO: send mail to faction members via the Mailboxes plugin
        return election.getUUID();
    }
}