package pl.norbit.treecuter.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.norbit.treecuter.config.Settings;
import pl.norbit.treecuter.utils.item.ItemsUtils;
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
            case "RELOAD" -> {
                if(hasNotPermission(sender, "reload")) return true;

                sender.sendMessage(ChatUtils.format(Settings.RELOAD_START));
                Settings.loadConfig(true);
                sender.sendMessage(ChatUtils.format(Settings.RELOAD_END));
                return true;
            }
            case "GET" -> {
                if(hasNotPermission(sender, "get")) return true;

                if(!Settings.TOOL_ENABLE) {
                    sender.sendMessage(ChatUtils.format(Settings.TOOL_DISABLED));
                    return true;
                }
                var p = getPlayer(sender);

                if(p == null) return true;

                var item = ItemsUtils.getItem();

                p.getInventory().addItem(item);

                p.sendMessage(ChatUtils.format(Settings.TOOL_GET));
                return true;
            }
            case "TOGGLE" -> {
                if (hasNotPermission(sender, "toggle")) return true;

                var p = getPlayer(sender);

                if (p == null) return true;

                boolean status = ToggleService.changeToggle(p.getUniqueId());

                String message = status ? Settings.TOGGLE_MESSAGE_ON : Settings.TOGGLE_MESSAGE_OFF;

                p.sendMessage(ChatUtils.format(message));
                return true;
            }
            default -> {
                sendInfo(sender);
                return true;
            }
        }
    }

    private boolean hasNotPermission(CommandSender sender, String permission){
        String perm = "treecuter." + permission;

        if(!PermissionsUtils.hasPermission(sender, perm)){
            sender.sendMessage(ChatUtils.format(Settings.PERMISSION_MESSAGE));
            return true;
        }
        return false;
    }

    private Player getPlayer(CommandSender sender){
        if((sender instanceof Player player)) {
            return player;
        }
        sender.sendMessage(ChatUtils.format(Settings.CONSOLE_MESSAGE));
        return null;
    }

    private static void sendInfo(CommandSender sender){
        if(!sender.hasPermission("treecuter.help")){
            sender.sendMessage(ChatUtils.format(Settings.PERMISSION_MESSAGE));
            return;
        }

        Settings.HELP_MESSAGE.stream().map(ChatUtils::format).forEach(sender::sendMessage);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return List.of("reload", "get", "toggle");
    }
}
