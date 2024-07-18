package pl.norbit.treecuter.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Block;
import org.bukkit.block.Lidded;

import java.util.List;
import java.util.Set;

@Getter
@AllArgsConstructor
public class GlowBlock {
    private final List<Block> blocks;
    @Setter
    private int time;
}
