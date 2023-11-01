package pl.norbit.treecuter.api.listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class TreeCutEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final Set<Block> blocks;
    private final Player p;
    public TreeCutEvent(Set<Block> blocks, Player p) {
        this.blocks = blocks;
        this.p = p;
    }

    /**
     * Get set of wood blocks.
     *
     * @return set of wood blocks
     */
    public Set<Block> getBlocks() {
        return blocks;
    }

    public Player getPlayer() {
        return p;
    }

    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
