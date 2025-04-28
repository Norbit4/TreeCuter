package pl.norbit.treecuter.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.norbit.treecuter.config.Settings;
import pl.norbit.treecuter.service.ToggleService;
import pl.norbit.treecuter.utils.ChatUtils;
import pl.norbit.treecuter.utils.PermissionsUtils;

import java.util.List;

public class TreeCuterCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0) {
            sendInfo(sender);
            return true;
        }
        String arg = args[0].toUpperCase();

        switch (arg) {
            case "RELOAD" -> reload(sender);
            case "GET" -> {
                if(args.length < 2){
                    sendInfo(sender);
                    return true;
                }

                String key = args[1].toUpperCase();

                get(sender, key);
            }
            case "TOGGLE" -> toggle(sender);
            default -> sendInfo(sender);
        }
        return true;
    }

    private void reload(CommandSender sender){
        if(hasNotPermission(sender, "reload")) return;

        sender.sendMessage(ChatUtils.format(Settings.getReloadStart()));
        Settings.loadConfig(true);
        sender.sendMessage(ChatUtils.format(Settings.getReloadEnd()));
    }

    private void get(CommandSender sender, String key){
        if(hasNotPermission(sender, "get")) return;

        var p = getPlayer(sender);

        if(p == null){
            return;
        }

        Settings.getCustomToolForKey(key)
                .ifPresentOrElse(customTool -> {
                    p.getInventory().addItem(customTool);
                    p.sendMessage(ChatUtils.format(Settings.getToolGet()));
                }, () -> p.sendMessage(ChatUtils.format(Settings.getToolNotFound())));


//        var item = ItemsUtils.getItem();
//
//        p.getInventory().addItem(item);
//
//        p.sendMessage(ChatUtils.format(Settings.getToolGet()));
    }

    private void toggle(CommandSender sender){
        if(hasNotPermission(sender, "toggle")){
            return;
        }

        var p = getPlayer(sender);

        if(p == null){
            return;
        }

        boolean status = ToggleService.changeToggle(p.getUniqueId());

        String message = status ? Settings.getToggleMessageOn() : Settings.getToggleMessageOff();

        p.sendMessage(ChatUtils.format(message));
    }

    private boolean hasNotPermission(CommandSender sender, String permission){
        String perm = "treecuter." + permission;

        if(!PermissionsUtils.hasPermission(sender, perm)){
            sender.sendMessage(ChatUtils.format(Settings.getPermissionMessage()));
            return true;
        }
        return false;
    }

    private Player getPlayer(CommandSender sender){
        if((sender instanceof Player player)) {
            return player;
        }
        sender.sendMessage(ChatUtils.format(Settings.getConsoleMessage()));
        return null;
    }

    private static void sendInfo(CommandSender sender){
        if(!sender.hasPermission("treecuter.help")){
            sender.sendMessage(ChatUtils.format(Settings.getPermissionMessage()));
            return;
        }

        Settings.getHelpMessage()
                .stream()
                .map(ChatUtils::format)
                .forEach(sender::sendMessage);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 2 && args[0].equalsIgnoreCase("get")){
            return Settings.getCustomToolKeys();
        }
        return List.of("reload", "get", "toggle");
    }
}
