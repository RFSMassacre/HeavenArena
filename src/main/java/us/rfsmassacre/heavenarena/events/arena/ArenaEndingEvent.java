package us.rfsmassacre.heavenarena.events.arena;

import us.rfsmassacre.heavenarena.arenas.Arena;

public class ArenaEndingEvent extends ArenaEvent
{
    private boolean forfeit;

    public ArenaEndingEvent(Arena arena, boolean forfeit)
    {
        super(arena);

        this.forfeit = forfeit;
    }

    public boolean isForfeit()
    {
        return forfeit;
    }
}
