package pl.norbit.treecuter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.entity.Player;
import pl.norbit.treecuter.utils.PlayerUtils;

import java.util.UUID;

@Data
@AllArgsConstructor
public class EffectPlayer {
    private UUID playerUUID;
    private int updateTime;

    public Player getPlayer(){
        return PlayerUtils.getPlayer(playerUUID);
    }
}
