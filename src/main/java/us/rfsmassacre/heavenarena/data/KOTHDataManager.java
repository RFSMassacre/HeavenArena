package us.rfsmassacre.heavenarena.data;

import com.faris.Cuboid;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import us.rfsmassacre.heavenarena.arenas.KOTHArena;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaPoint;

import java.io.IOException;

public class KOTHDataManager extends ArenaDataManager
{
    public KOTHDataManager(JavaPlugin instance)
    {
        super(instance);
    }

    @Override
    protected void storeData(Object object, YamlConfiguration data) throws IOException
    {
        if (!(object instanceof KOTHArena))
        {
            return;
        }

        KOTHArena arena = (KOTHArena)object;
        storeArenaData(arena, data);

        //Store KOTH data
        ArenaPoint point = arena.getPoint();
        if (point != null)
        {
            int maxMeter = point.getMaxMeter();
            data.set("point.max-meter", maxMeter);

            Cuboid region = point.getRegion();
            if (region != null)
            {
                String world = region.getWorld().getName();
                Location p1 = region.getLowerNE();
                Location p2 = region.getUpperSW();
                data.set("point.world", world);
                data.set("point.position.1.x", p1.getBlockX());
                data.set("point.position.1.y", p1.getBlockY());
                data.set("point.position.1.z", p1.getBlockZ());
                data.set("point.position.2.x", p2.getBlockX());
                data.set("point.position.2.y", p2.getBlockY());
                data.set("point.position.2.z", p2.getBlockZ());
            }
        }

    }

    @Override
    protected Object loadData(YamlConfiguration data) throws IOException
    {
        KOTHArena arena = new KOTHArena(loadArenaData(data));

        //Load KOTH data
        int maxMeter = data.getInt("point.max-meter");
        String worldName = data.getString("point.world");
        int p1x = data.getInt("point.position.1.x");
        int p1y = data.getInt("point.position.1.y");
        int p1z = data.getInt("point.position.1.z");
        int p2x = data.getInt("point.position.2.x");
        int p2y = data.getInt("point.position.2.y");
        int p2z = data.getInt("point.position.2.z");

        if (worldName != null)
        {
            World world = Bukkit.getWorld(worldName);
            Location p1 = new Location(world, p1x, p1y, p1z);
            Location p2 = new Location(world, p2x, p2y, p2z);
            Cuboid point = new Cuboid(p1, p2);
            arena.setPoint(new ArenaPoint(point, maxMeter));
        }

        return arena;
    }
}
