package pl.norbit.treecuter.config;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import pl.norbit.treecuter.TreeCuter;
import pl.norbit.treecuter.config.model.CustomItem;
import pl.norbit.treecuter.config.model.CustomTool;
import pl.norbit.treecuter.config.model.CutShape;
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
    private static boolean hideMiningEffect;
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
    private static List<Material> acceptCustomLeavesBlocks;
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
    private static String toolNotFound;

    @Getter
    private static List<String> helpMessage;

    private static List<Material> groundBlocks;

    private static List<CutShape> woodBlocks;

    @Getter
    private static boolean actionsEnabled;
    @Getter
    private static List<String> actions;

    private Settings() {
        throw new IllegalStateException("This class cannot be instantiated");
    }

    public static Optional<ItemStack> getCustomToolForKey(String key){
        return woodBlocks.stream()
                .filter(shape -> shape.getId().equalsIgnoreCase(key))
                .filter(shape -> shape.getCustomTool() != null)
                .map(CutShape::getCustomToolItem)
                .findFirst();
    }

    public static CutShape getCutShape(Block block, ItemStack tool){
        return woodBlocks.stream()
                .filter(woodBlock -> woodBlock.isAcceptBlock(block.getType()))
                .filter(woodBlock -> woodBlock.isAcceptTool(tool))
                .findFirst()
                .orElse(null);
    }

    public static List<String> getCustomToolKeys(){
        return woodBlocks.stream()
                .filter(shape -> shape.getCustomTool() != null)
                .map(CutShape::getId)
                .toList();
    }

    public static boolean isAcceptedSapling(Material type){
        return autoPlantSapling.contains(type);
    }

    public static boolean isAcceptedWoodBlock(Material type){
        return acceptWoodBlocks.contains(type);
    }

    public static boolean isAcceptedCustomLeavesBlock(Material type){
        return acceptCustomLeavesBlocks.contains(type);
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
        hideMiningEffect = config.getBoolean("mining-effect.hide");
        defaultEffectLevel = config.getInt("mining-effect.default-level");

        itemsToInventory = config.getBoolean("items-to-inventory");

        autoPlant = config.getBoolean("auto-plant");
        usePermissions = config.getBoolean("use-permissions");
        permission = config.getString("permission");

        blockWorlds = config.getStringList("block-worlds");

        decayAmount = config.getInt("leaves.decay-amount");
        leavesEnabled = config.getBoolean("leaves.enable");

        placeholderToggleOn = config.getString("placeholder.toggle-on");
        placeholderToggleOff = config.getString("placeholder.toggle-off");

        actions = config.getStringList("actions.action-list");
        actionsEnabled = config.getBoolean("actions.enable");

        acceptTools = new ArrayList<>();
        acceptWoodBlocks = new ArrayList<>();
        acceptLeavesBlocks = new ArrayList<>();
        acceptCustomLeavesBlocks = new ArrayList<>();
        autoPlantSapling = new ArrayList<>();
        woodBlocks = new ArrayList<>();

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

        config.getStringList("accept-custom-leaves-blocks")
                .stream()
                .map(Material::getMaterial)
                .filter(Objects::nonNull)
                .forEach(acceptCustomLeavesBlocks::add);

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
        toolNotFound = config.getString("messages.tool.not-found");
        helpMessage = config.getStringList("messages.help");

        groundBlocks = config.getStringList("sapling-ground-materials")
                .stream()
                .map(String::toUpperCase)
                .map(Material::getMaterial)
                .filter(Objects::nonNull)
                .toList();

        ConfigurationSection section = config.getConfigurationSection("wood-blocks");

        if (section == null) {
            javaPlugin.getLogger().warning("No wood blocks found in config");
        }else {
            for (String key : section.getKeys(false)) {
                ConfigurationSection woodBlockSection = section.getConfigurationSection(key);

                if (woodBlockSection == null) {
                    javaPlugin.getLogger().warning("No wood block found in config: " + key);
                    continue;
                }

                CutShape woodBlock = getCutShape(woodBlockSection, key);
                woodBlocks.add(woodBlock);
            }
        }

        if(autoPlant) TreePlanterService.start();
        else TreePlanterService.stop();
    }

    private static CutShape getCutShape(ConfigurationSection section, String key) {
        CutShape cutShape = new CutShape();
        cutShape.setId(key);

        String color = section.getString("glowing-color");

        if (color == null) {
            cutShape.setGlowingColor(glowingColor);
        } else {
            try {
                cutShape.setGlowingColor(ChatColor.valueOf(color.toUpperCase()));
            } catch (IllegalArgumentException e) {
                TreeCuter.getInstance().getLogger().warning("Wrong glowing color: " + color);
                cutShape.setGlowingColor(glowingColor);
            }
        }

        List<CustomItem> acceptTools = section.getStringList("accept-tools")
                .stream()
                .map(CustomItem::new)
                .toList();

        cutShape.setAcceptTools(acceptTools);

        ConfigurationSection customToolSection = section.getConfigurationSection("custom-tool");

        if (customToolSection != null) {
            CustomTool customTool = getCustomTool(customToolSection);
            cutShape.setCustomTool(customTool);
        }

        List<Material> acceptBlocks = new ArrayList<>();
        section.getStringList("accept-blocks")
                .stream()
                .map(Material::getMaterial)
                .filter(Objects::nonNull)
                .forEach(acceptBlocks::add);

        cutShape.setAcceptBlocks(acceptBlocks);

        return cutShape;
    }

    private static CustomTool getCustomTool(ConfigurationSection section) {
        CustomTool customTool = new CustomTool();
        customTool.setName(section.getString("name"));
        customTool.setMaterial(section.getString("material"));
        customTool.setLore(section.getStringList("lore"));

        return customTool;
    }
}
