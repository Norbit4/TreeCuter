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
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldGuardUtils {
    private static final Map<String, RegionManager> REGION_CACHE = new HashMap<>();

    private WorldGuardUtils() {
        throw new IllegalStateException("This class cannot be instantiated");
    }

    public static RegionManager getRegionManager(World world) {
        var cached = REGION_CACHE.get(world.getName());

        if(cached != null){
            return cached;
        }

        var container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        var current = container.get(BukkitAdapter.adapt(world));
        REGION_CACHE.put(world.getName(), current);

        return current;
    }

    public static boolean canBreak(Location loc, Player p) {
        if(p.isOp()){
            return true;
        }

        var regionManager = getRegionManager(loc.getWorld());

        if (regionManager == null){
            return true;
        }

        List<ProtectedRegion> regionsByLoc = getRegionsByLoc(loc, regionManager);

        if(regionsByLoc.isEmpty()){
            return true;
        }

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

        if(region.isOwner(localPlayer)){
            return true;
        }

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
