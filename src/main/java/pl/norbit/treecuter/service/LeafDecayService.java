package pl.norbit.treecuter.service;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Leaves;
import pl.norbit.treecuter.config.Settings;
import pl.norbit.treecuter.config.model.CutShape;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

import static pl.norbit.treecuter.utils.TaskUtils.*;

public class LeafDecayService {

    private static final List<Queue<Block>> blocksQueue = new CopyOnWriteArrayList<>();
    private LeafDecayService() {
        throw new IllegalStateException("This class cannot be instantiated");
    }

    public static void start() {
        timer(() -> {
            List<Queue<Block>> toRemove = new ArrayList<>();
            int decayAmount = Settings.getDecayAmount();

            if(decayAmount < 1){
                decayAmount = 1;
            }

            for (Queue<Block> queue : blocksQueue) {
                for (int i = 0; i < decayAmount; i++) {
                    if (queue.isEmpty()) {
                        toRemove.add(queue);
                        break;
                    }
                    breakBlock(queue.poll());
                }
            }
            blocksQueue.removeAll(toRemove);
        }, 5L);
    }
    private static void breakBlock(Block b){
        if(b == null) {
            return;
        }

        if (b.getType() != Material.AIR) {
            //log block break to CoreProtect
            CoreProtectService.logBreak("TreeCutter-decay", b.getState());
            b.breakNaturally();
        }
    }

    public static void scanLeaves(List<Block> blocks, CutShape cutShape) {
        if(!Settings.isLeavesEnabled()){
            return;
        }

        async(() -> {
            Set<Block> leaves = new HashSet<>();
            List<Material> acceptBlocks = Settings.getAcceptLeavesBlocks();
            acceptBlocks.addAll(Settings.getAcceptCustomLeavesBlocks());

            blocks.forEach(b -> leaves.addAll(getBlocks(b, acceptBlocks, 5)));

            List<Block> decayLeaves = new ArrayList<>(leaves.stream()
                    .filter(bl -> isLeafDecaying(bl, cutShape))
                    .toList());

            // Shuffle the leaves to break them in random order
            Collections.shuffle(decayLeaves);

            blocksQueue.add(new LinkedList<>(decayLeaves));
        });
    }

    private static boolean isLeafDecaying(Block block, CutShape cutShape) {
        int range = 2;

        if (block.getBlockData() instanceof Leaves leaves) {
            if (leaves.isPersistent()) {
                return false;
            }
            List<Block> blocks = getBlocks(block, cutShape.getAcceptBlocks(), range);

            return blocks.isEmpty();
        } else if (Settings.isAcceptedCustomLeavesBlock(block.getType())) {
            List<Block> blocks = getBlocks(block, cutShape.getAcceptBlocks(), range);

            return blocks.isEmpty();
        }

        return false;
    }

    private static List<Block> getBlocks(Block block, List<Material> mat, int range) {
        return getBlocks(block, mat, range, range);
    }

    private static List<Block> getBlocks(Block block, List<Material> mat, int range, int yRange) {
        return IntStream.rangeClosed(-range, range)
                .boxed()
                .flatMap(dx -> IntStream.rangeClosed(-range, range)
                        .boxed()
                        .flatMap(dy -> IntStream.rangeClosed(-yRange, yRange)
                                .mapToObj(dz -> block.getRelative(dx, dy, dz))))
                .filter(bl -> mat.contains(bl.getType()))
                .toList();
    }
}
