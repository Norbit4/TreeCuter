package pl.norbit.treecuter.glow;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import pl.norbit.treecuter.TreeCuter;
import pl.norbit.treecuter.utils.TaskUtils;

import java.util.Collection;

public class UnglowListener implements Listener {

    private void unsetGlow(Block b){
        TaskUtils.runTaskLaterAsynchronously(() -> {
            Collection<? extends Player> onlinePlayers = TreeCuter.getInstance().getServer().getOnlinePlayers();

            onlinePlayers.forEach(p -> GlowingService.unsetGlowing(b, p));
        }, 0L);
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent e) {
        unsetGlow(e.getBlock());
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        unsetGlow(e.getBlock());
    }
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
         e.blockList().forEach(this::unsetGlow);
    }
}
