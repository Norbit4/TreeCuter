package pl.norbit.treecuter.listeners;

import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import pl.norbit.treecuter.config.Settings;
import pl.norbit.treecuter.config.model.CutShape;
import pl.norbit.treecuter.service.EffectService;
import pl.norbit.treecuter.service.TreeCutService;

public class ItemsAdderBreakListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(CustomBlockBreakEvent e) {
        Block b = e.getBlock();
        Player p = e.getPlayer();

        if (e.isCancelled()){
            return;
        }

        if (Settings.isShiftMining() && (!p.isSneaking())){
            return;
        }

        if (!EffectService.isEffectPlayer(p)){
            return;
        }

        ItemStack item = p.getInventory().getItemInMainHand();

        CutShape shape = Settings.getCutShape(b, item);

        if (shape == null) {
            return;
        }

        TreeCutService.cutTree(p, shape);
    }
}
