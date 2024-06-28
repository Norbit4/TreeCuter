package pl.norbit.treecuter.service;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitTask;
import pl.norbit.treecuter.TreeCuter;
import pl.norbit.treecuter.config.Settings;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static pl.norbit.treecuter.utils.TaskUtils.timer;

public class TreePlanterService {
    private static final ConcurrentHashMap<UUID, Integer> itemsMap = new  ConcurrentHashMap<>();
    private static BukkitTask task;

    private TreePlanterService() {
        throw new IllegalStateException("This class cannot be instantiated");
    }

    public static void start() {
        if(task != null){
            task.cancel();
        }
        startTimer();
    }
    public static void stop() {
        if(task != null) {
            task.cancel();
        }
    }

    private static void startTimer(){
        var server = TreeCuter.getInstance().getServer();

        List<World> worlds = server.getWorlds();

        task = timer(() -> worlds.forEach(TreePlanterService::updateSaplingItemsInWorld),40L);
    }

    private static void updateSaplingItemsInWorld(World world){
        world.getEntitiesByClass(Item.class).forEach(item -> {
            var is = item.getItemStack();

            if (!Settings.isAcceptedSapling(is.getType())){
                return;
            }

            var loc = item.getLocation();
            var blockUnderType = loc.getBlock().getRelative(0, -1, 0).getType();

            if (!Settings.isGround(blockUnderType)){
                return;
            }

            var type = loc.getBlock().getType();

            if (type != Material.AIR){
                return;
            }
            updateTime(item);
        });
    }

    private static void updateTime(Item item){
        var time = itemsMap.get(item.getUniqueId());

        if(time == null){
            itemsMap.compute(item.getUniqueId(), (uuid, integer) -> 3);
        }
        else if(time == 0){
            plantSapling(item);
            itemsMap.remove(item.getUniqueId());
        }else {
            itemsMap.compute(item.getUniqueId(), (uuid, integer) -> time - 1);
        }
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
