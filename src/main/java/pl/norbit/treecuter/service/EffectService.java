package pl.norbit.treecuter.service;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.norbit.treecuter.config.Settings;
import pl.norbit.treecuter.model.EffectPlayer;
import pl.norbit.treecuter.utils.GlowUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static pl.norbit.treecuter.utils.TaskUtils.sync;
import static pl.norbit.treecuter.utils.TaskUtils.timerAsync;

public class EffectService {
    private static final Map<UUID, EffectPlayer> effectPlayersMap = new ConcurrentHashMap<>();

    private EffectService() {
        throw new IllegalStateException("This class cannot be instantiated");
    }

    private static List<EffectPlayer> getValues(){
        return new ArrayList<>(effectPlayersMap.values());
    }

    public static void start(){
        timerAsync(() -> {
            //remove players that are not sneaking
            getValues()
                    .stream()
                    .filter(p -> !p.getPlayer().isSneaking())
                    .toList()
                    .forEach(p -> {
                        Player player = p.getPlayer();
                        effectPlayersMap.remove(p.getPlayer().getUniqueId());
                        TreeCutService.removeColorFromTree(player);
                    });
            //start it in synchronized task because effects in minecraft cannot be applied in async task
            sync(() -> effectPlayersMap.values().forEach(p -> EffectService.addSlowDiggingEffect(p.getPlayer())));
        }, 10L);

        //update glowing blocks
        timerAsync(() -> getValues().forEach(p ->{
            Player player = p.getPlayer();

            if(!player.isOnline()){
                return;
            }

            int time = p.getUpdateTime() - 1;

            if(time == 0){
                List<Block> selectedBlocks = TreeCutService.getSelectedBlocks(player);
                GlowUtils.setGlowing(selectedBlocks, player, Settings.getGlowingColor());
                p.setUpdateTime(9);
            }else {
                p.setUpdateTime(time);
            }
        }), 20L);
    }

    private static void addSlowDiggingEffect(Player p){
        int effectLevel = Settings.getDefaultEffectLevel() - 1;

        var potionEffect = new PotionEffect(PotionEffectType.SLOW_DIGGING, 12, effectLevel);
        p.addPotionEffect(potionEffect);
    }

    public static boolean isEffectPlayer(Player p){
        if(!Settings.isShiftMining()){
            return true;
        }

        if(!Settings.isApplyMiningEffect()){
            return true;
        }

        UUID uniqueId = p.getUniqueId();

        return effectPlayersMap.containsKey(uniqueId);
    }

    public static void removeEffect(Player p){
        effectPlayersMap.remove(p.getUniqueId());
        p.removePotionEffect(PotionEffectType.SLOW_DIGGING);
    }

    public static void applyEffect(Player p){
        if(!Settings.isShiftMining()){
            return;
        }

        if(!p.isSneaking()){
            return;
        }

        if(!Settings.isApplyMiningEffect()){
            return;
        }

        EffectPlayer effectPlayer = effectPlayersMap.get(p.getUniqueId());

        if(effectPlayer != null){
            effectPlayer.setUpdateTime(9);
            return;
        }

        UUID playerUUID = p.getUniqueId();

        effectPlayersMap.compute(playerUUID, (k, v) -> new EffectPlayer(playerUUID, 9));
        addSlowDiggingEffect(p);
    }
}
