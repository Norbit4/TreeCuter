package pl.norbit.treecuter.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import pl.norbit.treecuter.config.Settings;
import pl.norbit.treecuter.utils.item.ItemsUtils;
import pl.norbit.treecuter.service.EffectService;
import pl.norbit.treecuter.service.TreeCutService;

public class BlockBreakListener implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        var b = e.getBlock();
        var p = e.getPlayer();

        if (e.isCancelled()) return;

        if (Settings.SHIFT_MINING && (!p.isSneaking())) return;

        if (!EffectService.isEffectPlayer(p)) return;

        if (Settings.BLOCK_WORLDS.contains(p.getWorld().getName())) return;

        var item = e.getPlayer().getInventory().getItemInMainHand();

        if (!Settings.ACCEPT_TOOLS.contains(item.getType())) return;

        if(!Settings.ACCEPT_WOOD_BLOCKS.contains(b.getType())) return;

        if (!ItemsUtils.useTool(item)) return;

        TreeCutService.cutTree(p);
    }
}
