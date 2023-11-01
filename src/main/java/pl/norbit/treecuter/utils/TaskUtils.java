package pl.norbit.treecuter.utils;

import org.bukkit.scheduler.BukkitTask;
import pl.norbit.treecuter.TreeCuter;

public class TaskUtils {

    public static void runTaskLater(Runnable runnable, long delay){
        TreeCuter inst = TreeCuter.getInstance();
        inst.getServer().getScheduler().runTaskLater(inst, runnable, delay);
    }
    public static BukkitTask runTaskTimer(Runnable runnable, long delay, long period){
        TreeCuter inst = TreeCuter.getInstance();
        return inst.getServer().getScheduler().runTaskTimer(inst, runnable, delay, period);
    }

    public static void runTaskLaterAsynchronously(Runnable runnable, long delay){
        TreeCuter inst = TreeCuter.getInstance();
        inst.getServer().getScheduler().runTaskLaterAsynchronously(inst, runnable, delay);
    }

    public static void runTaskTimerAsynchronously(Runnable runnable, long delay, long period){
        TreeCuter inst = TreeCuter.getInstance();
        inst.getServer().getScheduler().runTaskTimerAsynchronously(inst, runnable, delay, period);
    }
}
