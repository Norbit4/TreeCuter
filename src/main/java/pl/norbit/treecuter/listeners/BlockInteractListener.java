package pl.norbit.treecuter.listeners;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import pl.norbit.treecuter.config.Settings;
import pl.norbit.treecuter.utils.item.ItemsUtils;
import pl.norbit.treecuter.service.TreeCutService;
import pl.norbit.treecuter.utils.PermissionsUtils;
import pl.norbit.treecuter.utils.WorldGuardUtils;

public class BlockInteractListener implements Listener {
    @EventHandler
    public void onBlockInteract(PlayerInteractEvent e) {
        var b = e.getClickedBlock();
        var action = e.getAction();
        var p = e.getPlayer();

        if(b == null) return;

        if(action != Action.LEFT_CLICK_BLOCK) return;

        if(p.getGameMode() == GameMode.CREATIVE) return;

        if (Settings.WORLDGUARD_IS_ENABLED && (!WorldGuardUtils.canBreak(b.getLocation(), p))) return;

        String worldName = p.getWorld().getName();

        if(Settings.BLOCK_WORLDS.contains(worldName)) return;

        if(Settings.USE_PERMISSIONS && (!PermissionsUtils.hasPermission(p, Settings.PERMISSION))) return;

        if(!Settings.ACCEPT_WOOD_BLOCKS.contains(b.getType())) return;

        var item = p.getInventory().getItemInMainHand();

        if(!Settings.ACCEPT_TOOLS.contains(item.getType())) return;

        if(!ItemsUtils.useTool(item)) return;

        if(Settings.SHIFT_MINING && (!p.isSneaking())) return;

        TreeCutService.selectTreeByBlock(b, p, Settings.GLOWING_COLOR, item);
    }
}
