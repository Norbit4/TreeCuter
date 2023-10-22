package pl.norbit.treecuter.glow;

import fr.skytasul.glowingentities.GlowingBlocks;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import pl.norbit.treecuter.TreeCuter;
import pl.norbit.treecuter.config.Settings;
import pl.norbit.treecuter.utils.VersionUtil;

import java.util.logging.Logger;

public class GlowingService {

    private static GlowingBlocks glowingBlocks;
    private static boolean enable;

    public static void init() {
        if(VersionUtil.isSupportedVersion(TreeCuter.getInstance().getServer().getVersion())) enable = true;

        if(!enable){
            send();
            return;
        }

        try {
            glowingBlocks = new GlowingBlocks(TreeCuter.getInstance());
        } catch (Exception e) {
            enable = false;
            send();
        }
    }

    private static void send(){
        Server server = TreeCuter.getInstance().getServer();

        Logger log = server.getLogger();

        log.warning("Glowing is not supported on this server version: " + server.getVersion());
        log.warning("Suported ver: " + VersionUtil.getSupportedVersion().toString());
        log.warning("Suported engines: Paper and forks");
        log.warning("");
    }

    public static void setGlowing(Block b, Player p){
        if(!enable) return;

        if(b == null) return;

        if(!Settings.GLOWING_BLOCKS) return;

        if(b.getType() == Material.AIR) return;

        try {
            glowingBlocks.setGlowing(b, p, Settings.GLOWING_COLOR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void unsetGlowing(Block b, Player p){
        if(!enable) return;

        if(b == null) return;

        if(!Settings.GLOWING_BLOCKS) return;
        try {
            glowingBlocks.unsetGlowing(b, p);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
