package pl.norbit.treecuter.listeners;

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
import pl.norbit.treecuter.TreeCuter;
import pl.norbit.treecuter.config.Settings;
import pl.norbit.treecuter.jobs.JobsService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BlockBreakListener implements Listener {
    private static final List<UUID> sPlayers = new ArrayList<>();
    private static final int MAX_TREE_HEIGHT = Settings.MAX_TREE_HEIGHT;
    private static final List<Material> MATERIALS = Settings.ACCEPT_BLOCKS;
    private static final List<Material> TOOLS = Settings.ACCEPT_TOOLS;

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent e) {
        Block clickedBlock = e.getClickedBlock();
        Action action = e.getAction();
        Player p = e.getPlayer();

        if(clickedBlock == null) return;

        if(action != Action.LEFT_CLICK_BLOCK) return;

        if(!MATERIALS.contains(clickedBlock.getType())) return;

        ItemStack item = p.getInventory().getItemInMainHand();

        if(!Settings.ACCEPT_NO_TOOLS) if(!TOOLS.contains(item.getType())) return;

        if(Settings.SHIFT_MINING) if(!p.isSneaking()) return;

        if(!Settings.APPLY_MINING_EFFECT) return;

        sPlayers.add(p.getUniqueId());
        new BukkitRunnable() {
            @Override
            public void run() {
                if(!p.isSneaking()) {
                    sPlayers.remove(p.getUniqueId());
                    cancel();
                    return;
                }
                int level = Settings.EFFECT_LEVEL - 1;
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20, level));
            }
        }.runTaskTimer(TreeCuter.getInstance(), 0, 4);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();

        Player p = e.getPlayer();

        if(e.isCancelled()) return;

        if(Settings.SHIFT_MINING){
            if(!p.isSneaking()) return;
            if(!sPlayers.contains(p.getUniqueId())) return;
        }

        if(!MATERIALS.contains(block.getType())) return;

        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();

        if(!Settings.ACCEPT_NO_TOOLS) if(!TOOLS.contains(item.getType())) return;

        Block bottomBlock = getBottomBlock(block);

        if (bottomBlock == null) return;

        e.setCancelled(true);
        destroyTree(bottomBlock, p);
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
        blocks.add(bottomBlock);

        for (int y = 0; y <= MAX_TREE_HEIGHT; y++) for (int x = -1; x <= 1; x++) for (int z = -1; z <= 1; z++) {

            Block cBlock = bottomBlock.getRelative(x, y, z);

            if (cBlock.getType() != logType) continue;

            blocks.add(cBlock);
        }
        breakBlocks(blocks, p);
    }

    private void breakBlocks(List<Block> blocks, Player p) {
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

                if(Settings.JOBS_IS_ENABLED) JobsService.updateJobs(p, block);

                if(Settings.ITEMS_TO_INVENTORY){
                    p.getInventory().addItem(new ItemStack(block.getType()));
                    block.setType(Material.AIR);
                }else block.breakNaturally();
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