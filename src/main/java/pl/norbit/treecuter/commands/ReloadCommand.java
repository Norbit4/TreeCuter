package pl.norbit.treecuter.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import pl.norbit.treecuter.config.Settings;
import pl.norbit.treecuter.utils.ChatUtils;

public class ReloadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(args.length == 0) {
            sendInfo(sender);
            return true;
        }

        if(args[0].equalsIgnoreCase("reload")){
            sender.sendMessage(ChatUtils.format("&7Reloading plugin..."));
            Settings.loadConfig(true);
            sender.sendMessage(ChatUtils.format("&aPlugin reloaded!"));
            return true;
        }

        sendInfo(sender);
        return true;
    }

    private static void sendInfo(CommandSender sender){
        sender.sendMessage("");
        sender.sendMessage(ChatUtils.format("&7TreeCuter by &aNorbit4!"));
        sender.sendMessage(ChatUtils.format("&7Website: &fhttps://n0rbit.pl/"));
        sender.sendMessage(ChatUtils.format(""));
        sender.sendMessage(ChatUtils.format("&7Type: &8/&btreecuter reload &7to reload plugin!"));
        sender.sendMessage("");
    }
}
