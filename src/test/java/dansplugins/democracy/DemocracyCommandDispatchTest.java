package dansplugins.democracy;

import dansplugins.democracy.trace.TraceClient;
import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import preponderous.ponder.minecraft.bukkit.services.CommandService;

import java.lang.reflect.Field;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Pins {@link Democracy#onCommand}: only a command whose sender is a player is reported to trace,
 * as README.md, CONFIG.md and config.yml promise, and every sender's command still runs. {@code onEnable()}
 * cannot run outside a server, so the client and command service it would set up are put into a
 * mocked plugin directly.
 */
class DemocracyCommandDispatchTest {
    private static final String[] NO_ARGS = new String[0];
    private static final String[] INFO_ARGS = new String[] { "info" };

    private Democracy democracy;
    private TraceClient trace;
    private CommandService commandService;
    private Command command;

    @BeforeEach
    void setUp() throws ReflectiveOperationException {
        democracy = mock(Democracy.class);
        trace = mock(TraceClient.class);
        commandService = mock(CommandService.class);
        command = mock(Command.class);
        when(command.getName()).thenReturn("d");
        when(democracy.getVersion()).thenReturn("v9.9.9");
        when(democracy.onCommand(any(), any(), any(), any())).thenCallRealMethod();
        setField("trace", trace);
        setField("commandService", commandService);
    }

    private void setField(String name, Object value) throws ReflectiveOperationException {
        Field field = Democracy.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(democracy, value);
    }

    private void verifyCommandReported() {
        verify(trace).report(eq("command"), isNull(), eq(Collections.singletonMap("name", "d")));
    }

    @Test
    void aPlayersSubcommandIsReported() {
        democracy.onCommand(mock(Player.class), command, "d", INFO_ARGS);

        verifyCommandReported();
    }

    @Test
    void aPlayersBareCommandIsReported() {
        democracy.onCommand(mock(Player.class), command, "d", NO_ARGS);

        verifyCommandReported();
    }

    @Test
    void theCommandsNameIsReportedRatherThanTheLabelTyped() {
        democracy.onCommand(mock(Player.class), command, "democracy:d", INFO_ARGS);

        verifyCommandReported();
    }

    @Test
    void aConsoleCommandIsNotReported() {
        democracy.onCommand(mock(ConsoleCommandSender.class), command, "d", INFO_ARGS);

        verifyNoInteractions(trace);
    }

    @Test
    void aBareConsoleCommandIsNotReported() {
        democracy.onCommand(mock(ConsoleCommandSender.class), command, "d", NO_ARGS);

        verifyNoInteractions(trace);
    }

    @Test
    void aCommandBlockCommandIsNotReported() {
        democracy.onCommand(mock(BlockCommandSender.class), command, "d", INFO_ARGS);

        verifyNoInteractions(trace);
    }

    @Test
    void anyOtherSenderThatIsNotAPlayerIsNotReported() {
        democracy.onCommand(mock(CommandSender.class), command, "d", INFO_ARGS);

        verifyNoInteractions(trace);
    }

    @Test
    void aBareCommandShowsThePluginInformationWithoutTheCommandService() {
        CommandSender sender = mock(CommandSender.class);

        assertTrue(democracy.onCommand(sender, command, "d", NO_ARGS));

        verify(sender).sendMessage(ChatColor.AQUA + "Democracy v9.9.9");
        verify(commandService, never()).interpretAndExecuteCommand(any(), any(), any());
    }

    @Test
    void aSubcommandThatSucceedsReturnsTrue() {
        Player player = mock(Player.class);
        when(commandService.interpretAndExecuteCommand(player, "d", INFO_ARGS)).thenReturn(true);

        assertTrue(democracy.onCommand(player, command, "d", INFO_ARGS));
    }

    @Test
    void aSubcommandThatFailsReturnsFalse() {
        Player player = mock(Player.class);
        when(commandService.interpretAndExecuteCommand(player, "d", INFO_ARGS)).thenReturn(false);

        assertFalse(democracy.onCommand(player, command, "d", INFO_ARGS));
    }

    @Test
    void aConsoleSubcommandStillRunsThoughItIsNotReported() {
        ConsoleCommandSender console = mock(ConsoleCommandSender.class);

        democracy.onCommand(console, command, "d", INFO_ARGS);

        verify(commandService).interpretAndExecuteCommand(console, "d", INFO_ARGS);
        verifyNoInteractions(trace);
    }
}
