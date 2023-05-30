package pl.norbit.treecuter;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    private static final int MAX_TREE_HEIGHT = 10;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (block.getType() == Material.OAK_LOG || block.getType() == Material.BIRCH_LOG ||
                block.getType() == Material.SPRUCE_LOG || block.getType() == Material.JUNGLE_LOG ||
                block.getType() == Material.ACACIA_LOG || block.getType() == Material.DARK_OAK_LOG) {
            Block bottomBlock = getBottomBlock(block);

            if (bottomBlock != null) destroyTree(bottomBlock);
        }
    }

    private Block getBottomBlock(Block block) {
        Block bottomBlock = block;

        while (bottomBlock.getType() == block.getType()) {
            Block tempBlock = bottomBlock.getRelative(0, -1, 0);
            if (tempBlock.getType() != block.getType() || tempBlock.getLocation().getY() <= block.getLocation().getY() - MAX_TREE_HEIGHT) {
                return bottomBlock;
            }
            bottomBlock = tempBlock;
        }

        return null;
    }

    private void destroyTree(Block bottomBlock) {
        Material logType = bottomBlock.getType();

        for (int y = 0; y <= MAX_TREE_HEIGHT; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    Block cBlock = bottomBlock.getRelative(x, y, z);
                    if (cBlock.getType() == logType) {
                        cBlock.breakNaturally();
                    }
                }
            }
        }
    }
}
