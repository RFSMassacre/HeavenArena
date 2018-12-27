package us.rfsmassacre.heavenarena.data;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaFlag;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaTeam;
import us.rfsmassacre.heavenarena.arenas.CTFArena;

import java.io.IOException;

public class CTFDataManager extends ArenaDataManager
{
    public CTFDataManager(JavaPlugin instance)
    {
        super(instance);
    }

    @Override
    protected void storeData(Object object, YamlConfiguration data) throws IOException
    {
        if (!(object instanceof CTFArena))
        {
            return;
        }

        CTFArena arena = (CTFArena)object;
        storeArenaData(arena, data);

        //Store CTF data
        for (ArenaTeam team : arena.getTeams())
        {
            String teamName = team.getName();
            ArenaFlag flag = arena.getFlag(team.getColor());

            if (flag != null)
            {
                Location current = flag.getCurrentLocation();
                Location flagHome = flag.getHomeLocation();

                if (current != null)
                {
                    data.set("teams." + teamName + ".flag.current.world", current.getWorld().getName());
                    data.set("teams." + teamName + ".flag.current.x", current.getBlockX());
                    data.set("teams." + teamName + ".flag.current.y", current.getBlockY());
                    data.set("teams." + teamName + ".flag.current.z", current.getBlockZ());
                }

                if (flagHome != null)
                {
                    data.set("teams." + teamName + ".flag.home.world", flagHome.getWorld().getName());
                    data.set("teams." + teamName + ".flag.home.x", flagHome.getBlockX());
                    data.set("teams." + teamName + ".flag.home.y", flagHome.getBlockY());
                    data.set("teams." + teamName + ".flag.home.z", flagHome.getBlockZ());
                }
            }
        }
    }

    @Override
    protected Object loadData(YamlConfiguration data) throws IOException
    {
        CTFArena arena = new CTFArena(loadArenaData(data));

        //Load CTF data
        ConfigurationSection section = data.getConfigurationSection("teams");
        if (section != null)
        {
            for (String teamKey : section.getKeys(false))
            {
                ChatColor color = ChatColor.valueOf(teamKey);
                String homeWorldName = data.getString("teams." + teamKey + ".flag.home.world");
                String currentWorldName = data.getString("teams." + teamKey + ".flag.current.world");
                int homeX = data.getInt("teams." + teamKey + ".flag.home.x");
                int homeY = data.getInt("teams." + teamKey + ".flag.home.y");
                int homeZ = data.getInt("teams." + teamKey + ".flag.home.z");
                int currentX = data.getInt("teams." + teamKey + ".flag.current.x");
                int currentY = data.getInt("teams." + teamKey + ".flag.current.y");
                int currentZ = data.getInt("teams." + teamKey + ".flag.current.z");

                ArenaFlag flag = new ArenaFlag(color, null);

                if (homeWorldName != null)
                {
                    World world = Bukkit.getWorld(homeWorldName);
                    Location home = new Location(world, homeX, homeY, homeZ);
                    flag.setHomeLocation(home);
                }

                if (currentWorldName != null)
                {
                    World world = Bukkit.getWorld(currentWorldName);
                    Location current = new Location(world, currentX, currentY, currentZ);
                    flag.setCurrentLocation(current);
                }

                arena.setFlag(color, flag);
            }
        }

        return arena;
    }
}
