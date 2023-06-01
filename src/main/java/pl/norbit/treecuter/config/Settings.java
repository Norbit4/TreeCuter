package pl.norbit.treecuter.config;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import pl.norbit.treecuter.TreeCuter;

import java.util.ArrayList;
import java.util.List;

public class Settings {
    public static int EFFECT_LEVEL,  MAX_TREE_HEIGHT;
    public static boolean SHIFT_MINING, ACCEPT_NO_TOOLS, APPLY_MINING_EFFECT, ITEMS_TO_INVENTORY, JOBS_IS_ENABLED;
    public static List<Material> ACCEPT_TOOLS, ACCEPT_BLOCKS;

    public static void loadConfig(){

        JavaPlugin javaPlugin = TreeCuter.getInstance();
        FileConfiguration config = javaPlugin.getConfig();

        config.options().copyDefaults();
        javaPlugin.saveDefaultConfig();

        MAX_TREE_HEIGHT = config.getInt("max-tree-height");
        EFFECT_LEVEL = config.getInt("effect-level");

        SHIFT_MINING = config.getBoolean("shift-mining");
        ACCEPT_NO_TOOLS = config.getBoolean("accept-no-tools");
        APPLY_MINING_EFFECT = config.getBoolean("apply-mining-effect");
        ITEMS_TO_INVENTORY = config.getBoolean("items-to-inventory");

        ACCEPT_TOOLS = new ArrayList<>();
        ACCEPT_BLOCKS = new ArrayList<>();

        for (String sMat : config.getStringList("accept-tools")) {
            Material material = Material.getMaterial(sMat.toUpperCase());

            if (material == null) continue;

            ACCEPT_TOOLS.add(material);
        }

        for (String sMat : config.getStringList("accept-blocks")) {
            Material material = Material.getMaterial(sMat.toUpperCase());

            if (material == null) continue;

            ACCEPT_BLOCKS.add(material);
        }
    }
}
