package pl.norbit.treecuter.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerData {
    private UUID playerUUID;

    private String playerName;
    private int cutTrees;
    private int cutBlocks;

    private boolean enabled;
}
