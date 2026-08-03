package dansplugins.democracy.commands;

import dansplugins.democracy.Democracy;
import dansplugins.democracy.data.PersistentData;
import dansplugins.democracy.factories.CandidateFactory;
import dansplugins.democracy.objects.Election;
import dansplugins.factionsystem.externalapi.MF_Faction;
import dansplugins.factionsystem.externalapi.MedievalFactionsAPI;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RunCommandTest {
    private RunCommand runCommand;
    private PersistentData persistentData;
    private CandidateFactory candidateFactory;
    private Player player;
    private UUID playerUUID;
    private MedievalFactionsAPI medievalFactionsAPI;

    @BeforeEach
    void setUp() {
        playerUUID = UUID.randomUUID();
        player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(playerUUID);

        MF_Faction faction = mock(MF_Faction.class);
        when(faction.getName()).thenReturn("TestFaction");

        medievalFactionsAPI = mock(MedievalFactionsAPI.class);
        when(medievalFactionsAPI.getFaction(player)).thenReturn(faction);

        Democracy democracy = mock(Democracy.class);
        when(democracy.getMedievalFactionsAPI()).thenReturn(medievalFactionsAPI);

        persistentData = new PersistentData();
        candidateFactory = new CandidateFactory(persistentData);
        runCommand = new RunCommand(democracy, persistentData, candidateFactory);
    }

    @Test
    void failsWhenNotInFaction() {
        when(medievalFactionsAPI.getFaction(player)).thenReturn(null);

        assertFalse(runCommand.execute(player));
    }

    @Test
    void failsWhenNoElectionInProgress() {
        assertFalse(runCommand.execute(player));
    }

    @Test
    void succeedsAndRegistersCandidate() {
        Election election = new Election(player, "TestFaction");
        persistentData.addElection(election);

        assertTrue(runCommand.execute(player));
        assertTrue(election.isCandidate(playerUUID));
    }

    @Test
    void failsWhenAlreadyACandidate() {
        Election election = new Election(player, "TestFaction");
        persistentData.addElection(election);
        runCommand.execute(player);

        assertFalse(runCommand.execute(player));
    }
}
