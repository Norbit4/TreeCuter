package pl.norbit.treecuter.utils;

import fr.skytasul.glowingentities.GlowingBlocks;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.norbit.treecuter.config.Settings;
import pl.norbit.treecuter.model.GlowBlock;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class GlowUtils {
    private static final Map<Player, GlowBlock> BLOCKS = new ConcurrentHashMap<>();
    private static final List<String> SUPPORTED_VERSIONS = List.of("1.17.1", "1.18.2", "1.19.4", "1.20.2", "1.20.4");
    private static GlowingBlocks glowingBlocks;
    private static boolean enable;

    private GlowUtils() {
        throw new IllegalStateException("This class cannot be instantiated");
    }

    public static boolean isSupportedVersion(String version){
        return SUPPORTED_VERSIONS.stream()
                .filter(version::contains)
                .findFirst()
                .orElse(null) != null;
    }

    public static List<String> getSupportedVersion(){
        return SUPPORTED_VERSIONS;
    }

    public static void init(JavaPlugin instance){
        Server server = instance.getServer();

        if(isSupportedVersion(server.getVersion())) enable = true;

        try {
            glowingBlocks = new GlowingBlocks(instance);
        } catch (Exception e) {
            enable = false;
        }

        if(enable){
            TaskUtils.runTaskTimerAsynchronously(GlowUtils::update, 0L, 20L);
        }else {
            notSupportedMessage(server);
        }
    }

    public static void update(){
        BLOCKS.forEach((p, glowBlock) -> {
            if(glowBlock.getTime() == 0){
                unsetGlowing(glowBlock.getBlocks(), p);

                BLOCKS.compute(p, (existPlayer, gBlock) -> null);
                return;
            }
            glowBlock.setTime(glowBlock.getTime() - 1);
        });
    }

    private static void notSupportedMessage(Server server){
        Logger log = server.getLogger();

        log.warning("Glowing is not supported on this server version: " + server.getVersion());
        log.warning("Suported ver: " + SUPPORTED_VERSIONS.toString());
        log.warning("Suported engines: Paper and forks");
        log.warning("");
    }

    public static void setGlowing(Set<Block> blocksSet, Player p, ChatColor color){
        if(!enable) return;

        if(!Settings.GLOWING_BLOCKS) return;

        if(p == null) return;

        GlowBlock glowBlock = BLOCKS.get(p);

        if(glowBlock != null){
            unsetGlowing(glowBlock.getBlocks(), p);
        }
        blocksSet.forEach(b -> setGlowing(b, p, color));

        BLOCKS.compute(p, (player, blocks) -> new GlowBlock(blocksSet, 12));
    }

    public static void unsetGlowing(Set<Block> blocksSet, Player p){
        if(!enable) return;

        if(p == null) return;

        blocksSet.forEach(b -> unsetGlowing(b, p));

        BLOCKS.compute(p, (player, blocks) -> null);
    }

    public static void setGlowing(Block b, Player p, ChatColor color){
        try {
            glowingBlocks.setGlowing(b, p, color);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void unsetGlowing(Block b, Player p){
        try {
            glowingBlocks.unsetGlowing(b, p);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
