package us.rfsmassacre.heavenarena.events.koth;

import org.bukkit.entity.Player;
import us.rfsmassacre.heavenarena.arenas.KOTHArena;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaTeam;

public class PointCaptureEvent extends PointContestEvent
{
    public PointCaptureEvent(KOTHArena arena, Player player, ArenaTeam team)
    {
        super(arena, player, team);
    }
}
