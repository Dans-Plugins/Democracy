package dansplugins.democracy.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel McCoy Stephenson
 */
public class HelpCommand extends AbstractPluginCommand {

    public HelpCommand() {
        super(new ArrayList<>(List.of("help")), new ArrayList<>(List.of("d.help")));
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        commandSender.sendMessage(ChatColor.AQUA + "/d help");
        commandSender.sendMessage(ChatColor.AQUA + "/d info");
        commandSender.sendMessage(ChatColor.AQUA + "/d vote <candidate>");
        commandSender.sendMessage(ChatColor.AQUA + "/d run");
        commandSender.sendMessage(ChatColor.AQUA + "/d dropout");
        commandSender.sendMessage(ChatColor.AQUA + "/d start");
        commandSender.sendMessage(ChatColor.AQUA + "/d cancel");
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}
