package pl.norbit.treecuter.utils;

import org.bukkit.entity.Player;
import pl.norbit.treecuter.TreeCuter;

import java.util.ArrayList;
import java.util.List;

public class PlayerUtils {

    private PlayerUtils() {
        throw new IllegalStateException("This class cannot be instantiated");
    }

    public static List<Player> getOnlinePlayers(){
        return new ArrayList<>(TreeCuter.getInstance().getServer().getOnlinePlayers());
    }
}
