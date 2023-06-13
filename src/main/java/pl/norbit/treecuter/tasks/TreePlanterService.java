package pl.norbit.treecuter.tasks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import pl.norbit.treecuter.TreeCuter;
import pl.norbit.treecuter.config.Settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TreePlanterService {
    private static final HashMap<UUID, Integer> S_UUIDS = new HashMap<>();
    private static final List<Material> SAPLINGS = Settings.AUTO_PLANT_SAPLINGS; //new ArrayList<>(){{
//        add(Material.ACACIA_SAPLING);
//        add(Material.BIRCH_SAPLING);
//        add(Material.DARK_OAK_SAPLING);
//        add(Material.JUNGLE_SAPLING);
//        add(Material.OAK_SAPLING);
//        add(Material.SPRUCE_SAPLING);
//    }};

    public static void start() {
        timer();
    }

    private static void timer(){
        new BukkitRunnable() {
            @Override
            public void run() {
                TreeCuter instance = TreeCuter.getInstance();

                for (World world : instance.getServer().getWorlds()) {
                    for (Item item : world.getEntitiesByClass(Item.class)) {
                        ItemStack is = item.getItemStack();

                        if (!SAPLINGS.contains(is.getType())) continue;

                        Location itemLocation = item.getLocation();
                        Material blockUnder = itemLocation.getBlock().getRelative(0, -1, 0).getType();

                        if (!(blockUnder == Material.DIRT || blockUnder == Material.GRASS_BLOCK
                                || blockUnder == Material.MUD || blockUnder == Material.MOSS_BLOCK
                        || blockUnder == Material.PODZOL|| blockUnder == Material.MYCELIUM ||
                        blockUnder == Material.COARSE_DIRT)) continue;

                        Material block = itemLocation.getBlock().getRelative(0, 0, 0).getType();

                        if(block != Material.AIR) continue;

                        updateTime(item);
                    }
                }
            }
        }.runTaskTimer(TreeCuter.getInstance(), 0L, 20L);
    }

    private static void updateTime(Item item){
        Integer time = S_UUIDS.get(item.getUniqueId());

        if(time == null) S_UUIDS.put(item.getUniqueId(), 2);
        else if(time == 0){
            plantSapling(item);
            S_UUIDS.remove(item.getUniqueId());
        }else S_UUIDS.put(item.getUniqueId(), time - 1);
    }

    private static void plantSapling(Item item){
        ItemStack is = item.getItemStack();
        Location itemLocation = item.getLocation();

        itemLocation.getBlock().setType(is.getType());

        if(is.getAmount() > 1){
            is.setAmount(is.getAmount() - 1);
            item.setItemStack(is);
        }
        else item.remove();
    }
}
