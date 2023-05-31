package pl.norbit.treecuter;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.actions.BlockActionInfo;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.JobsPlayer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class BlockBreakListener implements Listener {

    private static final int MAX_TREE_HEIGHT = 15;
    private static final int MAX_TREE_WIDTH = 3;

    private static final List<Material> materials = new ArrayList<>(){
        {
            add(Material.OAK_LOG);
            add(Material.BIRCH_LOG);
            add(Material.SPRUCE_LOG);
            add(Material.JUNGLE_LOG);
            add(Material.ACACIA_LOG);
            add(Material.DARK_OAK_LOG);
        }
    };

    private static final List<Material> exes = new ArrayList<>(){
        {
            add(Material.WOODEN_AXE);
            add(Material.STONE_AXE);
            add(Material.GOLDEN_AXE);
            add(Material.IRON_AXE);
            add(Material.DIAMOND_AXE);
            add(Material.NETHERITE_AXE);
        }
    };

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent e) {
        Block clickedBlock = e.getClickedBlock();
        Action action = e.getAction();
        Player p = e.getPlayer();

        if(clickedBlock == null) return;

        if(action != Action.LEFT_CLICK_BLOCK) return;

        if(!materials.contains(clickedBlock.getType())) return;

        ItemStack item = p.getInventory().getItemInMainHand();

        if(!exes.contains(item.getType())) return;

        if(!p.isSneaking()) return;

        new BukkitRunnable() {
            @Override
            public void run() {
                if(!p.isSneaking()) {
                    cancel();
                    return;
                }
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20, 1));
            }
        }.runTaskTimer(TreeCuter.getInstance(), 0, 10);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();

        Player p = e.getPlayer();

        if(e.isCancelled()) return;

        if(!p.isSneaking()) return;

        if(!materials.contains(block.getType())) return;

        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();

        if(!exes.contains(item.getType())) return;

        Block bottomBlock = getBottomBlock(block);

        if (bottomBlock != null) destroyTree(bottomBlock, p);
    }

    private Block getBottomBlock(Block b) {
        Block bottomB = b;

        while (bottomB.getType() == b.getType()) {
            Block tempB = bottomB.getRelative(0, -1, 0);

            double tempBY = tempB.getLocation().getY();
            double bY = b.getLocation().getY();

            if (tempB.getType() != b.getType() || tempBY  <= bY - MAX_TREE_HEIGHT) return bottomB;

            bottomB = tempB;
        }
        return null;
    }

    private void destroyTree(Block bottomBlock, Player p) {
        Material logType = bottomBlock.getType();

        List<Block> blocks = new ArrayList<>();

        for (int y = 0; y <= MAX_TREE_HEIGHT; y++) for (int x = -1; x <= 1; x++) for (int z = -1; z <= 1; z++) {

            Block cBlock = bottomBlock.getRelative(x, y, z);

            if (cBlock.getType() != logType) continue;

            blocks.add(cBlock);
        }
        breakBlocks(blocks, p);
    }

    private void breakBlocks(List<Block> blocks, Player p) {
        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(p);
        updateItem(p, blocks.size() - 1);

        final int[] i = {0};
        new BukkitRunnable() {
            @Override
            public void run() {
                i[0]++;

                if (i[0] == blocks.size()) {
                    cancel();
                    return;
                }

                Block block = blocks.get(i[0]);

                if(jPlayer != null) Jobs.action(jPlayer, new BlockActionInfo(block, ActionType.BREAK), block);

                block.breakNaturally();
            }
        }.runTaskTimer(TreeCuter.getInstance(), 0, 1);
    }

    private void updateItem(Player p, int durabilityDamage){

        ItemStack itemInHand = p.getInventory().getItemInMainHand();

        if(itemInHand.getType() == Material.AIR) return;

        ItemMeta meta = itemInHand.getItemMeta();

        meta = updateDurability(meta, durabilityDamage);

        itemInHand.setItemMeta(meta);

        p.getInventory().setItemInMainHand(itemInHand);
    }

    private ItemMeta updateDurability(ItemMeta meta,  int durabilityDamage){
        if (meta instanceof Damageable damageable) damageable.setDamage((damageable.getDamage() + durabilityDamage));
        return meta;
    }
}
