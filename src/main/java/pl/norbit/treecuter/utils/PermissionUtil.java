package pl.norbit.treecuter.utils;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PermissionUtil {
    private final HashSet<String> permissionsSet;
    private final Player p;

    public PermissionUtil(Player p) {
        permissionsSet = new HashSet<>();
        this.p = p;

        p.getEffectivePermissions().forEach(permInfo -> permissionsSet.add(permInfo.getPermission()));
    }

    public boolean hasPermission(String... perms) {
        if(p.isOp()) return true;

        for (String perm : perms) if (permissionsSet.contains(perm)) return true;

        return false;
    }

    public boolean hasPermission(List<String > perms){
        if(p.isOp()) return true;

        for (String perm : perms) if(permissionsSet.contains(perm)) return true;

        return false;
    }

    public List<String> getSamePermissions(List<String > perms){
        List<String> permList = new ArrayList<>();

        for (String perm : perms) if(permissionsSet.contains(perm)) permList.add(perm);

        if(permList.isEmpty()) return null;

        return permList;
    }
}
