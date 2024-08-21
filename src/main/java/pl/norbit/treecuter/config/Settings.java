package pl.norbit.treecuter.config;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import pl.norbit.treecuter.TreeCuter;
import pl.norbit.treecuter.service.TreePlanterService;

import java.util.*;

public class Settings {

    @Getter
    private static int defaultEffectLevel;

    //block limits
    @Getter
    private static int maxBlocks;
    @Getter
    private static int minBlocks;

    //addons
    @Getter
    @Setter
    private static boolean worldGuardEnabled;
    @Getter
    @Setter
    private static boolean itemsAdderEnabled;

    @Getter
    @Setter
    private static boolean placeholderApiEnabled;

    //settings
    @Getter
    private static boolean shiftMining;
    @Getter
    private static boolean applyMiningEffect;
    @Getter
    private static boolean itemsToInventory;
    @Getter
    private static boolean glowingBlocks;
    private static boolean autoPlant;

    @Getter
    private static String placeholderToggleOn;

    @Getter
    private static String placeholderToggleOff;

    //accepted blocks
    private static List<Material> acceptTools;
    @Getter
    private static List<Material> acceptWoodBlocks;
    @Getter
    private static List<Material> acceptLeavesBlocks;
    @Getter
    private static List<Material> autoPlantSapling;

    //block worlds
    private static List<String> blockWorlds;

    //leaves
    @Getter
    private static boolean leavesEnabled;

    @Getter
    private static int decayAmount;

    //permission
    @Getter
    private static boolean usePermissions;

    @Getter
    private static String permission;

    @Getter
    private static ChatColor glowingColor;

    //custom tool
    @Getter
    private static String toolName;
    @Getter
    private static String toolMaterial;
    @Getter
    private static boolean toolEnable;

    //messages
    @Getter
    private static String permissionMessage;

    @Getter
    private static String toggleMessageOn;
    @Getter
    private static String toggleMessageOff;

    @Getter
    private static String consoleMessage;

    @Getter
    private static String reloadStart;
    @Getter
    private static String reloadEnd;

    @Getter
    private static String toolGet;
    @Getter
    private static String toolDisabled;

    @Getter
    private static List<String> helpMessage;

    private static List<Material> groundBlocks;

    private Settings() {
        throw new IllegalStateException("This class cannot be instantiated");
    }

    public static boolean isAcceptedSapling(Material type){
        return autoPlantSapling.contains(type);
    }

    public static boolean isAcceptedWoodBlock(Material type){
        return acceptWoodBlocks.contains(type);
    }

    public static boolean isAcceptedTool(Material type){
        return acceptTools.contains(type);
    }

    public static boolean isBlockedWorld(String worldName){
        return blockWorlds.contains(worldName);
    }

    public static boolean isGround(Material type){
        return groundBlocks.contains(type);
    }

    public static void loadConfig(boolean reload) {
        var javaPlugin = TreeCuter.getInstance();

        if(!reload) javaPlugin.saveDefaultConfig();
        else javaPlugin.reloadConfig();

        var config = javaPlugin.getConfig();

        maxBlocks = config.getInt("max-blocks");
        minBlocks = config.getInt("min-blocks");

        shiftMining = config.getBoolean("shift-mining");

        applyMiningEffect = config.getBoolean("mining-effect.enable");
        defaultEffectLevel = config.getInt("mining-effect.default-level");

        itemsToInventory = config.getBoolean("items-to-inventory");

        autoPlant = config.getBoolean("auto-plant");
        usePermissions = config.getBoolean("use-permissions");
        permission = config.getString("permission");

        blockWorlds = config.getStringList("block-worlds");

        decayAmount = config.getInt("leaves.decay-amount");
        leavesEnabled = config.getBoolean("leaves.enable");

        toolEnable = config.getBoolean("custom-tool.enable");
        toolName = config.getString("custom-tool.name");

        toolMaterial = config.getString("custom-tool.material");

        placeholderToggleOn = config.getString("placeholder.toggle-on");
        placeholderToggleOff = config.getString("placeholder.toggle-off");

        acceptTools = new ArrayList<>();
        acceptWoodBlocks = new ArrayList<>();
        acceptLeavesBlocks = new ArrayList<>();
        autoPlantSapling = new ArrayList<>();

        config.getStringList("accept-tools")
                .stream()
                .map(Material::getMaterial)
                .filter(Objects::nonNull)
                .forEach(acceptTools::add);

        config.getStringList("accept-wood-blocks")
                .stream()
                .map(Material::getMaterial)
                .filter(Objects::nonNull)
                .forEach(acceptWoodBlocks::add);

        config.getStringList("accept-leaves-blocks")
                .stream()
                .map(Material::getMaterial)
                .filter(Objects::nonNull)
                .forEach(acceptLeavesBlocks::add);

        config.getStringList("auto-plant-saplings")
                .stream()
                .map(Material::getMaterial)
                .filter(Objects::nonNull)
                .forEach(autoPlantSapling::add);

        glowingBlocks = config.getBoolean("glowing-blocks");

        String color = config.getString("glowing-color");

        try {
            glowingColor = ChatColor.valueOf(color.toUpperCase());
        }catch (IllegalArgumentException e){
            TreeCuter.getInstance().getLogger().warning("Wrong glowing color: " + color);
            glowingColor = ChatColor.RED;
        }

        permissionMessage = config.getString("messages.permission");
        toggleMessageOn = config.getString("messages.toggle.enable");
        toggleMessageOff = config.getString("messages.toggle.disable");
        consoleMessage = config.getString("messages.console");
        reloadStart = config.getString("messages.reload.start");
        reloadEnd = config.getString("messages.reload.end");
        toolGet = config.getString("messages.tool.get");
        toolDisabled = config.getString("messages.tool.disabled");
        helpMessage = config.getStringList("messages.help");

        groundBlocks = config.getStringList("sapling-ground-materials")
                .stream()
                .map(String::toUpperCase)
                .map(Material::getMaterial)
                .filter(Objects::nonNull)
                .toList();

        if(autoPlant) TreePlanterService.start();
        else TreePlanterService.stop();
    }
}
