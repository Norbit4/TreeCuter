package pl.norbit.treecuter;

import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import pl.norbit.treecuter.commands.ReloadCommand;
import pl.norbit.treecuter.config.Settings;
import pl.norbit.treecuter.glow.GlowingService;
import pl.norbit.treecuter.service.BlockBreakService;
import pl.norbit.treecuter.service.TreeCutService;
import pl.norbit.treecuter.service.TreePlanterService;

import java.util.logging.Logger;


public final class TreeCuter extends JavaPlugin {

    private static TreeCuter instance;

    @Override
    public void onEnable() {
        instance = this;
        Settings.loadConfig(false);

        if(Settings.AUTO_PLANT) TreePlanterService.start();

        checkJobs();

        infoMessage();

        TreeCutService.start();
        BlockBreakService.start();
        GlowingService.init();

        getCommand("treecuter").setExecutor(new ReloadCommand());

        var pM = getServer().getPluginManager();
        pM.registerEvents(new BlockBreakService(), this);
    }

    private static void infoMessage(){
        Server server = TreeCuter.getInstance().getServer();

        Logger log = server.getLogger();
        log.info("");
        log.info("TreeCuter by Norbit4!");
        log.info("Website: https://n0rbit.pl/");
        log.info("");
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
