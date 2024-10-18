package pl.norbit.treecuter.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class WorldGuardUtils {

    private WorldGuardUtils() {
        throw new IllegalStateException("This class cannot be instantiated");
    }

    public static boolean canBreak(Location loc, Player p) {
        var container = WorldGuard.getInstance().getPlatform().getRegionContainer();

        if(p.isOp()){
            return true;
        }

        var regionManager = container.get(BukkitAdapter.adapt(loc.getWorld()));

        if (regionManager == null){
            return true;
        }

        List<ProtectedRegion> regionsByLoc = getRegionsByLoc(loc, regionManager);

        for (ProtectedRegion protectedRegion : regionsByLoc) {
            if (canBreakInRegion(p, protectedRegion)){
                return true;
            }
        }
        return false;
    }

    private static boolean canBreakInRegion(Player p, ProtectedRegion region) {
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(p);
        StateFlag.State flag = region.getFlag(Flags.BLOCK_BREAK);

        if(region.isMember(localPlayer) && flag != StateFlag.State.DENY){
            return true;
        }

        return flag == StateFlag.State.ALLOW;
    }

    private static List<ProtectedRegion> getRegionsByLoc(Location loc, RegionManager manager) {
        Collection<ProtectedRegion> regions = manager.getRegions().values();

        return regions.stream()
                .filter(value -> value.contains(BlockVector3.at(loc.getX(), loc.getY(), loc.getZ())))
                .toList();
    }
}
