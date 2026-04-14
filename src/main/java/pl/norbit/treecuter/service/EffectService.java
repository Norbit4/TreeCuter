package pl.norbit.treecuter.service;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.norbit.treecuter.config.Settings;
import pl.norbit.treecuter.model.EffectPlayer;
import pl.norbit.treecuter.utils.GlowUtils;

import java.util.*;

import static pl.norbit.treecuter.utils.TaskUtils.*;

public class EffectService {

    private static final Map<UUID, EffectPlayer> effectPlayersMap = new HashMap<>();
    private static PotionEffect slowDiggingEffect;

    private EffectService() {}

    public static void start() {
        reloadEffect();

        timer(() -> {
            Iterator<Map.Entry<UUID, EffectPlayer>> iterator = effectPlayersMap.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<UUID, EffectPlayer> entry = iterator.next();
                EffectPlayer effectPlayer = entry.getValue();

                Player player = effectPlayer.getPlayer();

                if (player == null || !player.isSneaking()) {
                    if (player != null) {
                        TreeCutService.removeColorFromTree(player);
                    }

                    iterator.remove();
                    continue;
                }

                int time = effectPlayer.getUpdateTime() - 1;

                if (time <= 0) {

                    if (player.isOnline()) {
                        List<Block> selectedBlocks = TreeCutService.getSelectedBlocks(player);
                        GlowUtils.setGlowing(selectedBlocks, player, Settings.getGlowingColor());
                    }

                    effectPlayer.setUpdateTime(9);

                } else {
                    effectPlayer.setUpdateTime(time);
                }

                if (player.isOnline()) {
                    player.addPotionEffect(slowDiggingEffect);
                }
            }
        }, 10L);
    }

    public static void reloadEffect() {
        int effectLevel = Settings.getDefaultEffectLevel() - 1;
        boolean hideMiningEffect = !Settings.isHideMiningEffect();

        slowDiggingEffect = new PotionEffect(
                PotionEffectType.MINING_FATIGUE,
                14,
                effectLevel,
                false,
                hideMiningEffect,
                hideMiningEffect
        );
    }

    public static boolean isEffectPlayer(Player player) {

        if (!Settings.isShiftMining() || !Settings.isApplyMiningEffect()) {
            return true;
        }

        return effectPlayersMap.containsKey(player.getUniqueId());
    }

    public static void removeEffect(Player player) {

        UUID uuid = player.getUniqueId();

        effectPlayersMap.remove(uuid);
        player.removePotionEffect(PotionEffectType.SLOWNESS);
    }

    public static void applyEffect(Player player) {
        if (!Settings.isShiftMining() || !Settings.isApplyMiningEffect()) {
            return;
        }

        if (!player.isSneaking()) {
            return;
        }

        UUID uuid = player.getUniqueId();

        EffectPlayer effectPlayer = effectPlayersMap.get(uuid);

        if (effectPlayer != null) {
            effectPlayer.setUpdateTime(9);
            return;
        }

        effectPlayersMap.put(uuid, new EffectPlayer(uuid, 9));
        player.addPotionEffect(slowDiggingEffect);
    }
}