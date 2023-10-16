package pl.norbit.treecuter.service;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import pl.norbit.treecuter.TreeCuter;
import pl.norbit.treecuter.config.Settings;
import pl.norbit.treecuter.utils.TaskUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TreePlanterService {
    private static final ConcurrentHashMap<UUID, Integer> S_UUIDS = new  ConcurrentHashMap<>();

    public static void start() {
        timer();
    }

    private static void timer(){

        TaskUtils.runTaskTimer(() ->{
            var server = TreeCuter.getInstance().getServer();

            for (World world : server.getWorlds()) {
                for (Item item : world.getEntitiesByClass(Item.class)) {
                    var is = item.getItemStack();

                    if (!Settings.AUTO_PLANT_SAPLINGS.contains(is.getType())) continue;

                    var loc = item.getLocation();
                    var blockUnderType = loc.getBlock().getRelative(0, -1, 0).getType();

                    if (!(blockUnderType == Material.DIRT || blockUnderType == Material.GRASS_BLOCK
                            || blockUnderType == Material.MUD || blockUnderType == Material.MOSS_BLOCK
                            || blockUnderType == Material.PODZOL|| blockUnderType == Material.MYCELIUM
                            || blockUnderType == Material.COARSE_DIRT)) continue;

                    var type = loc.getBlock().getType();

                    if(type != Material.AIR) continue;

                    updateTime(item);
                }
            }
        }, 0L, 20L);
    }

    private static void updateTime(Item item){
        var time = S_UUIDS.get(item.getUniqueId());

        if(time == null) S_UUIDS.compute(item.getUniqueId(), (uuid, integer) -> 5);
        else if(time == 0){
            plantSapling(item);
            S_UUIDS.compute(item.getUniqueId(), (uuid, integer) -> null);
        }else S_UUIDS.put(item.getUniqueId(), time - 1);
    }

    private static void plantSapling(Item item){
        var is = item.getItemStack();
        var loc = item.getLocation();

        loc.getBlock().setType(is.getType());

        if(is.getAmount() > 1){
            is.setAmount(is.getAmount() - 1);
            item.setItemStack(is);
        }
        else item.remove();
    }
}
