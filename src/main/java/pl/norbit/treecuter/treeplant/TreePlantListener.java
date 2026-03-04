package pl.norbit.treecuter.treeplant;

import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import pl.norbit.treecuter.config.Settings;

public class TreePlantListener implements Listener {

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        Item item = event.getEntity();

        if (!Settings.isAcceptedSapling(item.getItemStack().getType())) {
            return;
        }

        TreePlanterService.track(item);
    }
}
