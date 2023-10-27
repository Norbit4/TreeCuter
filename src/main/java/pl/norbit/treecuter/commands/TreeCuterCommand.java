package pl.norbit.treecuter.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.norbit.treecuter.config.Settings;
import pl.norbit.treecuter.item.ItemService;
import pl.norbit.treecuter.utils.ChatUtils;

public class TreeCuterCommand implements CommandExecutor {
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
        }else if(args[0].equalsIgnoreCase("get")){

            if(!Settings.TOOL_ENABLE) {
                sender.sendMessage(ChatUtils.format("&cTool is disabled! Enable it in config!"));
                return true;
            }

            if(!(sender instanceof Player player)){
                sender.sendMessage(ChatUtils.format("&cOnly players can use this command!"));
                return true;
            }

            var item = ItemService.getItem();

            player.getInventory().addItem(item);

            player.sendMessage(ChatUtils.format("&aYou got tool!"));
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
        sender.sendMessage(ChatUtils.format("&7Type: &8/&btreecuter get &7to get tool!"));
        sender.sendMessage("");
    }
}
