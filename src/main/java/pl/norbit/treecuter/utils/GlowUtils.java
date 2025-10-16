package pl.norbit.treecuter.utils;

import fr.skytasul.glowingentities.GlowingBlocks;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.norbit.treecuter.config.Settings;
import pl.norbit.treecuter.exception.GlowException;
import pl.norbit.treecuter.model.GlowBlock;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class GlowUtils {
    private static final Map<Player, GlowBlock> blocks = new ConcurrentHashMap<>();
    private static final List<String> supportedGlowingVersions =
            List.of("1.17.1", "1.18.2", "1.19.4", "1.20.2", "1.20.4", "1.20.5", "1.20.6", "1.21.1"," 1.21.2",
                    "1.21.3", "1.21.4", "1.21.5", "1.21.6", "1.21.7", "1.21.8", "1.21.9", "1.21.10");
    private static GlowingBlocks glowingBlocks;
    private static boolean enable;

    private GlowUtils() {
        throw new IllegalStateException("This class cannot be instantiated");
    }

    public static boolean isSupportedVersion(String version){
        return supportedGlowingVersions.stream()
                .filter(version::contains)
                .findFirst()
                .orElse(null) != null;
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
            TaskUtils.timerAsync(GlowUtils::update,20L);
        }else {
            notSupportedMessage(server);
        }
    }

    public static void update(){
        blocks.forEach((p, glowBlock) -> {
            if(glowBlock.getTime() == 0){
                unsetGlowing(glowBlock.getBlocks(), p);

                blocks.compute(p, (existPlayer, gBlock) -> null);
                return;
            }
            glowBlock.setTime(glowBlock.getTime() - 1);
        });
    }

    private static void notSupportedMessage(Server server){
        Logger log = server.getLogger();

        log.warning("Glowing is not supported on this server version: " + server.getVersion());
        log.warning("Suported ver: " + supportedGlowingVersions.toString());
        log.warning("Suported engines: Paper and forks");
        log.warning("");
    }

    public static void setGlowing(List<Block> blocksSet, Player p, ChatColor color){
        if(!enable){
            return;
        }

        if(!Settings.isGlowingBlocks()){
            return;
        }

        if(p == null){
            return;
        }

        GlowBlock glowBlock = blocks.get(p);

        if(glowBlock != null){
            unsetGlowing(glowBlock.getBlocks(), p);
        }
        blocksSet.forEach(b -> setGlowing(b, p, color));

        blocks.compute(p, (player, blocks) -> new GlowBlock(blocksSet, 12));
    }

    public static void unsetGlowing(List<Block> blocksSet, Player p){
        if(!enable){
            return;
        }

        if(blocksSet == null){
            return;
        }

        if(p == null){
            return;
        }

        blocksSet.forEach(b -> unsetGlowing(b, p));

        blocks.compute(p, (player, blocks) -> null);
    }

    public static void setGlowing(Block b, Player p, ChatColor color){
        try {
            glowingBlocks.setGlowing(b, p, color);
        } catch (Exception e) {
            throw new GlowException("Error while setting glowing block", e);
        }
    }

    public static void unsetGlowing(Block b, Player p){
        if(!enable){
            return;
        }

        if(b == null){
            return;
        }

        try {
            glowingBlocks.unsetGlowing(b, p);
        } catch (Exception e) {
            throw new GlowException("Error while unsetting glowing block", e);
        }
    }
}
