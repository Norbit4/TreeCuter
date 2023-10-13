package pl.norbit.treecuter.service;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import pl.norbit.treecuter.config.Settings;
import pl.norbit.treecuter.jobs.JobsService;
import pl.norbit.treecuter.utils.TaskUtils;

import java.util.HashSet;
import java.util.Set;

public class TreeCutService {

    public static void cutTree(Block b, Player p) {

        var type = b.getType();

        if(!Settings.ACCEPT_WOOD_BLOCKS.contains(type)) return;

        TaskUtils.runTaskLaterAsynchronously(() ->{
            Set<Block> blocks = getBlocksAround(new HashSet<>(), b);

            blocks.forEach(block -> TaskUtils.runTaskLater(() -> {

                if(Settings.JOBS_IS_ENABLED) JobsService.updateJobs(p, block);

                if(Settings.ITEMS_TO_INVENTORY){
                    p.getInventory().addItem(new ItemStack(block.getType()));
                    block.setType(Material.AIR);
                }else block.breakNaturally();

            }, 1L));

            if(blocks.size() == 0) return;

            TaskUtils.runTaskLater(() -> updateItem(p, blocks.size()),1L);

        }, 0L);
    }

    private static Set<Block> getBlocksAround(Set<Block> blocks, Block b) {

        for (BlockFace v : BlockFace.values()) {
                var relativeB = b.getRelative(v);

                if(blocks.contains(relativeB)) continue;

                if(!Settings.ACCEPT_WOOD_BLOCKS.contains(relativeB.getType())) continue;

                if(blocks.size() >= Settings.MAX_BLOCKS) return blocks;

                blocks.add(relativeB);
                blocks = getBlocksAround(blocks, relativeB);
        }
        return blocks;
    }

    private static void updateItem(Player p, int durabilityDamage){

        var itemInHand = p.getInventory().getItemInMainHand();

        if(itemInHand.getType() == Material.AIR) return;

        var meta = itemInHand.getItemMeta();

        meta = updateDurability(meta, durabilityDamage);
        itemInHand.setItemMeta(meta);

        p.getInventory().setItemInMainHand(itemInHand);
    }

    private static ItemMeta updateDurability(ItemMeta meta,  int dmg){
        if (meta instanceof Damageable damageable) damageable.setDamage((damageable.getDamage() + dmg));
        return meta;
    }
}
