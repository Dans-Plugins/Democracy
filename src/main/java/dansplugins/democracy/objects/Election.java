package dansplugins.democracy.objects;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import preponderous.ponder.misc.abs.Savable;

/**
 * @author Daniel McCoy Stephenson
 * @since Februrary 20th, 2022
 */
public class Election implements Savable {
    private final UUID electionUUID;
    private final LocalDateTime creationTimestamp;
    private final UUID creatorUUID;
    private final ArrayList<UUID> candidateUUIDs = new ArrayList<>();
    private final ArrayList<UUID> voterUUIDs = new ArrayList<>();

    public Election(Player player) {
        electionUUID = UUID.randomUUID();
        creationTimestamp = LocalDateTime.now();
        creatorUUID = player.getUniqueId();
    }

    public UUID getUUID() {
        return electionUUID;
    }

    public LocalDateTime getCreationTimestamp() {
        return creationTimestamp;
    }

    public UUID getCreator() {
        return creatorUUID;
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

    @Override
    public Map<String, String> save() {
        // TODO: implement
        return null;
    }

    @Override
    public void load(Map<String, String> data) {
        // TODO: implement
    }
}