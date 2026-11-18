package pl.norbit.treecuter.service;

import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.aurora.api.item.TypeId;
import gg.auroramc.collections.api.AuroraCollectionsProvider;
import gg.auroramc.collections.collection.CollectionManager;
import gg.auroramc.collections.collection.Trigger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AuroraService {

    private AuroraService() {}

    public static void trigger(Player player, ItemStack itemStack) {
        CollectionManager manager = AuroraCollectionsProvider.getCollectionManager();
        TypeId typeId = AuroraAPI.getItemManager().resolveId(itemStack);

        manager.progressCollections(player, typeId, 1, Trigger.BLOCK_LOOT);
    }
}
