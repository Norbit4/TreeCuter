package pl.norbit.treecuter;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import pl.norbit.treecuter.config.Settings;
import pl.norbit.treecuter.listeners.BlockBreakListener;


public final class TreeCuter extends JavaPlugin {

    private static TreeCuter instance;

    @Override
    public void onEnable() {
        instance = this;
        Settings.loadConfig();

        checkJobs();

        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
    }
    private void checkJobs() {
        PluginManager pluginManager = getServer().getPluginManager();
        Plugin jobsPlugin = pluginManager.getPlugin("Jobs");

        Settings.JOBS_IS_ENABLED = jobsPlugin != null && jobsPlugin.isEnabled();
    }

    public static TreeCuter getInstance() {
        return instance;
    }
}
