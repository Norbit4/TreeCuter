package pl.norbit.treecuter.model;

import org.bukkit.block.Block;

import java.util.Set;

public class GlowBlock {
    private int time;
    private final Set<Block> blocks;

    public GlowBlock(Set<Block> blocks) {
        this.time = 12;
        this.blocks = blocks;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public Set<Block> getBlocks() {
        return blocks;
    }
}
