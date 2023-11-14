package pl.norbit.treecuter.service;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;
import pl.norbit.treecuter.api.listeners.TreeCutEvent;
import pl.norbit.treecuter.api.listeners.TreeGlowEvent;
import pl.norbit.treecuter.utils.TaskUtils;

import java.util.HashMap;
import java.util.UUID;

public class ToggleService implements Listener {
    private static final HashMap<UUID, Boolean> toggleMap = new HashMap<>();

    private static boolean getToggle(UUID uuid){
        toggleMap.putIfAbsent(uuid, true);
        return toggleMap.get(uuid);
    }

    public static boolean changeToggle(UUID uuid){
        boolean toggle = getToggle(uuid);

        toggleMap.put(uuid, !toggle);

        return !toggle;
    }

    @EventHandler
    public void onTreeGlow(TreeGlowEvent e){
        Player p = e.getPlayer();
        if(getToggle(e.getPlayer().getUniqueId())) return;

        BlockBreakService.removePlayer(p);
        p.removePotionEffect(PotionEffectType.SLOW_DIGGING);
        e.setCancelled(true);
    }
    @EventHandler
    public void onTreeCut(TreeCutEvent e){
        Player p = e.getPlayer();
        if(getToggle(e.getPlayer().getUniqueId())) return;

        BlockBreakService.removePlayer(p);
        p.removePotionEffect(PotionEffectType.SLOW_DIGGING);
        e.setCancelled(true);
    }
}
