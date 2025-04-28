package pl.norbit.treecuter.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import pl.norbit.treecuter.config.Settings;
import pl.norbit.treecuter.config.model.CutShape;
import pl.norbit.treecuter.service.EffectService;
import pl.norbit.treecuter.service.TreeCutService;

public class BlockBreakListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent e) {
        var b = e.getBlock();
        var p = e.getPlayer();

        if (e.isCancelled()){
            return;
        }

        if (Settings.isShiftMining() && (!p.isSneaking())){
            return;
        }

        if (!EffectService.isEffectPlayer(p)){
            return;
        }

        var item = p.getInventory().getItemInMainHand();

        CutShape woodBlocks = Settings.getCutShape(b, item);

        if (woodBlocks == null){
            return;
        }

        TreeCutService.cutTree(p);
    }
}
