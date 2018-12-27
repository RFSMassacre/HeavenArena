package us.rfsmassacre.heavenarena.events.arena;

import us.rfsmassacre.heavenarena.arenas.Arena;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaTeam;

public class ArenaTieEvent extends ArenaCompleteEvent
{
    public ArenaTieEvent(Arena arena, ArenaTeam team, boolean forfeit)
    {
        super(arena, team, forfeit);
    }
}
