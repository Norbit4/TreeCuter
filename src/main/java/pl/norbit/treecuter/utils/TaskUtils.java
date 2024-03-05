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

    public static void runTaskLater(Runnable runnable, long delay){
        scheduler.runTaskLater(inst, runnable, delay);
    }
    public static BukkitTask runTaskTimer(Runnable runnable, long delay, long period){
        return scheduler.runTaskTimer(inst, runnable, delay, period);
    }

    public static void runTaskLaterAsynchronously(Runnable runnable, long delay){
        scheduler.runTaskLaterAsynchronously(inst, runnable, delay);
    }

    public static void runTaskTimerAsynchronously(Runnable runnable, long delay, long period){
        scheduler.runTaskTimerAsynchronously(inst, runnable, delay, period);
    }
}
