package us.rfsmassacre.heavenarena.events.arena;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import us.rfsmassacre.heavenarena.arenas.Arena;

public abstract class ArenaEvent extends Event implements Cancellable
{
    //Handler List
    private static final HandlerList HANDLERS = new HandlerList();

    @Override
    public HandlerList getHandlers()
    {
        return HANDLERS;
    }
    public static HandlerList getHandlerList()
    {
        return HANDLERS;
    }

    protected Arena arena;

    private boolean cancel;

    public ArenaEvent(Arena arena)
    {
        this.arena = arena;
    }

    public Arena getArena()
    {
        return arena;
    }

    @Override
    public boolean isCancelled()
    {
        return cancel;
    }
    @Override
    public void setCancelled(boolean cancel)
    {
        this.cancel = cancel;
    }
}
