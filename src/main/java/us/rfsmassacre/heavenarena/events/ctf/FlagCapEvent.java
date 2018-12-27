package us.rfsmassacre.heavenarena.events.ctf;

import org.bukkit.entity.Player;
import us.rfsmassacre.heavenarena.arenas.CTFArena;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaFlag;

public class FlagCapEvent extends FlagEvent
{
    public FlagCapEvent(CTFArena arena, ArenaFlag flag, Player carrier)
    {
        super(arena, flag, carrier);
    }
}
