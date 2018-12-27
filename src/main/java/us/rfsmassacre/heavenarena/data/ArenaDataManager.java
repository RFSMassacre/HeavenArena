package us.rfsmassacre.heavenarena.data;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import us.rfsmassacre.heavenarena.arenas.Arena;
import com.faris.Cuboid;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaTeam;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaType;
import us.rfsmassacre.heavenlib.managers.DataManager;

import java.io.IOException;

public class ArenaDataManager extends DataManager
{
    public ArenaDataManager(JavaPlugin instance)
    {
        super(instance, "arenas");
    }

    @Override
    protected void storeData(Object object, YamlConfiguration data) throws IOException
    {
        if (!(object instanceof Arena))
        {
            return;
        }

        Arena arena = (Arena)object;
        storeArenaData(arena, data);
    }

    @Override
    protected Object loadData(YamlConfiguration data) throws IOException
    {
        return loadArenaData(data);
    }

    /*
     * All arenas have the same base datas to store. Run this when storing data.
     */
    protected void storeArenaData(Arena arena, YamlConfiguration data)
    {
        //Arena data
        String name = arena.getName();
        String type = arena.getType().toString();
        data.set("name", name);
        data.set("type", type);

        if (arena.getRegion() != null)
        {
            String world = arena.getRegion().getWorld().getName();
            Location p1 = arena.getRegion().getLowerNE();
            Location p2 = arena.getRegion().getUpperSW();
            data.set("region.world", world);
            data.set("region.position.1.x", p1.getBlockX());
            data.set("region.position.1.y", p1.getBlockY());
            data.set("region.position.1.z", p1.getBlockZ());
            data.set("region.position.2.x", p2.getBlockX());
            data.set("region.position.2.y", p2.getBlockY());
            data.set("region.position.2.z", p2.getBlockZ());
        }

        if (arena.getExit() != null)
        {
            Location exit = arena.getExit();
            String world = arena.getExit().getWorld().getName();
            data.set("exit.world", world);
            data.set("exit.x", exit.getBlockX());
            data.set("exit.y", exit.getBlockY());
            data.set("exit.z", exit.getBlockZ());
        }

        //Team data
        for (ArenaTeam team : arena.getTeams())
        {
            String teamName = team.getName();
            String teamAlt = team.getAltColor().name();
            int maxMembers = team.getMaxMembers();
            int minMembers = team.getMinMembers();
            data.set("teams." + teamName + ".name", teamName);
            data.set("teams." + teamName + ".alt", teamAlt);
            data.set("teams." + teamName + ".members.max", maxMembers);
            data.set("teams." + teamName + ".members.min", minMembers);

            if (team.getLobby() != null)
            {
                String lobbyWorld = team.getLobby().getWorld().getName();
                Location lobbyP1 = team.getLobby().getLowerNE();
                Location lobbyP2 = team.getLobby().getUpperSW();
                data.set("teams." + teamName + ".lobby.world", lobbyWorld);
                data.set("teams." + teamName + ".lobby.position.1.x", lobbyP1.getBlockX());
                data.set("teams." + teamName + ".lobby.position.1.y", lobbyP1.getBlockY());
                data.set("teams." + teamName + ".lobby.position.1.z", lobbyP1.getBlockZ());
                data.set("teams." + teamName + ".lobby.position.2.x", lobbyP2.getBlockX());
                data.set("teams." + teamName + ".lobby.position.2.y", lobbyP2.getBlockY());
                data.set("teams." + teamName + ".lobby.position.2.z", lobbyP2.getBlockZ());
            }

            if (team.getSpawn() != null)
            {
                Location spawn = team.getSpawn();
                data.set("teams." + teamName + ".spawn.world", spawn.getWorld().getName());
                data.set("teams." + teamName + ".spawn.x", spawn.getBlockX());
                data.set("teams." + teamName + ".spawn.y", spawn.getBlockY());
                data.set("teams." + teamName + ".spawn.z", spawn.getBlockZ());
            }
        }
    }

    /*
     * All arenas have the same basic data when storing them into file
     */
    protected Arena loadArenaData(YamlConfiguration data)
    {
        String name = data.getString("name");
        String typeName = data.getString("type");
        String worldName = data.getString("region.world");
        String exitWorldName = data.getString("exit.world");
        int p1x = data.getInt("region.position.1.x");
        int p1y = data.getInt("region.position.1.y");
        int p1z = data.getInt("region.position.1.z");
        int p2x = data.getInt("region.position.2.x");
        int p2y = data.getInt("region.position.2.y");
        int p2z = data.getInt("region.position.2.z");
        int eX = data.getInt("exit.x");
        int eY = data.getInt("exit.y");
        int eZ = data.getInt("exit.z");

        ArenaType type = ArenaType.fromString(typeName);
        Arena arena = new Arena(name, type, null, null);

        if (worldName != null)
        {
            World world = Bukkit.getWorld(worldName);
            Location p1 = new Location(world, p1x, p1y, p1z);
            Location p2 = new Location(world, p2x, p2y, p2z);
            Cuboid region = new Cuboid(p1, p2);
            arena.setRegion(region);
        }

        if (exitWorldName != null)
        {
            World world = Bukkit.getWorld(exitWorldName);
            Location exit = new Location(world, eX, eY, eZ);
            arena.setExit(exit);
        }

        ConfigurationSection section = data.getConfigurationSection("teams");
        if (section != null)
        {
            for (String teamKey : section.getKeys(false))
            {
                String teamName = data.getString("teams." + teamKey + ".name");
                String teamAlt = data.getString("teams." + teamKey + ".alt");
                String lobbyWorldName = data.getString("teams." + teamKey + ".lobby.world");
                String spawnWorldName = data.getString("teams." + teamKey + ".spawn.world");
                int lobbyP1x = data.getInt("teams." + teamKey + ".lobby.position.1.x");
                int lobbyP1y = data.getInt("teams." + teamKey + ".lobby.position.1.y");
                int lobbyP1z = data.getInt("teams." + teamKey + ".lobby.position.1.z");
                int lobbyP2x = data.getInt("teams." + teamKey + ".lobby.position.2.x");
                int lobbyP2y = data.getInt("teams." + teamKey + ".lobby.position.2.y");
                int lobbyP2z = data.getInt("teams." + teamKey + ".lobby.position.2.z");
                int spawnX = data.getInt("teams." + teamKey + ".spawn.x");
                int spawnY = data.getInt("teams." + teamKey + ".spawn.y");
                int spawnZ = data.getInt("teams." + teamKey + ".spawn.z");
                int maxMembers = data.getInt("teams." + teamKey + ".members.max");
                int minMembers = data.getInt("teams." + teamKey + ".members.min");

                ArenaTeam team = new ArenaTeam(ChatColor.valueOf(teamName), ChatColor.valueOf(teamAlt), null, null);
                team.setMaxMembers(maxMembers);
                team.setMinMembers(minMembers);

                if (lobbyWorldName != null)
                {
                    World lobbyWorld = Bukkit.getWorld(lobbyWorldName);
                    Location lobbyP1 = new Location(lobbyWorld, lobbyP1x, lobbyP1y, lobbyP1z);
                    Location lobbyP2 = new Location(lobbyWorld, lobbyP2x, lobbyP2y, lobbyP2z);
                    Cuboid lobby = new Cuboid(lobbyP1, lobbyP2);

                    team.setLobby(lobby);
                }
                if (spawnWorldName != null)
                {
                    World spawnWorld = Bukkit.getWorld(spawnWorldName);
                    Location spawn = new Location(spawnWorld, spawnX, spawnY, spawnZ);

                    team.setSpawn(spawn);
                }

                arena.addTeam(team);
            }
        }

        return arena;
    }
}
