package us.rfsmassacre.heavenarena.events.ctf;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import us.rfsmassacre.heavenarena.arenas.CTFArena;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaFlag;

public abstract class FlagEvent extends Event implements Cancellable
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

    private CTFArena arena;
    private ArenaFlag flag;
    private Player carrier;

    public FlagEvent(CTFArena arena, ArenaFlag flag, Player carrier)
    {
        this.arena = arena;
        this.flag = flag;
        this.carrier = carrier;
    }

    public CTFArena getArena()
    {
        return arena;
    }
    public ArenaFlag getFlag()
    {
        return flag;
    }
    public Player getCarrier()
    {
        return carrier;
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
