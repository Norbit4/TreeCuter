package pl.norbit.treecuter.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import pl.norbit.treecuter.api.listeners.TreeCutEvent;
import pl.norbit.treecuter.api.listeners.TreeGlowEvent;
import pl.norbit.treecuter.service.EffectService;
import pl.norbit.treecuter.service.ToggleService;

public class TreeListeners implements Listener {
    @EventHandler
    public void onTreeGlow(TreeGlowEvent e){
        Player p = e.getPlayer();

        if(ToggleService.getToggle(p.getUniqueId())) return;

        EffectService.removeEffect(p);
        e.setCancelled(true);
    }

    @EventHandler
    public void onTreeCut(TreeCutEvent e){
        Player p = e.getPlayer();

        if(ToggleService.getToggle(p.getUniqueId())) return;

        EffectService.removeEffect(p);
        e.setCancelled(true);
    }
}