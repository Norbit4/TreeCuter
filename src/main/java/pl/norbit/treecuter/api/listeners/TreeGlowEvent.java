package pl.norbit.treecuter.api.listeners;

import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TreeGlowEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    /**
     * -- GETTER --
     *  Get list of wood blocks.
     */
    @Getter
    private final List<Block> blocks;
    @Getter
    private final Player player;

    private boolean cancelled;
    public TreeGlowEvent (List<Block> blocks, Player p) {
        this.blocks = blocks;
        this.player = p;
    }

    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
