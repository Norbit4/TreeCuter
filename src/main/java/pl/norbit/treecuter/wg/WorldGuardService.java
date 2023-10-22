package pl.norbit.treecuter.wg;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;

public class WorldGuardService {

    public static boolean canBreak(Location c, Player p) {
        var container = WorldGuard.getInstance().getPlatform().getRegionContainer();

        if(p.isOp()) return true;

        var regions = container.get(BukkitAdapter.adapt(c.getWorld()));

        if (regions == null) return false;

        Collection<ProtectedRegion> values = regions.getRegions().values();

        for (ProtectedRegion value : values) {

            if (!value.contains(BlockVector3.at(c.getX(), c.getY(), c.getZ()))) continue;
            StateFlag.State flag = value.getFlag(Flags.BLOCK_BREAK);

            return flag == StateFlag.State.ALLOW;
        }
        return true;
    }
}
