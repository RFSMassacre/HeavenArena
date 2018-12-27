package us.rfsmassacre.heavenarena.events.koth;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import us.rfsmassacre.heavenarena.arenas.KOTHArena;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaPoint;

public abstract class PointEvent extends Event implements Cancellable
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

    private KOTHArena arena;
    private ArenaPoint point;
    private Player player;

    public PointEvent(KOTHArena arena, Player player)
    {
        this.arena = arena;
        this.point = arena.getPoint();
        this.player = player;
    }

    public KOTHArena getArena()
    {
        return arena;
    }
    public ArenaPoint getPoint()
    {
        return point;
    }
    public Player getPlayer()
    {
        return player;
    }

    private boolean cancel;

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
