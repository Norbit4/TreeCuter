package pl.norbit.treecuter;

import org.bukkit.plugin.java.JavaPlugin;
import pl.norbit.treecuter.commands.ReloadCommand;
import pl.norbit.treecuter.config.Settings;
import pl.norbit.treecuter.service.BlockBreakService;
import pl.norbit.treecuter.service.TreePlanterService;


public final class TreeCuter extends JavaPlugin {

    private static TreeCuter instance;

    @Override
    public void onEnable() {
        instance = this;
        Settings.loadConfig(false);

        if(Settings.AUTO_PLANT) TreePlanterService.start();

        checkJobs();

        BlockBreakService.start();

        getCommand("treecuter").setExecutor(new ReloadCommand());

        var pM = getServer().getPluginManager();
        pM.registerEvents(new BlockBreakService(), this);
    }

    private void checkJobs() {
        var pM = getServer().getPluginManager();
        var jobsPlugin = pM.getPlugin("Jobs");

        Settings.JOBS_IS_ENABLED = jobsPlugin != null && jobsPlugin.isEnabled();
    }

    public static TreeCuter getInstance() {
        return instance;
    }
}
