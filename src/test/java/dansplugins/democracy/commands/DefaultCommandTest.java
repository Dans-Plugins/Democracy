package dansplugins.democracy.commands;

import dansplugins.democracy.Democracy;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DefaultCommandTest {

    @Test
    void showsVersionAuthorAndWiki() {
        Democracy democracy = mock(Democracy.class);
        when(democracy.getVersion()).thenReturn("v9.9.9");
        CommandSender sender = mock(CommandSender.class);

        assertTrue(new DefaultCommand(democracy).execute(sender));

        verify(sender).sendMessage(ChatColor.AQUA + "Democracy v9.9.9");
        verify(sender).sendMessage(ChatColor.AQUA + "Developed by: Daniel Stephenson");
        verify(sender).sendMessage(ChatColor.AQUA + "Wiki: https://github.com/Dans-Plugins/Democracy/wiki");
    }

    @Test
    void ignoresArguments() {
        Democracy democracy = mock(Democracy.class);
        when(democracy.getVersion()).thenReturn("v9.9.9");
        CommandSender sender = mock(CommandSender.class);

        assertTrue(new DefaultCommand(democracy).execute(sender, new String[] { "anything" }));

        verify(sender).sendMessage(ChatColor.AQUA + "Democracy v9.9.9");
    }
}
