package us.rfsmassacre.heavenarena.events.arena;

import us.rfsmassacre.heavenarena.arenas.Arena;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaTeam;

public class ArenaWinEvent extends ArenaCompleteEvent
{
    public ArenaWinEvent(Arena arena, ArenaTeam team, boolean forfeit)
    {
        super(arena, team, forfeit);
    }
}
