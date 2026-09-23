package dansplugins.democracy;

import dansplugins.factionsystem.MedievalFactions;
import dansplugins.factionsystem.externalapi.MedievalFactionsAPI;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class DemocracyTest {

    @Test
    void lookUpReturnsTheApiOfTheEnabledMedievalFactionsInstance() {
        MedievalFactions medievalFactions = mock(MedievalFactions.class);
        MedievalFactionsAPI medievalFactionsAPI = mock(MedievalFactionsAPI.class);
        when(medievalFactions.getAPI()).thenReturn(medievalFactionsAPI);

        try (MockedStatic<MedievalFactions> mocked = mockStatic(MedievalFactions.class)) {
            mocked.when(MedievalFactions::getInstance).thenReturn(medievalFactions);

            assertSame(medievalFactionsAPI, Democracy.lookUpMedievalFactionsAPI());
        }
    }

    @Test
    void lookUpReturnsNullRatherThanThrowingWhenMedievalFactionsIsNotEnabled() {
        // the state the jar-load-time lookup ran into (#34): getInstance() is null until
        // Medieval Factions' own onEnable() has run
        try (MockedStatic<MedievalFactions> mocked = mockStatic(MedievalFactions.class)) {
            mocked.when(MedievalFactions::getInstance).thenReturn(null);

            assertNull(Democracy.lookUpMedievalFactionsAPI());
        }
    }
}
