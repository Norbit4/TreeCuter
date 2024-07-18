package pl.norbit.treecuter.model;

import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BreakTask {

    private final Queue<Block> blocksQueue;
    private final int perLoopBreakSize;
    @Getter
    private final Player player;

    public BreakTask(List<Block> blocks, Player player) {
        this.blocksQueue = new LinkedList<>(blocks);

        int perLoopSize = blocks.size()/10;

        if(perLoopSize == 0){
            perLoopSize = 1;
        }

        this.perLoopBreakSize = perLoopSize;
        this.player = player;
    }

    public boolean isTreeBroken() {
        return blocksQueue.isEmpty();
    }

    public List<Block> getBlocksToBreak() {
        List<Block> blocks = new LinkedList<>();
        for (int i = 0; i < perLoopBreakSize; i++) {
            if (blocksQueue.isEmpty()) {
                break;
            }
            blocks.add(blocksQueue.poll());
        }
        return blocks;
    }
}
