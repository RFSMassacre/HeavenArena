package us.rfsmassacre.heavenarena.events.arena;

import us.rfsmassacre.heavenarena.arenas.Arena;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaTeam;

public class ArenaLoseEvent extends ArenaCompleteEvent
{
    public ArenaLoseEvent(Arena arena, ArenaTeam team, boolean forfeit)
    {
        super(arena, team, forfeit);
    }
}
