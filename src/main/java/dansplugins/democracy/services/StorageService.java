package dansplugins.democracy.services;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import dansplugins.democracy.Democracy;
import dansplugins.democracy.data.PersistentData;
import dansplugins.democracy.objects.Candidate;
import dansplugins.democracy.objects.Election;
import dansplugins.democracy.objects.Voter;
import preponderous.ponder.misc.abs.Savable;

/**
 * Reads and writes the election state held in {@link PersistentData} as JSON files in the
 * plugin's data folder, one file per record type. Each file is a JSON array of the maps the
 * records' {@link Savable#save()} methods produce.
 * @author Daniel McCoy Stephenson
 */
public class StorageService {
    static final String ELECTIONS_FILE = "elections.json";
    static final String CANDIDATES_FILE = "candidates.json";
    static final String VOTERS_FILE = "voters.json";

    private static final Type RECORD_LIST_TYPE = new TypeToken<List<Map<String, String>>>() {}.getType();

    private final Democracy democracy;
    private final PersistentData persistentData;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // Set when load() could not read what is on disk. save() then refuses to run, because
    // writing the (empty) in-memory state over the files would destroy the very data that
    // failed to load, with no way back for the server owner.
    private boolean loadFailed = false;

    public StorageService(Democracy democracy, PersistentData persistentData) {
        this.democracy = democracy;
        this.persistentData = persistentData;
    }

    /**
     * Loads every stored election, candidate and voter into {@link PersistentData}. A file that
     * does not exist yet is treated as empty. Nothing is added to {@link PersistentData} until
     * all three files have been read and parsed, so a failure leaves it exactly as it was.
     * @return Whether the data was loaded. On failure the cause has been logged and subsequent
     *         calls to {@link #save()} are refused.
     */
    public boolean load() {
        File dataFolder = democracy.getDataFolder();
        try {
            List<Map<String, String>> electionRecords = readRecords(new File(dataFolder, ELECTIONS_FILE));
            List<Map<String, String>> candidateRecords = readRecords(new File(dataFolder, CANDIDATES_FILE));
            List<Map<String, String>> voterRecords = readRecords(new File(dataFolder, VOTERS_FILE));

            List<Election> elections = new ArrayList<>();
            for (Map<String, String> record : electionRecords) {
                elections.add(new Election(record));
            }
            List<Candidate> candidates = new ArrayList<>();
            for (Map<String, String> record : candidateRecords) {
                candidates.add(new Candidate(record));
            }
            List<Voter> voters = new ArrayList<>();
            for (Map<String, String> record : voterRecords) {
                voters.add(new Voter(record));
            }

            for (Election election : elections) {
                persistentData.addElection(election);
            }
            for (Candidate candidate : candidates) {
                persistentData.addCandidate(candidate);
            }
            for (Voter voter : voters) {
                persistentData.addVoter(voter);
            }
            loadFailed = false;
            democracy.getLogger().info("Loaded " + elections.size() + " election(s), " + candidates.size()
                    + " candidate(s) and " + voters.size() + " voter(s).");
            return true;
        } catch (IOException | RuntimeException e) {
            // RuntimeException covers Gson's syntax errors and the rejection of a record that
            // is missing a field or holds a malformed UUID or timestamp.
            loadFailed = true;
            democracy.getLogger().log(Level.SEVERE, "Election data could not be loaded from " + dataFolder
                    + ". The files have been left untouched and will not be overwritten on shutdown; "
                    + "fix or remove them and restart the server.", e);
            return false;
        }
    }

    /**
     * Writes every election, candidate and voter in {@link PersistentData} to the data folder.
     * @return Whether the data was written. On failure the cause has been logged.
     */
    public boolean save() {
        if (loadFailed) {
            democracy.getLogger().severe("Election data was not saved because it could not be loaded when "
                    + "the plugin was enabled. The files on disk have been left as they were.");
            return false;
        }
        File dataFolder = democracy.getDataFolder();
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            democracy.getLogger().severe("Election data was not saved because " + dataFolder + " could not be created.");
            return false;
        }
        try {
            writeRecords(new File(dataFolder, ELECTIONS_FILE), toRecords(persistentData.getElections()));
            writeRecords(new File(dataFolder, CANDIDATES_FILE), toRecords(persistentData.getCandidates()));
            writeRecords(new File(dataFolder, VOTERS_FILE), toRecords(persistentData.getVoters()));
            return true;
        } catch (IOException e) {
            democracy.getLogger().log(Level.SEVERE, "Election data could not be saved to " + dataFolder + ".", e);
            return false;
        }
    }

    private List<Map<String, String>> readRecords(File file) throws IOException {
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (Reader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            List<Map<String, String>> records = gson.fromJson(reader, RECORD_LIST_TYPE);
            return records == null ? new ArrayList<>() : records;
        }
    }

    private void writeRecords(File file, List<Map<String, String>> records) throws IOException {
        try (Writer writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            gson.toJson(records, writer);
        }
    }

    private static List<Map<String, String>> toRecords(List<? extends Savable> savables) {
        List<Map<String, String>> records = new ArrayList<>();
        for (Savable savable : savables) {
            records.add(savable.save());
        }
        return records;
    }
}
