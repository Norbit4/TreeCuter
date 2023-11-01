package pl.norbit.treecuter.api;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.norbit.treecuter.config.Settings;
import pl.norbit.treecuter.service.TreeCutService;

import java.util.List;

public class TreeCuterAPI {

    /**
     * Color tree for player.
     *
     * @param b Block of tree.
     * @param p Player to color tree.
     * @param color Color of glow.
     */
    public static void colorTree(@NotNull Block b, @NotNull Player p, @NotNull ChatColor color){
        if(!Settings.ACCEPT_WOOD_BLOCKS.contains(b.getType())) return;

        TreeCutService.colorSelectedTree(b, p, color);
    }

    /**
     * Color tree for players.
     *
     * @param b Block of tree.
     * @param players Players to color tree.
     * @param color Color of glow.
     */
    public static void colorTree(@NotNull Block b, @NotNull List<Player> players, @NotNull ChatColor color){
        if(!Settings.ACCEPT_WOOD_BLOCKS.contains(b.getType())) return;

        players.forEach(p -> TreeCutService.colorSelectedTree(b, p, color));
    }

    /**
     * Cut tree for player.
     *
     * @param b Block of tree.
     * @param p Player to cut tree.
     */
    public static void cutTree(@NotNull Block b, @NotNull Player p){
        if(!Settings.ACCEPT_WOOD_BLOCKS.contains(b.getType())) return;

        TreeCutService.cutTree(b, p);
    }
}
