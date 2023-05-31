package pl.norbit.treecuter;

import org.bukkit.plugin.java.JavaPlugin;

public final class TreeCuter extends JavaPlugin {

    private static TreeCuter instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    public static TreeCuter getInstance() {
        return instance;
    }
}
