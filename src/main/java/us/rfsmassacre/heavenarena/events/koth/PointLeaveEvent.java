package us.rfsmassacre.heavenarena.events.koth;

import org.bukkit.entity.Player;
import us.rfsmassacre.heavenarena.arenas.KOTHArena;

public class PointLeaveEvent extends PointEvent
{
    public PointLeaveEvent(KOTHArena arena, Player player)
    {
        super(arena, player);
    }
}
