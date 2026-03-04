package pl.norbit.treecuter.treeplant;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import pl.norbit.treecuter.config.Settings;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import static pl.norbit.treecuter.utils.TaskUtils.timer;

public class TreePlanterService {

    private static final Map<UUID, TrackedItem> items = new HashMap<>();
    private static BukkitTask task;

    private TreePlanterService() {}

    public static void start() {
        if (task != null) {
            task.cancel();
        }

        task = timer(TreePlanterService::updateItems, 40L);
    }

    public static void stop() {
        if (task != null) {
            task.cancel();
        }
    }

    public static void track(Item item) {
        items.put(item.getUniqueId(), new TrackedItem(item, 3, 10));
    }

    private static void updateItems() {
        Iterator<Map.Entry<UUID, TrackedItem>> iterator = items.entrySet().iterator();

        while (iterator.hasNext()) {
            TrackedItem tracked = iterator.next().getValue();
            Item item = tracked.item;

            if (!item.isValid() || item.isDead()) {
                iterator.remove();
                continue;
            }

            if (!Settings.isAcceptedSapling(item.getItemStack().getType())) {
                iterator.remove();
                continue;
            }
            Location loc = item.getLocation();

            if (!loc.isChunkLoaded()) {
                iterator.remove();
                continue;
            }

            Block b = loc.getBlock();
            Block ground = b.getRelative(0, -1, 0);

            if(b.getType() == Material.MUD) {
                ground = b;
                b = b.getRelative(0, 1, 0);
            }

            if (!b.isPassable()) {
                iterator.remove();
                continue;
            }

            if (!item.isOnGround()) {
                continue;
            }

            if (!Settings.isGround(ground.getType())) {
                tracked.attempts--;

                if (tracked.attempts <= 0) {
                    iterator.remove();
                }

                continue;
            }

            tracked.time--;

            if (tracked.time <= 0) {
                plantSapling(item, b);
                iterator.remove();
            }
        }
    }

    private static void plantSapling(Item item, Block block) {
        ItemStack is = item.getItemStack();

        block.setType(is.getType(), false);

        if (is.getAmount() > 1) {
            is.setAmount(is.getAmount() - 1);
            item.setItemStack(is);
        } else {
            item.remove();
        }
    }

    private static class TrackedItem {
        Item item;
        int time;
        int attempts;

        TrackedItem(Item item, int time, int attempts) {
            this.item = item;
            this.time = time;
            this.attempts = attempts;
        }
    }
}