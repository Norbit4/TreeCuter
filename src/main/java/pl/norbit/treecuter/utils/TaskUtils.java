package pl.norbit.treecuter.utils;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import pl.norbit.treecuter.TreeCuter;

public class TaskUtils {
    private static final JavaPlugin inst = TreeCuter.getInstance();
    private static final BukkitScheduler scheduler = inst.getServer().getScheduler();

    private TaskUtils() {
        throw new IllegalStateException("This class cannot be instantiated");
    }

    public static void sync(Runnable runnable){
        scheduler.runTask(inst, runnable);
    }

    public static BukkitTask timer(Runnable runnable, long period){
        return scheduler.runTaskTimer(inst, runnable, 0L, period);
    }

    public static void async(Runnable runnable){
        scheduler.runTaskAsynchronously(inst, runnable);
    }

    public static void timerAsync(Runnable runnable, long period){
        scheduler.runTaskTimerAsynchronously(inst, runnable, 0L, period);
    }
}
