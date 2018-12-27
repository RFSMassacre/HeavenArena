package us.rfsmassacre.heavenarena.arenas;

import com.faris.Cuboid;
import org.bukkit.Location;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaType;

public class TDMArena extends Arena
{
    public TDMArena(String name, Cuboid region, Location exit)
    {
        super(name, ArenaType.TEAM_DEATHMATCH, region, exit);
    }
    public TDMArena(Arena arena)
    {
        super (arena);
    }
}
