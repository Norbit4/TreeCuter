package pl.norbit.treecuter.service;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitTask;
import pl.norbit.treecuter.TreeCuter;
import pl.norbit.treecuter.config.Settings;
import pl.norbit.treecuter.utils.TaskUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static pl.norbit.treecuter.utils.TaskUtils.timer;

public class TreePlanterService {
    private static final ConcurrentHashMap<UUID, Integer> S_UUIDS = new  ConcurrentHashMap<>();
    private static BukkitTask task;

    private TreePlanterService() {
        throw new IllegalStateException("This class cannot be instantiated");
    }

    public static void start() {
        if(task != null) task.cancel();
        startTimer();
    }
    public static void stop() {
        if(task != null) task.cancel();
    }

    private static void startTimer(){
        var server = TreeCuter.getInstance().getServer();

        List<World> worlds = server.getWorlds();

        task = timer(() -> worlds.forEach(TreePlanterService::updateSaplingItemsInWorld),40L);
    }
    private static void updateSaplingItemsInWorld(World world){
        world.getEntitiesByClass(Item.class).forEach(item -> {
            var is = item.getItemStack();

            if (!Settings.AUTO_PLANT_SAPLINGS.contains(is.getType())) return;

            var loc = item.getLocation();
            var blockUnderType = loc.getBlock().getRelative(0, -1, 0).getType();

            if (!isGround(blockUnderType)) return;

            var type = loc.getBlock().getType();

            if (type != Material.AIR) return;
            updateTime(item);
        });
    }

    private static boolean isGround(Material type){
        return type == Material.DIRT || type == Material.GRASS_BLOCK
                || type == Material.MUD || type == Material.MOSS_BLOCK
                || type == Material.PODZOL || type == Material.MYCELIUM
                || type == Material.COARSE_DIRT;
    }

    private static void updateTime(Item item){
        var time = S_UUIDS.get(item.getUniqueId());

        if(time == null) S_UUIDS.compute(item.getUniqueId(), (uuid, integer) -> 3);
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
