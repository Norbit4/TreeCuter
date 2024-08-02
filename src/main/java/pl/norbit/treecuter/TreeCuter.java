package pl.norbit.treecuter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;
import pl.norbit.treecuter.commands.TreeCuterCommand;
import pl.norbit.treecuter.config.Settings;
import pl.norbit.treecuter.listeners.BlockBreakListener;
import pl.norbit.treecuter.listeners.BlockInteractListener;
import pl.norbit.treecuter.listeners.TreeListeners;
import pl.norbit.treecuter.listeners.UnglowListener;
import pl.norbit.treecuter.service.EffectService;
import pl.norbit.treecuter.service.TreeCutService;
import pl.norbit.treecuter.utils.GlowUtils;

public final class TreeCuter extends JavaPlugin {

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private static TreeCuter instance;

    @Override
    public void onEnable() {
        setInstance(this);
        Settings.loadConfig(false);

        GlowUtils.init(this);
        EffectService.start();
        TreeCutService.start();

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
        Settings.setWorldGuardEnabled(checkPlugin("WorldGuard"));
        Settings.setItemsAdderEnabled(checkPlugin("ItemsAdder"));

        if(Settings.isWorldGuardEnabled() || Settings.isItemsAdderEnabled()){
            getServer().getLogger().info("");
        }
    }

    private void infoMessage(){
        var logger = getServer().getLogger();
        logger.info("");
        logger.info("TreeCuter by Norbit4!");
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
