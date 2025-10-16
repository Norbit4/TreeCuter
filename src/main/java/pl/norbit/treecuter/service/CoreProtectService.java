package pl.norbit.treecuter.service;

import net.coreprotect.CoreProtectAPI;
import org.bukkit.block.BlockState;
import pl.norbit.treecuter.utils.CoreProtectUtils;

public class CoreProtectService {
    private static CoreProtectAPI coreProtectAPI;

    private CoreProtectService() {
        throw new IllegalStateException("This class cannot be instantiated");
    }

    public static void load(){
        coreProtectAPI = CoreProtectUtils.getCoreProtect();
    }

    public static void logBreak(String playerName, BlockState blockState) {
        if(coreProtectAPI == null){
            return;
        }
        coreProtectAPI.logRemoval(playerName, blockState);
    }
}
