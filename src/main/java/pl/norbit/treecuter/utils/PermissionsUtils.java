package pl.norbit.treecuter.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class PermissionsUtils {

    private PermissionsUtils() {
        throw new IllegalStateException("This class cannot be instantiated");
    }

    public static boolean hasPermission(Player p, String perm) {
        if(p.isOp()){
            return true;
        }

        if(p.hasPermission("*")){
            return true;
        }

        return p.hasPermission(perm);
    }

    public static boolean hasPermission(CommandSender sender, String perm) {
        if(sender.isOp()){
            return true;
        }

        if(sender.hasPermission("*")){
            return true;
        }

        return sender.hasPermission(perm);
    }

}
