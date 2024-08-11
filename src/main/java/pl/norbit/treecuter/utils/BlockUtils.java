package pl.norbit.treecuter.utils;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import pl.norbit.treecuter.config.Settings;

import java.util.Arrays;
import java.util.List;

public class BlockUtils {

    private BlockUtils() {
        throw new IllegalStateException("This class cannot be instantiated");
    }

    public static List<Block> getWoodBlocksAround(List<Block> blocks, Block b, int maxBlocks) {
        Arrays.stream(BlockFace.values())
                .map(b::getRelative)
                .filter(relativeB -> !blocks.contains(relativeB))
                .filter(relativeB -> Settings.isAcceptedWoodBlock(relativeB.getType()))
                .takeWhile(relativeB -> blocks.size() < Settings.getMaxBlocks())
                .takeWhile(relativeB -> blocks.size() < maxBlocks)
                .forEach(relativeB -> {
                    blocks.add(relativeB);
                    getWoodBlocksAround(blocks, relativeB, maxBlocks);
                    getDiagonalBlocks(blocks, relativeB, maxBlocks);
        });
        return blocks;
    }

    private static void getDiagonalBlocks(List<Block> blocks, Block b, int maxBlocks) {

        for (int i = -1; i < 2; i++) {
            checkBlock(blocks, b.getRelative(1, i, -1), maxBlocks);
            checkBlock(blocks, b.getRelative(1, i, 1), maxBlocks);
            checkBlock(blocks, b.getRelative(-1, i, -1), maxBlocks);
            checkBlock(blocks, b.getRelative(-1, i, 1), maxBlocks);

            checkBlock(blocks, b.getRelative(-1, i, 0), maxBlocks);
            checkBlock(blocks, b.getRelative(1, i, 0), maxBlocks);
            checkBlock(blocks, b.getRelative(0, i, -1), maxBlocks);
            checkBlock(blocks, b.getRelative(0, i, 1), maxBlocks);

            if(blocks.size() >= Settings.getMaxBlocks()){
                return;
            }
        }
    }

    private static void checkBlock(List<Block> blocks, Block b, int maxBlocks){
        if(blocks.contains(b)){
            return;
        }

        if(!Settings.isAcceptedWoodBlock(b.getType())){
            return;
        }

        if(blocks.size() >= Settings.getMaxBlocks()){
            return;
        }

        getWoodBlocksAround(blocks, b, maxBlocks);
    }
}
