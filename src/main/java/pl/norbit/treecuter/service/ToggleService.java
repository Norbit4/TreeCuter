package pl.norbit.treecuter.service;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import pl.norbit.treecuter.api.listeners.TreeCutEvent;
import pl.norbit.treecuter.api.listeners.TreeGlowEvent;

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
    public void onPlayerJoin(TreeGlowEvent e){
        if(!getToggle(e.getPlayer().getUniqueId())) e.setCancelled(true);
    }
    @EventHandler
    public void onPlayerJoin(TreeCutEvent e){
        if(!getToggle(e.getPlayer().getUniqueId())) e.setCancelled(true);
    }
}
