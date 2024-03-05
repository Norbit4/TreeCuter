package pl.norbit.treecuter;

import org.bukkit.plugin.java.JavaPlugin;
import pl.norbit.treecuter.commands.TreeCuterCommand;
import pl.norbit.treecuter.config.Settings;
import pl.norbit.treecuter.listeners.BlockBreakListener;
import pl.norbit.treecuter.listeners.BlockInteractListener;
import pl.norbit.treecuter.listeners.TreeListeners;
import pl.norbit.treecuter.listeners.UnglowListener;
import pl.norbit.treecuter.service.EffectService;
import pl.norbit.treecuter.utils.GlowUtils;

public final class TreeCuter extends JavaPlugin {
    private static TreeCuter instance;
    public static TreeCuter getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        Settings.loadConfig(false);

        GlowUtils.init(this);
        EffectService.start();

        infoMessage();
        checkPlugins();

        registerCommand();
        registerListeners();
    }

    private void registerCommand(){
        var command = getCommand("treecuter");
        var treeCuterCommand = new TreeCuterCommand();

        command.setExecutor(treeCuterCommand);
        command.setTabCompleter(treeCuterCommand);
    }

    private void registerListeners(){
        var pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new TreeListeners(), this);
        pluginManager.registerEvents(new BlockBreakListener(), this);
        pluginManager.registerEvents(new BlockInteractListener(), this);
        pluginManager.registerEvents(new UnglowListener(), this);
    }

    private void checkPlugins(){
        Settings.WORLDGUARD_IS_ENABLED = checkPlugin("WorldGuard");
        Settings.ITEMS_ADDER_IS_ENABLED = checkPlugin("ItemsAdder");

        if(Settings.WORLDGUARD_IS_ENABLED || Settings.ITEMS_ADDER_IS_ENABLED) getServer().getLogger().info("");
    }

    private void infoMessage(){
        var logger = getServer().getLogger();
        logger.info("");
        logger.info("TreeCuter by Norbit4!");
        logger.info("Website: https://n0rbit.pl/");
        logger.info("");
    }

    private boolean checkPlugin(String pluginName) {
        var pM = getServer().getPluginManager();
        var plugin = pM.getPlugin(pluginName);

        if(plugin != null && plugin.isEnabled()){
            var logger = getServer().getLogger();
            logger.info("Hooked to: " + pluginName);
            return true;
        }
        return false;
    }
}
