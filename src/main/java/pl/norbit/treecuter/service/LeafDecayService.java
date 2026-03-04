package pl.norbit.treecuter.service;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Leaves;
import pl.norbit.treecuter.config.Settings;
import pl.norbit.treecuter.config.model.CutShape;

import java.util.*;

import static pl.norbit.treecuter.utils.TaskUtils.*;

public class LeafDecayService {

    private static final List<Queue<Block>> blocksQueue = new ArrayList<>();

    //precomputed offsets for scanning cubes
    private static final int[][] OFFSETS_RANGE5 = generateOffsets(5,5);
    private static final int[][] OFFSETS_RANGE2 = generateOffsets(2,2);

    private LeafDecayService() {}

    public static void start() {
        timer(() -> {
            int decayAmount = Math.max(1, Settings.getDecayAmount());

            Iterator<Queue<Block>> iterator = blocksQueue.iterator();

            while (iterator.hasNext()) {

                Queue<Block> queue = iterator.next();

                for (int i = 0; i < decayAmount; i++) {

                    Block block = queue.poll();

                    if (block == null) {
                        iterator.remove();
                        break;
                    }
                    breakBlock(block);
                }
            }
        }, 5L);
    }

    private static void breakBlock(Block b){
        if(b == null){
            return;
        }

        if (b.getType() != Material.AIR) {
            CoreProtectService.logBreak("TreeCutter-decay", b.getState());
            b.breakNaturally();
        }
    }

    public static void scanLeaves(List<Block> blocks, CutShape cutShape) {
        if (!Settings.isLeavesEnabled()) {
            return;
        }

        async(() -> {
            Set<Block> leaves = new HashSet<>();

            Set<Material> acceptBlocks = new HashSet<>(Settings.getAcceptLeavesBlocks());
            acceptBlocks.addAll(Settings.getAcceptCustomLeavesBlocks());

            for (Block block : blocks) {
                collectBlocks(block, acceptBlocks, OFFSETS_RANGE5, leaves);
            }

            List<Block> decayLeaves = new ArrayList<>();

            Set<Material> woodBlocks = new HashSet<>(cutShape.getAcceptBlocks());

            for (Block leaf : leaves) {
                if (isLeafDecaying(leaf, woodBlocks)) {
                    decayLeaves.add(leaf);
                }
            }

            Collections.shuffle(decayLeaves);

            blocksQueue.add(new ArrayDeque<>(decayLeaves));
        });
    }

    private static boolean isLeafDecaying(Block block, Set<Material> woodBlocks) {
        if (block.getBlockData() instanceof Leaves leaves) {

            if (leaves.isPersistent()) {
                return false;
            }

            return !hasNearby(block, woodBlocks, OFFSETS_RANGE2);

        } else if (Settings.isAcceptedCustomLeavesBlock(block.getType())) {

            return !hasNearby(block, woodBlocks, OFFSETS_RANGE2);
        }

        return false;
    }

    private static boolean hasNearby(Block block, Set<Material> materials, int[][] offsets){
        for (int[] off : offsets) {

            Block relative = block.getRelative(off[0], off[1], off[2]);

            if (materials.contains(relative.getType())) {
                return true;
            }
        }

        return false;
    }

    private static void collectBlocks(Block block,
                                      Set<Material> materials,
                                      int[][] offsets,
                                      Set<Block> output){

        for (int[] off : offsets) {

            Block relative = block.getRelative(off[0], off[1], off[2]);

            if (materials.contains(relative.getType())) {
                output.add(relative);
            }
        }
    }

    private static int[][] generateOffsets(int range, int yRange){
        List<int[]> offsets = new ArrayList<>();

        for (int dx = -range; dx <= range; dx++) {
            for (int dy = -range; dy <= range; dy++) {
                for (int dz = -yRange; dz <= yRange; dz++) {
                    offsets.add(new int[]{dx,dy,dz});
                }
            }
        }

        return offsets.toArray(new int[0][]);
    }
}