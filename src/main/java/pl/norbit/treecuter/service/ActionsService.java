package pl.norbit.treecuter.service;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import pl.norbit.treecuter.config.Settings;
import pl.norbit.treecuter.config.model.CutShape;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ActionsService {
    private record MaterialCount(Material material, long count) { }

    private ActionsService() {
        throw new IllegalStateException("Utility class");
    }

    public static void triggerActions(Player p, CutShape shape, List<Block> blocks){
        if(!Settings.isActionsEnabled()){
            return;
        }

        String playerName = p.getName();
        Server server = p.getServer();
        ConsoleCommandSender consoleSender = server.getConsoleSender();
        String id = shape.getId();

        //global actions
        Settings.getActions().forEach(action ->{
            String command = action
                    .replace("{player}", playerName)
                    .replace("{shape}", id)
                    .replace("{count}", String.valueOf(blocks.size()));

            server.dispatchCommand(consoleSender, command);
        });

        //material specific actions
        countMaterials(blocks).forEach(mCount -> Settings.getActions(mCount.material)
                .forEach(action -> {
            String command = action
                    .replace("{player}", playerName)
                    .replace("{shape}", id)
                    .replace("{count}", String.valueOf(mCount.count()));

            server.dispatchCommand(consoleSender, command);
        }));
    }

    private static List<MaterialCount> countMaterials(List<Block> blocks) {
        Map<Material, Long> counts = blocks.stream()
                .collect(Collectors.groupingBy(
                        Block::getType,
                        Collectors.counting()
                ));

        return counts.entrySet().stream()
                .map(e -> new MaterialCount(e.getKey(), e.getValue()))
                .toList();
    }
}
