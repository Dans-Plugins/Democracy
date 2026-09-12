package dansplugins.democracy.objects;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import preponderous.ponder.misc.abs.Savable;

/**
 * @author Daniel McCoy Stephenson
 * @since Februrary 20th, 2022
 */
public class Election implements Savable {
    private UUID electionUUID;
    private LocalDateTime creationTimestamp;
    private UUID creatorUUID;
    private String factionName;
    private final ArrayList<UUID> candidateUUIDs = new ArrayList<>();
    private final ArrayList<UUID> voterUUIDs = new ArrayList<>();

    public Election(Player player, String factionName) {
        electionUUID = UUID.randomUUID();
        creationTimestamp = LocalDateTime.now();
        creatorUUID = player.getUniqueId();
        this.factionName = factionName;
    }

    /**
     * Rebuilds an election from the map produced by {@link #save()}.
     */
    public Election(Map<String, String> data) {
        load(data);
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

    public String getFactionName() {
        return factionName;
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

    public List<UUID> getCandidateUUIDs() {
        return Collections.unmodifiableList(candidateUUIDs);
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
        Map<String, String> data = new HashMap<>();
        data.put("electionUUID", electionUUID.toString());
        data.put("creationTimestamp", creationTimestamp.toString());
        data.put("creatorUUID", creatorUUID.toString());
        data.put("factionName", factionName);
        data.put("candidateUUIDs", SavableFields.uuidListToJson(candidateUUIDs));
        data.put("voterUUIDs", SavableFields.uuidListToJson(voterUUIDs));
        return data;
    }

    /**
     * Replaces this election's state with the given data. Every key written by
     * {@link #save()} is required; a record missing one is rejected with an
     * {@link IllegalArgumentException}.
     */
    @Override
    public void load(Map<String, String> data) {
        electionUUID = UUID.fromString(SavableFields.require(data, "electionUUID"));
        creationTimestamp = LocalDateTime.parse(SavableFields.require(data, "creationTimestamp"));
        creatorUUID = UUID.fromString(SavableFields.require(data, "creatorUUID"));
        factionName = SavableFields.require(data, "factionName");
        candidateUUIDs.clear();
        candidateUUIDs.addAll(SavableFields.uuidListFromJson(SavableFields.require(data, "candidateUUIDs")));
        voterUUIDs.clear();
        voterUUIDs.addAll(SavableFields.uuidListFromJson(SavableFields.require(data, "voterUUIDs")));
    }
}
