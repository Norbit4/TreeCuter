package pl.norbit.treecuter.utils;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.plugin.Plugin;
import pl.norbit.treecuter.TreeCuter;

public class CoreProtectUtils {

    private CoreProtectUtils() {
        throw new IllegalStateException("This class cannot be instantiated");
    }

    public static CoreProtectAPI getCoreProtect() {
        Plugin plugin = TreeCuter.getInstance().getServer().getPluginManager().getPlugin("CoreProtect");

        // Check that CoreProtect is loaded
        if (!(plugin instanceof CoreProtect)) {
            return null;
        }

        // Check that the API is enabled
        CoreProtectAPI CoreProtect = ((CoreProtect) plugin).getAPI();
        if (!CoreProtect.isEnabled()) {
            return null;
        }

        // Check that a compatible version of the API is loaded
        if (CoreProtect.APIVersion() < 10) {
            return null;
        }

        return CoreProtect;
    }
}
