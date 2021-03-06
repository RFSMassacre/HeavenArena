package us.rfsmassacre.heavenarena.events.queue;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class QueueEvent extends Event implements Cancellable
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

    private Player player;
    private boolean cancel;

    public QueueEvent(Player player)
    {
        this.player = player;
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

    public Player getPlayer()
    {
        return player;
    }
}
