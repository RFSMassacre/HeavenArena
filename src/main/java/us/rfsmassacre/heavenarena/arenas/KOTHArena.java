package us.rfsmassacre.heavenarena.arenas;

import com.faris.Cuboid;
import org.bukkit.Location;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaPoint;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaType;

public class KOTHArena extends Arena
{
    private ArenaPoint point;

    public KOTHArena(String name, Cuboid region, Location exit)
    {
        super(name, ArenaType.KING_OF_THE_HILL, region, exit);
    }
    public KOTHArena(Arena arena)
    {
        super(arena);
    }

    public void setPoint(ArenaPoint point)
    {
        this.point = point;
    }
    public ArenaPoint getPoint()
    {
        return point;
    }
}
