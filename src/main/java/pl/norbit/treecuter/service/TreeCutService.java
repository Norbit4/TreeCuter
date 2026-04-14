package pl.norbit.treecuter.service;

import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockMechanic;
import org.bukkit.Material;
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
import pl.norbit.treecuter.utils.item.ItemsAdderUtils;
import pl.norbit.treecuter.utils.item.NexoUtils;

import java.util.*;

import static pl.norbit.treecuter.utils.TaskUtils.timer;

public class TreeCutService {

    private static final Map<UUID, SelectedBreak> selectedMap = new HashMap<>();
    private static final PluginManager pluginManager = TreeCuter.getInstance().getServer().getPluginManager();
    private static final List<BreakTask> breakTasks = new ArrayList<>();

    private TreeCutService() {}

    public static void start() {
        timer(() -> {
            Iterator<BreakTask> iterator = breakTasks.iterator();

            while (iterator.hasNext()) {
                BreakTask breakTree = iterator.next();

                if (breakTree.isTreeBroken()) {
                    breakTree.leafTask();
                    iterator.remove();
                    continue;
                }

                Player player = breakTree.getPlayer();

                for (Block block : breakTree.getBlocksToBreak()) {
                    breakBlock(player, block);
                }
            }

        }, 2L);
    }

    /**
     * Get selected blocks (to cut) by player
     * @param p Player
     * @return List of blocks, if the player has no selected blocks, return an empty set
     */
    public static List<Block> getSelectedBlocks(Player p){
        SelectedBreak selected = selectedMap.get(p.getUniqueId());

        if(selected == null){
            return Collections.emptyList();
        }

        return selected.getSelectedBlocks();
    }

    /**
     * Remove selected blocks from player and remove glowing effect for blocks
     * @param p Player
     */
    public static void removeColorFromTree(Player p){

        SelectedBreak selected = selectedMap.remove(p.getUniqueId());

        if(selected == null){
            return;
        }

        GlowUtils.unsetGlowing(selected.getSelectedBlocks(), p);
    }

    /**
     * Select a tree to player and apply glowing effect to blocks
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

        if(blocks.size() < Settings.getMinBlocks()){
            selectedMap.remove(p.getUniqueId());
            return;
        }

        UUID uuid = p.getUniqueId();
        //apply mining effect to player
        EffectService.applyEffect(p);

        TreeGlowEvent event = new TreeGlowEvent(blocks, p);
        pluginManager.callEvent(event);

        if(event.isCancelled()){
            return;
        }

        if(lastBlocksIsSame(uuid, b, blocks)){
            return;
        }

        GlowUtils.setGlowing(blocks, p, shape.getGlowingColor());

        selectedMap.put(uuid, new SelectedBreak(b, blocks, shape));
    }

    private static boolean lastBlocksIsSame(UUID playerUUID, Block mainBlock, List<Block> blocks){
        SelectedBreak selected = selectedMap.get(playerUUID);

        if(selected == null){
            return false;
        }

        Block lastMainBlock = selected.getMainBlock();
        List<Block> lastBlocks = selected.getSelectedBlocks();

        if(lastBlocks != null && lastMainBlock != null){
            return lastBlocks.size() == blocks.size() && lastMainBlock.equals(mainBlock);
        }

        return false;
    }

    /**
     * Cut selected tree by player, when player cut tree, remove glowing effect from blocks and break blocks.
     * When a player has no selected blocks, do nothing.
     * @param p Player
     */
    public static void cutTree(Player p, CutShape shape) {
        if(p == null){
            return;
        }

        if (!EffectService.isEffectPlayer(p)){
            return;
        }

        SelectedBreak selected = selectedMap.remove(p.getUniqueId());

        if(selected == null){
            return;
        }

        List<Block> blocks = selected.getSelectedBlocks();

        if(blocks == null){
            return;
        }

        TreeCutEvent event = new TreeCutEvent(blocks, p);

        pluginManager.callEvent(event);

        if(event.isCancelled()){
            GlowUtils.unsetGlowing(blocks, p);
            return;
        }

        GlowUtils.unsetGlowing(blocks, p);

        BreakTask breakTask = new BreakTask(blocks, p, selected.getCutShape());
        breakTasks.add(breakTask);

        updateItem(p, blocks.size() - 1);

        ActionsService.triggerActions(p, shape, blocks);
    }

    private static void breakBlock(Player p, Block b){
        if (p == null || !p.isOnline() || b == null){
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
        }else {
            if(Settings.isNexoAdderEnabled()){
                NexoUtils.nexoBreak(b, p);
            } else if (Settings.isItemsAdderEnabled()) {
                ItemsAdderUtils.iaBreak(b);
            }else {
                b.breakNaturally();
            }
        }
    }

    private static void updateItem(Player p, int durabilityDamage){

        PlayerInventory inventory = p.getInventory();

        ItemStack item = inventory.getItemInMainHand();

        if(item.getType() == Material.AIR){
            return;
        }

        ItemStack updated = DurabilityUtils.updateDurability(item, durabilityDamage);

        inventory.setItemInMainHand(updated);
    }
}