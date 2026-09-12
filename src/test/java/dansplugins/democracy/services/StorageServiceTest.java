package dansplugins.democracy.services;

import dansplugins.democracy.Democracy;
import dansplugins.democracy.data.PersistentData;
import dansplugins.democracy.objects.Candidate;
import dansplugins.democracy.objects.Election;
import dansplugins.democracy.objects.Voter;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StorageServiceTest {
    @TempDir
    Path tempDir;

    private File dataFolder;
    private PersistentData persistentData;
    private StorageService storageService;
    private Player owner;
    private Player voterPlayer;

    @BeforeEach
    void setUp() {
        dataFolder = tempDir.resolve("Democracy").toFile();

        Democracy democracy = mock(Democracy.class);
        when(democracy.getDataFolder()).thenReturn(dataFolder);
        when(democracy.getLogger()).thenReturn(Logger.getLogger("StorageServiceTest"));

        owner = mock(Player.class);
        when(owner.getUniqueId()).thenReturn(UUID.randomUUID());
        voterPlayer = mock(Player.class);
        when(voterPlayer.getUniqueId()).thenReturn(UUID.randomUUID());

        persistentData = new PersistentData();
        storageService = new StorageService(democracy, persistentData);
    }

    @Test
    void loadWithNoFilesSucceedsAndLeavesNothingBehind() {
        assertTrue(storageService.load());

        assertTrue(persistentData.getElections().isEmpty());
        assertTrue(persistentData.getCandidates().isEmpty());
        assertTrue(persistentData.getVoters().isEmpty());
    }

    @Test
    void saveIsRefusedUntilALoadHasSucceeded() {
        // Bukkit runs onDisable() even when onEnable() threw before load() was reached, and a
        // save at that point would write empty lists over whatever is on disk.
        assertFalse(storageService.save());

        assertFalse(dataFolder.exists());
    }

    @Test
    void saveCreatesTheDataFolderAndOneFilePerRecordType() {
        assertTrue(storageService.load());
        assertFalse(dataFolder.exists());

        assertTrue(storageService.save());

        assertTrue(new File(dataFolder, StorageService.ELECTIONS_FILE).isFile());
        assertTrue(new File(dataFolder, StorageService.CANDIDATES_FILE).isFile());
        assertTrue(new File(dataFolder, StorageService.VOTERS_FILE).isFile());
    }

    @Test
    void saveLeavesNoTemporaryFilesBehind() {
        assertTrue(storageService.load());

        assertTrue(storageService.save());
        assertTrue(storageService.save());

        String[] names = dataFolder.list();
        assertNotNull(names);
        assertEquals(3, names.length, String.join(", ", names));
        for (String name : names) {
            assertTrue(name.endsWith(".json"), name);
        }
    }

    @Test
    void saveThenLoadRestoresAnElectionWithItsCandidatesAndVotes() {
        assertTrue(storageService.load());
        Election election = new Election(owner, "TestFaction");
        Candidate candidate = new Candidate(owner, election);
        Voter voter = new Voter(voterPlayer, election);
        election.addCandidate(owner.getUniqueId());
        election.addVoter(voterPlayer.getUniqueId());
        candidate.addVoter(voterPlayer.getUniqueId());
        persistentData.addElection(election);
        persistentData.addCandidate(candidate);
        persistentData.addVoter(voter);
        assertTrue(storageService.save());

        // a fresh PersistentData stands in for the one a restarted server starts with
        PersistentData restarted = new PersistentData();
        Democracy democracy = mock(Democracy.class);
        when(democracy.getDataFolder()).thenReturn(dataFolder);
        when(democracy.getLogger()).thenReturn(Logger.getLogger("StorageServiceTest"));
        assertTrue(new StorageService(democracy, restarted).load());

        Election loadedElection = restarted.getElectionForFaction("TestFaction");
        assertNotNull(loadedElection);
        assertEquals(election.getUUID(), loadedElection.getUUID());
        assertEquals(owner.getUniqueId(), loadedElection.getCreator());
        assertTrue(loadedElection.isCandidate(owner.getUniqueId()));
        assertTrue(loadedElection.isVoter(voterPlayer.getUniqueId()));

        Candidate loadedCandidate = restarted.getCandidate(election.getUUID(), owner.getUniqueId());
        assertNotNull(loadedCandidate);
        assertEquals(1, loadedCandidate.getNumVoter());
        assertTrue(loadedCandidate.isVoter(voterPlayer.getUniqueId()));

        assertNotNull(restarted.getVoter(election.getUUID(), voterPlayer.getUniqueId()));
    }

    @Test
    void saveWritesTheRecordsAsJsonArraysOfStringMaps() throws IOException {
        assertTrue(storageService.load());
        Election election = new Election(owner, "TestFaction");
        persistentData.addElection(election);
        storageService.save();

        String json = new String(Files.readAllBytes(new File(dataFolder, StorageService.ELECTIONS_FILE).toPath()), StandardCharsets.UTF_8);

        assertTrue(json.trim().startsWith("["), json);
        assertTrue(json.contains("\"electionUUID\": \"" + election.getUUID() + "\""), json);
        assertTrue(json.contains("\"factionName\": \"TestFaction\""), json);
    }

    @Test
    void loadFailsOnMalformedJsonWithoutTouchingPersistentData() throws IOException {
        writeFile(StorageService.ELECTIONS_FILE, "this is not json");

        assertFalse(storageService.load());

        assertTrue(persistentData.getElections().isEmpty());
    }

    @Test
    void loadAddsNothingWhenOnlyTheLastFileIsCorrupt() throws IOException {
        assertTrue(storageService.load());
        Election election = new Election(owner, "TestFaction");
        persistentData.addElection(election);
        storageService.save();
        writeFile(StorageService.VOTERS_FILE, "[{\"playerUUID\": \"not-a-uuid\"}]");
        PersistentData restarted = new PersistentData();
        Democracy democracy = mock(Democracy.class);
        when(democracy.getDataFolder()).thenReturn(dataFolder);
        when(democracy.getLogger()).thenReturn(Logger.getLogger("StorageServiceTest"));

        assertFalse(new StorageService(democracy, restarted).load());

        // the valid elections.json must not have been half-applied
        assertTrue(restarted.getElections().isEmpty());
    }

    @Test
    void saveIsRefusedAfterAFailedLoadSoTheFilesOnDiskSurvive() throws IOException {
        writeFile(StorageService.ELECTIONS_FILE, "this is not json");
        assertFalse(storageService.load());

        assertFalse(storageService.save());

        String stillOnDisk = new String(Files.readAllBytes(new File(dataFolder, StorageService.ELECTIONS_FILE).toPath()), StandardCharsets.UTF_8);
        assertEquals("this is not json", stillOnDisk);
        assertFalse(new File(dataFolder, StorageService.CANDIDATES_FILE).exists());
    }

    @Test
    void saveIsAllowedAgainOnceALoadSucceeds() throws IOException {
        writeFile(StorageService.ELECTIONS_FILE, "this is not json");
        assertFalse(storageService.load());
        writeFile(StorageService.ELECTIONS_FILE, "[]");

        assertTrue(storageService.load());
        assertTrue(storageService.save());
    }

    private void writeFile(String name, String content) throws IOException {
        Files.createDirectories(dataFolder.toPath());
        Files.write(new File(dataFolder, name).toPath(), content.getBytes(StandardCharsets.UTF_8));
    }
}
