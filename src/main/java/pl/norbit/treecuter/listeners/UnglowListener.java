package pl.norbit.treecuter.listeners;

import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import pl.norbit.treecuter.TreeCuter;
import pl.norbit.treecuter.utils.GlowUtils;
import pl.norbit.treecuter.utils.TaskUtils;

import java.util.Collection;

public class UnglowListener implements Listener {
    private final Server SERVER = TreeCuter.getInstance().getServer();

    private void unsetGlow(Block b){
        TaskUtils.runTaskLaterAsynchronously(() -> {
            Collection<? extends Player> onlinePlayers = SERVER.getOnlinePlayers();

            onlinePlayers.forEach(p -> GlowUtils.unsetGlowing(b, p));
        }, 0L);
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
