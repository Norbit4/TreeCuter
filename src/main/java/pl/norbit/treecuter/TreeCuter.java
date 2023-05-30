package pl.norbit.treecuter;

import org.bukkit.plugin.java.JavaPlugin;

public final class TreeCuter extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
