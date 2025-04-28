package pl.norbit.treecuter.utils;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import pl.norbit.treecuter.config.Settings;
import pl.norbit.treecuter.config.model.CutShape;

import java.util.Arrays;
import java.util.List;

public class BlockUtils {

    private BlockUtils() {
        throw new IllegalStateException("This class cannot be instantiated");
    }

    public static List<Block> getWoodBlocksAround(List<Block> blocks, Block b, int maxBlocks, CutShape woodBlocks) {
        Arrays.stream(BlockFace.values())
                .map(b::getRelative)
                .filter(relativeB -> !blocks.contains(relativeB))
                .filter(relativeB -> woodBlocks.isAcceptBlock(relativeB.getType()))
                .takeWhile(relativeB -> blocks.size() < Settings.getMaxBlocks())
                .takeWhile(relativeB -> blocks.size() < maxBlocks)
                .forEach(relativeB -> {
                    blocks.add(relativeB);
                    getWoodBlocksAround(blocks, relativeB, maxBlocks, woodBlocks);
                    getDiagonalBlocks(blocks, relativeB, maxBlocks, woodBlocks);
        });
        return blocks;
    }

    private static void getDiagonalBlocks(List<Block> blocks, Block b, int maxBlocks, CutShape woodBlocks) {

        for (int i = -1; i < 2; i++) {
            checkBlock(blocks, b.getRelative(1, i, -1), maxBlocks, woodBlocks);
            checkBlock(blocks, b.getRelative(1, i, 1), maxBlocks, woodBlocks);
            checkBlock(blocks, b.getRelative(-1, i, -1), maxBlocks, woodBlocks);
            checkBlock(blocks, b.getRelative(-1, i, 1), maxBlocks, woodBlocks);

            checkBlock(blocks, b.getRelative(-1, i, 0), maxBlocks, woodBlocks);
            checkBlock(blocks, b.getRelative(1, i, 0), maxBlocks, woodBlocks);
            checkBlock(blocks, b.getRelative(0, i, -1), maxBlocks, woodBlocks);
            checkBlock(blocks, b.getRelative(0, i, 1), maxBlocks, woodBlocks);

            if(blocks.size() >= Settings.getMaxBlocks()){
                return;
            }
        }
    }

    private static void checkBlock(List<Block> blocks, Block b, int maxBlocks, CutShape woodBlocks) {
        if(blocks.contains(b)){
            return;
        }

        if(!woodBlocks.isAcceptBlock(b.getType())){
            return;
        }

        if(blocks.size() >= Settings.getMaxBlocks()){
            return;
        }

        getWoodBlocksAround(blocks, b, maxBlocks, woodBlocks);
    }
}
