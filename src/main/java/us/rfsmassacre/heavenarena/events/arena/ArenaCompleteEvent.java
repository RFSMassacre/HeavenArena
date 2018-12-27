package us.rfsmassacre.heavenarena.events.arena;

import us.rfsmassacre.heavenarena.arenas.Arena;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaTeam;

public abstract class ArenaCompleteEvent extends ArenaEvent
{
    private ArenaTeam team;
    private boolean forfeit;

    public ArenaCompleteEvent(Arena arena, ArenaTeam team, boolean forfeit)
    {
        super(arena);

        this.team = team;
        this.forfeit = forfeit;
    }

    public Arena getArena()
    {
        return arena;
    }
    public ArenaTeam getTeam()
    {
        return team;
    }
    public boolean isForfeit()
    {
        return forfeit;
    }
}
