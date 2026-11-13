package dansplugins.democracy;

import com.dansplugins.factionsystem.MedievalFactions;
import dansplugins.democracy.services.ConfigService;
import dansplugins.democracy.services.StorageService;
import dansplugins.democracy.trace.TraceClient;
import dansplugins.democracy.utils.Logger;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import preponderous.ponder.minecraft.bukkit.services.CommandService;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Pins the plugin's lifecycle: the two places {@code onEnable()} stops early and disables the
 * plugin (Medieval Factions not enabled, election data not loadable), and what {@code onDisable()}
 * does on the way down. Neither can run outside a server, so the services they use are put into a
 * mocked plugin directly, as in {@link DemocracyCommandDispatchTest}.
 */
class DemocracyLifecycleTest {
    private Democracy democracy;
    private PluginManager pluginManager;
    private java.util.logging.Logger serverLog;
    private ConfigService configService;
    private StorageService storageService;
    private CommandService commandService;
    private TraceClient trace;

    @BeforeEach
    void setUp() throws ReflectiveOperationException {
        democracy = mock(Democracy.class);
        Server server = mock(Server.class);
        pluginManager = mock(PluginManager.class);
        serverLog = mock(java.util.logging.Logger.class);
        when(democracy.getServer()).thenReturn(server);
        when(server.getPluginManager()).thenReturn(pluginManager);
        when(democracy.getLogger()).thenReturn(serverLog);
        doCallRealMethod().when(democracy).onEnable();
        doCallRealMethod().when(democracy).onDisable();
        when(democracy.getFactionLookup()).thenCallRealMethod();

        configService = mock(ConfigService.class);
        storageService = mock(StorageService.class);
        commandService = mock(CommandService.class);
        trace = mock(TraceClient.class);
        setField("configService", configService);
        setField("storageService", storageService);
        setField("commandService", commandService);
        setField("trace", trace);
        setField("logger", new Logger(democracy));
    }

    private void setField(String name, Object value) throws ReflectiveOperationException {
        Field field = Democracy.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(democracy, value);
    }

    private void enabledMedievalFactions() {
        MedievalFactions medievalFactions = mock(MedievalFactions.class);
        when(medievalFactions.isEnabled()).thenReturn(true);
        when(pluginManager.getPlugin("MedievalFactions")).thenReturn(medievalFactions);
    }

    @Test
    void withoutMedievalFactionsThePluginDisablesItselfBeforeLoadingAnything() {
        when(pluginManager.getPlugin("MedievalFactions")).thenReturn(null);
        democracy.onEnable();

        verify(serverLog).severe("Democracy is being disabled because Medieval Factions is not enabled.");
        verify(pluginManager).disablePlugin(democracy);
        verifyNoInteractions(configService, storageService, commandService, trace);
        assertNull(democracy.getFactionLookup());
    }

    @Test
    void aMedievalFactionsThatIsInstalledButNotEnabledAlsoDisablesThePlugin() {
        Plugin disabledMedievalFactions = mock(MedievalFactions.class);
        when(disabledMedievalFactions.isEnabled()).thenReturn(false);
        when(pluginManager.getPlugin("MedievalFactions")).thenReturn(disabledMedievalFactions);
        democracy.onEnable();

        verify(pluginManager).disablePlugin(democracy);
        verifyNoInteractions(storageService, commandService, trace);
    }

    @Test
    void electionDataThatCannotBeLoadedDisablesThePluginBeforeAnyCommandIsRegistered() {
        enabledMedievalFactions();
        when(storageService.load()).thenReturn(false);
        democracy.onEnable();

        verify(serverLog).severe("Democracy is being disabled because its election data could not be loaded.");
        verify(pluginManager).disablePlugin(democracy);
        verifyNoInteractions(commandService, trace);
        verify(storageService).load();
        verify(storageService, never()).save();
        // the lookup was found before the load was attempted
        assertNotNull(democracy.getFactionLookup());
    }

    @Test
    void onDisableSavesTheElectionDataThenClosesUsageReporting() {
        democracy.onDisable();

        InOrder order = inOrder(storageService, trace);
        order.verify(storageService).save();
        order.verify(trace).close();
    }

    @Test
    void debugModeIsReadFromTheDebugModeConfigOption() {
        when(democracy.isDebugEnabled()).thenCallRealMethod();

        when(configService.getBoolean("debugMode")).thenReturn(true);
        assertTrue(democracy.isDebugEnabled());

        when(configService.getBoolean("debugMode")).thenReturn(false);
        assertFalse(democracy.isDebugEnabled());
    }
}
