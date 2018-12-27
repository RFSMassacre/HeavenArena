package us.rfsmassacre.heavenarena.events.koth;

import org.bukkit.entity.Player;
import us.rfsmassacre.heavenarena.arenas.KOTHArena;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaPoint;

public class PointEnterEvent extends PointEvent
{
    public PointEnterEvent(KOTHArena arena, Player player)
    {
        super(arena, player);
    }
}
