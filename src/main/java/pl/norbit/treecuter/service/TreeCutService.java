package pl.norbit.treecuter.service;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import pl.norbit.treecuter.TreeCuter;
import pl.norbit.treecuter.api.listeners.TreeCutEvent;
import pl.norbit.treecuter.api.listeners.TreeGlowEvent;
import pl.norbit.treecuter.config.Settings;
import pl.norbit.treecuter.glow.GlowingService;
import pl.norbit.treecuter.model.GlowBlock;
import pl.norbit.treecuter.utils.TaskUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TreeCutService {

    private static final ConcurrentHashMap<Player, GlowBlock> playerBlockMap = new ConcurrentHashMap<>();

    public static void start(){
        TaskUtils.runTaskTimerAsynchronously(() -> {
            playerBlockMap.forEach((player, glowBlock) -> {
                if(glowBlock.getTime() == 0){
                    glowBlock.getBlocks().forEach(block -> GlowingService.unsetGlowing(block, player));
                    playerBlockMap.compute(player, (player1, glowBlock1) -> null);
                    return;
                }
                glowBlock.setTime(glowBlock.getTime() - 1);
            });

        }, 0L,20L);

    }

    public static void colorSelectedTree(Block b, Player p, ChatColor color){
        TaskUtils.runTaskLaterAsynchronously(() ->{
            var gBlock = playerBlockMap.get(p);
            if (gBlock != null) gBlock.getBlocks().forEach(block -> GlowingService.unsetGlowing(block, p));

            Set<Block> blocks = getBlocksAround(new HashSet<>(), b);

            TreeGlowEvent event = new TreeGlowEvent(blocks, p);
            TaskUtils.runTaskLater(() -> TreeCuter.getInstance().getServer().getPluginManager().callEvent(event),0L);

            TaskUtils.runTaskLaterAsynchronously(() -> {
                if (event.isCancelled()) return;

                GlowingService.setGlowing(b, p, color);
                blocks.forEach(block -> GlowingService.setGlowing(block, p, color));

                playerBlockMap.compute(p, (player, blocks1) -> new GlowBlock(blocks));
            }, 2L);
        }, 0L);
    }

    public static void cutTree(Block b, Player p) {

        var type = b.getType();

        if(!Settings.ACCEPT_WOOD_BLOCKS.contains(type)) return;

        GlowingService.unsetGlowing(b, p);

        TaskUtils.runTaskLaterAsynchronously(() -> {
            Set<Block> blocks = getBlocksAround(new HashSet<>(), b);

            TreeCutEvent event = new TreeCutEvent(blocks, p);
            TaskUtils.runTaskLater(() -> TreeCuter.getInstance().getServer().getPluginManager().callEvent(event),0L);

            //wait for event
            TaskUtils.runTaskLaterAsynchronously(() -> {
                if(event.isCancelled()) return;

                blocks.forEach(block -> TaskUtils.runTaskLater(() -> {

                    GlowingService.unsetGlowing(block, p);

                    if (Settings.ITEMS_TO_INVENTORY) {
                        p.getInventory().addItem(new ItemStack(block.getType()));
                        block.setType(Material.AIR);
                    } else block.breakNaturally();

                }, 1L));

                if (blocks.size() == 0) return;

                TaskUtils.runTaskLater(() -> updateItem(p, blocks.size()), 1L);
            }, 2L);

        }, 0L);
    }

    private static Set<Block> getBlocksAround(Set<Block> blocks, Block b) {

        for (BlockFace v : BlockFace.values()) {
            var relativeB = b.getRelative(v);

            if(blocks.contains(relativeB)) continue;

            if(!Settings.ACCEPT_WOOD_BLOCKS.contains(relativeB.getType())) continue;

            if(blocks.size() >= Settings.MAX_BLOCKS) return blocks;

            blocks.add(relativeB);
            getBlocksAround(blocks, relativeB);
            getDiagonalBlocks(blocks, relativeB);
        }
        return blocks;
    }

    private static void getDiagonalBlocks(Set<Block> blocks, Block b){

        for (int i = -1; i < 2; i++) {
            checkBlock(blocks, b.getRelative(1, i, -1));
            checkBlock(blocks, b.getRelative(1, i, 1));
            checkBlock(blocks, b.getRelative(-1, i, -1));
            checkBlock(blocks, b.getRelative(-1, i, 1));

            checkBlock(blocks, b.getRelative(-1, i, 0));
            checkBlock(blocks, b.getRelative(1, i, 0));
            checkBlock(blocks, b.getRelative(0, i, -1));
            checkBlock(blocks, b.getRelative(0, i, 1));

            if(blocks.size() >= Settings.MAX_BLOCKS) return;
        }
    }

    private static void checkBlock(Set<Block> blocks, Block b){
        if(blocks.contains(b)) return;

        if(!Settings.ACCEPT_WOOD_BLOCKS.contains(b.getType())) return;

        if(blocks.size() >= Settings.MAX_BLOCKS) return;

        getBlocksAround(blocks, b);
    }

    private static void updateItem(Player p, int durabilityDamage){

        if(p == null) return;

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
