package us.rfsmassacre.heavenarena.events.koth;

import org.bukkit.entity.Player;
import us.rfsmassacre.heavenarena.arenas.KOTHArena;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaTeam;

public abstract class PointContestEvent extends PointEvent
{
    private ArenaTeam team;

    public PointContestEvent(KOTHArena arena, Player player, ArenaTeam team)
    {
        super(arena, player);

        this.team = team;
    }

    public ArenaTeam getTeam()
    {
        return team;
    }
}
