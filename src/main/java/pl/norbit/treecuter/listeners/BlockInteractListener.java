package pl.norbit.treecuter.listeners;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import pl.norbit.treecuter.config.Settings;
import pl.norbit.treecuter.config.model.CutShape;
import pl.norbit.treecuter.service.TreeCutService;
import pl.norbit.treecuter.utils.PermissionsUtils;
import pl.norbit.treecuter.utils.WorldGuardUtils;

public class BlockInteractListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockInteract(PlayerInteractEvent e) {
        var b = e.getClickedBlock();
        var action = e.getAction();
        var p = e.getPlayer();

        if(b == null){
            return;
        }

        if(action != Action.LEFT_CLICK_BLOCK){
            return;
        }

        if(p.getGameMode() == GameMode.CREATIVE){
            return;
        }

        String worldName = p.getWorld().getName();

        if(Settings.isBlockedWorld(worldName)){
            return;
        }

        if(Settings.isShiftMining() && (!p.isSneaking())){
            return;
        }

        if(Settings.isUsePermissions() && (!PermissionsUtils.hasPermission(p, Settings.getPermission()))){
            return;
        }

        var item = p.getInventory().getItemInMainHand();

        CutShape shape = Settings.getCutShape(b, item);

        if (shape == null) {
            return;
        }

        if (Settings.isWorldGuardEnabled() && (!WorldGuardUtils.canBreak(b.getLocation(), p))){
            return;
        }

        TreeCutService.selectTreeByBlock(b, p, shape, item);
    }
}
