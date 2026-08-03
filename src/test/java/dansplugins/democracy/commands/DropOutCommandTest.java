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

class DropOutCommandTest {
    private DropOutCommand dropOutCommand;
    private PersistentData persistentData;
    private CandidateFactory candidateFactory;
    private Player player;
    private UUID playerUUID;
    private Election election;

    @BeforeEach
    void setUp() {
        playerUUID = UUID.randomUUID();
        player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(playerUUID);

        MF_Faction faction = mock(MF_Faction.class);
        when(faction.getName()).thenReturn("TestFaction");

        MedievalFactionsAPI medievalFactionsAPI = mock(MedievalFactionsAPI.class);
        when(medievalFactionsAPI.getFaction(player)).thenReturn(faction);

        Democracy democracy = mock(Democracy.class);
        when(democracy.getMedievalFactionsAPI()).thenReturn(medievalFactionsAPI);

        persistentData = new PersistentData();
        candidateFactory = new CandidateFactory(persistentData);
        dropOutCommand = new DropOutCommand(democracy, persistentData);

        election = new Election(player, "TestFaction");
        persistentData.addElection(election);
    }

    @Test
    void failsWhenNotACandidate() {
        assertFalse(dropOutCommand.execute(player));
    }

    @Test
    void succeedsAndRemovesCandidate() {
        candidateFactory.createCandidate(player, election);

        assertTrue(dropOutCommand.execute(player));
        assertFalse(election.isCandidate(playerUUID));
        assertFalse(dropOutCommand.execute(player));
    }
}
