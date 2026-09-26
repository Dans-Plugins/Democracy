package dansplugins.democracy.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class HelpCommandTest {

    @Test
    void listsVoteWithItsCandidateArgument() {
        CommandSender sender = mock(CommandSender.class);

        assertTrue(new HelpCommand().execute(sender));

        verify(sender).sendMessage(ChatColor.AQUA + "/d vote <candidate>");
        verify(sender, never()).sendMessage(ChatColor.AQUA + "/d vote");
    }
}
