package pl.norbit.treecuter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.block.Block;
import pl.norbit.treecuter.config.model.CutShape;

import java.util.List;

@Data
@AllArgsConstructor
public class SelectedBreak {
    private final Block mainBlock;
    private final List<Block> selectedBlocks;
    private final CutShape cutShape;
}
