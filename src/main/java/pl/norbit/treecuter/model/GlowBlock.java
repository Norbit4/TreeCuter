package pl.norbit.treecuter.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Block;

import java.util.Set;

@Getter
@AllArgsConstructor
public class GlowBlock {
    private final Set<Block> blocks;
    @Setter
    private int time;
}
