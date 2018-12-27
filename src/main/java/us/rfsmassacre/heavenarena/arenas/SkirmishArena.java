package us.rfsmassacre.heavenarena.arenas;

import com.faris.Cuboid;
import org.bukkit.Location;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaType;

public class SkirmishArena extends Arena
{
    public SkirmishArena(String name, Cuboid region, Location exit)
    {
        super(name, ArenaType.SKIRMISH, region, exit);
    }
    public SkirmishArena(Arena arena)
    {
        super(arena);
    }
}
