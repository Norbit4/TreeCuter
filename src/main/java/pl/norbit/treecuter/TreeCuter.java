package pl.norbit.treecuter;

import org.bukkit.plugin.java.JavaPlugin;
import pl.norbit.treecuter.commands.TreeCuterCommand;
import pl.norbit.treecuter.config.Settings;
import pl.norbit.treecuter.glow.GlowingService;
import pl.norbit.treecuter.service.BlockBreakService;
import pl.norbit.treecuter.service.TreeCutService;
import pl.norbit.treecuter.service.TreePlanterService;


public final class TreeCuter extends JavaPlugin {

    private static TreeCuter instance;

    @Override
    public void onEnable() {
        instance = this;
        Settings.loadConfig(false);

        if(Settings.AUTO_PLANT) TreePlanterService.start();

        infoMessage();
        checkPlugins();

        TreeCutService.start();
        BlockBreakService.start();
        GlowingService.init();

        getCommand("treecuter").setExecutor(new TreeCuterCommand());

        var pM = getServer().getPluginManager();
        pM.registerEvents(new BlockBreakService(), this);
    }

    private void checkPlugins(){
        Settings.JOBS_IS_ENABLED = checkPlugin("Jobs");
        Settings.WORLDGUARD_IS_ENABLED = checkPlugin("WorldGuard");
        Settings.ITEMS_ADDER_IS_ENABLED = checkPlugin("ItemsAdder");

        if(Settings.JOBS_IS_ENABLED || Settings.WORLDGUARD_IS_ENABLED || Settings.ITEMS_ADDER_IS_ENABLED)
            getServer().getLogger().info("");
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

    public static TreeCuter getInstance() {
        return instance;
    }
}
