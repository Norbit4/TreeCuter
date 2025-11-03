package pl.norbit.treecuter.service;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import pl.norbit.treecuter.TreeCuter;
import pl.norbit.treecuter.api.listeners.TreeCutEvent;
import pl.norbit.treecuter.api.listeners.TreeGlowEvent;
import pl.norbit.treecuter.config.Settings;
import pl.norbit.treecuter.config.model.CutShape;
import pl.norbit.treecuter.model.BreakTask;
import pl.norbit.treecuter.model.SelectedBreak;
import pl.norbit.treecuter.utils.BlockUtils;
import pl.norbit.treecuter.utils.GlowUtils;
import pl.norbit.treecuter.utils.DurabilityUtils;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static pl.norbit.treecuter.utils.TaskUtils.*;

public class TreeCutService {

//    private static final Map<UUID, List<Block>> selectedBlocks = new HashMap<>();
//    private static final Map<UUID, Block> mainBlocks = new HashMap<>();
    private static final Map<UUID, SelectedBreak> selectedMap = new HashMap<>();
    private static final PluginManager pluginManager = TreeCuter.getInstance().getServer().getPluginManager();
    private static final List<BreakTask> breakTasks = new CopyOnWriteArrayList<>();

    private TreeCutService() {
        throw new IllegalStateException("This class cannot be instantiated");
    }

    public static void start(){
        timer(() -> {
            List<BreakTask> toRemove = new ArrayList<>();

            for (BreakTask breakTree : breakTasks) {
                if (breakTree.isTreeBroken()) {
                    breakTree.leafTask();
                    toRemove.add(breakTree);
                    continue;
                }

                List<Block> blocks = breakTree.getBlocksToBreak();
                blocks.forEach(b -> breakBlock(breakTree.getPlayer(), b));
            }

            breakTasks.removeAll(toRemove);
        }, 2L);
    }

    /**
     * Get selected blocks (to cut) by player
     * @param p Player
     * @return List of blocks, if player has no selected blocks, return empty set
     */
    public static List<Block> getSelectedBlocks(Player p){
        SelectedBreak selected = selectedMap.get(p.getUniqueId());

        if(selected == null){
            return new ArrayList<>();
        }

        return selected.getSelectedBlocks();
    }

    /**
     * Remove selected blocks from player and remove glowing effect for blocks
     * @param p Player
     */
    public static void removeColorFromTree(Player p){
        SelectedBreak selected = selectedMap.get(p.getUniqueId());

        if(selected == null){
            return;
        }

        List<Block> blocks = selected.getSelectedBlocks();

        GlowUtils.unsetGlowing(blocks, p);
        selectedMap.remove(p.getUniqueId());
    }

    /**
     * Select tree to player and apply glowing effect to blocks
     * @param b Block
     * @param p Player
     * @param shape Wood blocks to select
     * @param item Item in player hand
     */
    public static void selectTreeByBlock(Block b, Player p, CutShape shape, ItemStack item){
        if (p == null || b == null){
            return;
        }
        //check max uses of player item
        int maxBlock = DurabilityUtils.checkRemainingUses(item);

        List<Block> blocks = BlockUtils.getWoodBlocksAround(new ArrayList<>(), b, maxBlock, shape);

        UUID playerUUID = p.getUniqueId();

        if(blocks.size() < Settings.getMinBlocks()){
            selectedMap.remove(playerUUID);
            return;
        }
        //apply mining effect to player
        EffectService.applyEffect(p);

        var treeGlowEvent = new TreeGlowEvent(blocks, p);
        pluginManager.callEvent(treeGlowEvent);

        if(treeGlowEvent.isCancelled()){
            return;
        }

        if(lastBlocksIsSame(playerUUID, b, blocks)){
            return;
        }

        GlowUtils.setGlowing(blocks, p, shape.getGlowingColor());
//        selectedBlocks.put(p.getUniqueId(), blocks);
//        mainBlocks.put(p.getUniqueId(), b);

        selectedMap.put(playerUUID, new SelectedBreak(b,blocks, shape));
    }

    private static boolean lastBlocksIsSame(UUID playerUUID, Block mainBlock, List<Block> blocks){
        SelectedBreak selected = selectedMap.get(playerUUID);

        if(selected == null){
            return false;
        }

        Block lastMainBlock = selected.getMainBlock();
        List<Block> lastBlocks = selected.getSelectedBlocks();

        if(lastBlocks != null && lastMainBlock != null){
            int lastBlocksSize = lastBlocks.size();
            int blocksSize = blocks.size();

            return lastBlocksSize == blocksSize && lastMainBlock.equals(mainBlock);
        }
        return false;
    }

    /**
     * Cut selected tree by player, when player cut tree, remove glowing effect from blocks and break blocks.
     * When player has no selected blocks, do nothing.
     * @param p Player
     */
    public static void cutTree(Player p) {
        if(p == null){
            return;
        }

        if (!EffectService.isEffectPlayer(p)){
            return;
        }

        SelectedBreak selected = selectedMap.get(p.getUniqueId());

        if(selected == null){
            return;
        }

        List<Block> blocks = selected.getSelectedBlocks();

        if(blocks == null){
            return;
        }

        var treeCutEvent = new TreeCutEvent(blocks, p);

        pluginManager.callEvent(treeCutEvent);

        if(treeCutEvent.isCancelled()){
            return;
        }
        GlowUtils.unsetGlowing(blocks, p);

        //create task to break blocks
        BreakTask breakTask = new BreakTask(blocks, p, selected.getCutShape());
        breakTasks.add(breakTask);

        int size = blocks.size();
        updateItem(p, size - 1);

        selectedMap.remove(p.getUniqueId());

        if(Settings.isActionsEnabled()){
            String playerName = p.getName();
            Server server = p.getServer();

            Settings.getActions().forEach(action ->{
                String command = action
                        .replace("{player}", playerName)
                        .replace("{count}", String.valueOf(size));

                server.dispatchCommand(server.getConsoleSender(), command);
            });
        }
    }

    private static void breakBlock(Player p, Block b){
        if(b == null){
            return;
        }

        if(!p.isOnline()){
            return;
        }

        Material mat = b.getType();

        if(mat == Material.AIR){
            return;
        }
        //log block break to CoreProtect
        CoreProtectService.logBreak(p.getName(), b.getState());

        if (Settings.isItemsToInventory()) {
            p.getInventory().addItem(new ItemStack(mat));
            b.setType(Material.AIR);
        } else b.breakNaturally();
    }

    private static void updateItem(Player p, int durabilityDamage){
        PlayerInventory inventory = p.getInventory();

        var itemInHand = inventory.getItemInMainHand();

        if(itemInHand.getType() == Material.AIR){
            return;
        }

        ItemStack itemStack = DurabilityUtils.updateDurability(itemInHand, durabilityDamage);

        inventory.setItemInMainHand(itemStack);
    }
}
