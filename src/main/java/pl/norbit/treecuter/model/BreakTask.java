package pl.norbit.treecuter.model;

import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import pl.norbit.treecuter.config.model.CutShape;
import pl.norbit.treecuter.service.LeafDecayService;


import java.util.*;

public class BreakTask {

    private final Queue<Block> blocksQueue;
    private final List<Block> blocks;
    private final CutShape cutShape;
    private final int perLoopBreakSize;
    @Getter
    private final Player player;

    public BreakTask(List<Block> blocks, Player player, CutShape cutShape) {
        this.blocksQueue = new ArrayDeque<>(blocks);
        this.blocks = blocks;
        this.cutShape = cutShape;

        int size = blocks.size();
        int perLoopSize;

        if (size < 15) {
            perLoopSize = 2;
        } else {
            perLoopSize = 5;
        }

        this.perLoopBreakSize = perLoopSize;
        this.player = player;
    }

    public void leafTask(){
        LeafDecayService.scanLeaves(blocks, cutShape);
    }

    public boolean isTreeBroken() {
        return blocksQueue.isEmpty();
    }

    public List<Block> getBlocksToBreak() {
        List<Block> blocksToBreak = new ArrayList<>();
        for (int i = 0; i < perLoopBreakSize && !blocksQueue.isEmpty(); i++) {
            blocksToBreak.add(blocksQueue.poll());
        }
        return blocksToBreak;
    }
}
