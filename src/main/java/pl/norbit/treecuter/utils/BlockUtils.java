package pl.norbit.treecuter.utils;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import pl.norbit.treecuter.config.Settings;
import pl.norbit.treecuter.config.model.CutShape;

import java.util.*;

public class BlockUtils {

    private static final BlockFace[] FACES = BlockFace.values();

    private BlockUtils() {}

    public static List<Block> getWoodBlocksAround(List<Block> blocks, Block start, int maxBlocks, CutShape woodBlocks) {
        int max = Math.min(Settings.getMaxBlocks(), maxBlocks);

        Set<Block> visited = new HashSet<>(max * 2);
        Deque<Block> queue = new ArrayDeque<>();

        blocks.add(start);
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty() && blocks.size() < max) {

            Block current = queue.poll();

            for (BlockFace face : FACES) {

                Block relative = current.getRelative(face);

                if (!visited.add(relative)) {
                    continue;
                }

                if (!woodBlocks.isAcceptBlock(relative.getType())) {
                    continue;
                }

                blocks.add(relative);
                queue.add(relative);

                if (blocks.size() >= max) {
                    return blocks;
                }

                getDiagonalBlocks(blocks, queue, visited, relative, max, woodBlocks);
            }
        }
        return blocks;
    }

    private static void getDiagonalBlocks(List<Block> blocks,
                                          Deque<Block> queue,
                                          Set<Block> visited,
                                          Block b,
                                          int maxBlocks,
                                          CutShape woodBlocks) {

        for (int y = -1; y < 2; y++) {
            checkBlock(blocks, queue, visited, b.getRelative(1, y, -1), maxBlocks, woodBlocks);
            checkBlock(blocks, queue, visited, b.getRelative(1, y, 1), maxBlocks, woodBlocks);
            checkBlock(blocks, queue, visited, b.getRelative(-1, y, -1), maxBlocks, woodBlocks);
            checkBlock(blocks, queue, visited, b.getRelative(-1, y, 1), maxBlocks, woodBlocks);

            checkBlock(blocks, queue, visited, b.getRelative(-1, y, 0), maxBlocks, woodBlocks);
            checkBlock(blocks, queue, visited, b.getRelative(1, y, 0), maxBlocks, woodBlocks);
            checkBlock(blocks, queue, visited, b.getRelative(0, y, -1), maxBlocks, woodBlocks);
            checkBlock(blocks, queue, visited, b.getRelative(0, y, 1), maxBlocks, woodBlocks);

            if (blocks.size() >= maxBlocks) {
                return;
            }
        }
    }

    private static void checkBlock(List<Block> blocks,
                                   Deque<Block> queue,
                                   Set<Block> visited,
                                   Block b,
                                   int maxBlocks,
                                   CutShape woodBlocks) {

        if (!visited.add(b)) {
            return;
        }

        if (!woodBlocks.isAcceptBlock(b.getType())) {
            return;
        }

        if (blocks.size() >= maxBlocks) {
            return;
        }

        blocks.add(b);
        queue.add(b);
    }
}