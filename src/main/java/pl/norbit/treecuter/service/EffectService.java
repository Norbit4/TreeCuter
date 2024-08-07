package pl.norbit.treecuter.service;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.norbit.treecuter.config.Settings;
import pl.norbit.treecuter.utils.GlowUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static pl.norbit.treecuter.utils.TaskUtils.sync;
import static pl.norbit.treecuter.utils.TaskUtils.timerAsync;

public class EffectService {
    private static final Set<Player> EFFECT_PLAYERS = new HashSet<>();

    private EffectService() {
        throw new IllegalStateException("This class cannot be instantiated");
    }

    public static void start(){
        timerAsync(() -> {
            //remove players that are not sneaking
            EFFECT_PLAYERS.stream()
                    .filter(p -> !p.isSneaking())
                    .toList()
                    .forEach( p -> {
                        EFFECT_PLAYERS.remove(p);
                        TreeCutService.removeColorFromTree(p);
                    });

            //start it in synchronized task because effects in minecraft cannot be applied in async task
            sync(() -> EFFECT_PLAYERS.forEach(EffectService::addSlowDiggingEffect));
        }, 22L);

        //update glowing blocks
        timerAsync(() -> EFFECT_PLAYERS.forEach(p ->{
            List<Block> selectedBlocks = TreeCutService.getSelectedBlocks(p);

            GlowUtils.setGlowing(selectedBlocks, p, Settings.getGlowingColor());

        }), 20 * 6L);
    }

    private static void addSlowDiggingEffect(Player p){
        int effectLevel = Settings.getDefaultEffectLevel() - 1;

        var potionEffect = new PotionEffect(PotionEffectType.SLOW_DIGGING, 24, effectLevel);
        p.addPotionEffect(potionEffect);
    }


    public static boolean isEffectPlayer(Player p){
        if(!Settings.isShiftMining()){
            return true;
        }

        if(!Settings.isApplyMiningEffect()){
            return true;
        }

        return EFFECT_PLAYERS.contains(p);
    }

    public static void removeEffect(Player p){
        EFFECT_PLAYERS.remove(p);
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

        EFFECT_PLAYERS.add(p);
        addSlowDiggingEffect(p);
    }
}
