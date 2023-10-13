package pl.norbit.treecuter.service;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.norbit.treecuter.config.Settings;
import pl.norbit.treecuter.utils.PermissionUtil;
import pl.norbit.treecuter.utils.TaskUtils;

import java.util.HashSet;
import java.util.Set;

public class BlockBreakService implements Listener {
    private static final Set<Player> sPlayers = new HashSet<>();

    public static void start(){
        TaskUtils.runTaskTimerAsynchronously(() -> {
            sPlayers.stream().filter(p -> !p.isSneaking()).toList().forEach(sPlayers::remove);

            int level = Settings.EFFECT_LEVEL - 1;
            var potionEffect = new PotionEffect(PotionEffectType.SLOW_DIGGING, 15, level);

            TaskUtils.runTaskLater(()-> sPlayers.forEach(p -> p.addPotionEffect(potionEffect)), 0L);
        }, 0L,14L);
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent e) {
        var b = e.getClickedBlock();
        var action = e.getAction();
        var p = e.getPlayer();

        if(b == null) return;

        if(action != Action.LEFT_CLICK_BLOCK) return;

        if(p.getGameMode() == GameMode.CREATIVE) return;

        if(Settings.USE_PERMISSIONS) {
            var permissionUtil = new PermissionUtil(p);
            if(!permissionUtil.hasPermission(Settings.PERMISSION, "*")) return;
        }

        if(!Settings.ACCEPT_WOOD_BLOCKS.contains(b.getType())) return;

        var item = p.getInventory().getItemInMainHand();

        if(!Settings.ACCEPT_TOOLS.contains(item.getType())) return;

        if(!Settings.SHIFT_MINING) return;

        if(!p.isSneaking()) return;

        if(!Settings.APPLY_MINING_EFFECT) return;

        sPlayers.add(p);
        int level = Settings.EFFECT_LEVEL - 1;
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 15, level));
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        var b = e.getBlock();

        var p = e.getPlayer();

        if(e.isCancelled()) return;

        if(Settings.SHIFT_MINING) if(!p.isSneaking()) return;

        if(Settings.SHIFT_MINING && Settings.APPLY_MINING_EFFECT) if(!sPlayers.contains(p)) return;

        if(Settings.USE_PERMISSIONS) {
            PermissionUtil permissionUtil = new PermissionUtil(p);
            if(!permissionUtil.hasPermission(Settings.PERMISSION, "*")) return;
        }

        var item = e.getPlayer().getInventory().getItemInMainHand();

        if(!Settings.ACCEPT_TOOLS.contains(item.getType())) return;

        TreeCutService.cutTree(b, p);
    }
}