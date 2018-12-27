package us.rfsmassacre.heavenarena.arenas;

import com.faris.Cuboid;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaPhase;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaTeam;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaType;

import java.util.*;

public class Arena
{
    protected String name;
    protected ArenaType type;
    protected HashMap<ChatColor, ArenaTeam> teams;
    protected Cuboid region;
    protected ArenaPhase phase;
    protected Location exit;

    public Arena(String name, ArenaType type, Cuboid region, Location exit)
    {
        this.name = name;
        this.type = type;
        this.teams = new HashMap<ChatColor, ArenaTeam>();
        this.region = region;
        this.phase = ArenaPhase.CLOSED;
        this.exit = exit;
    }
    public Arena(Arena arena)
    {
        this.name = arena.name;
        this.type = arena.type;
        this.teams = arena.teams;
        this.region = arena.region;
        this.phase = ArenaPhase.CLOSED;
        this.exit = arena.exit;
    }

    /*
     * Management
     */
    public void setName(String name)
    {
        this.name = name;
    }
    public String getName()
    {
        return name;
    }

    public ArenaType getType()
    {
        return type;
    }

    public void addTeam(ArenaTeam team)
    {
        teams.put(team.getColor(), team);
    }
    public void removeTeam(ChatColor color)
    {
        teams.remove(color);
    }
    public boolean isTeam(ArenaTeam team)
    {
        return team.equals(teams.get(team.getColor()));
    }
    public ArenaTeam getTeam(ChatColor color)
    {
        return teams.get(color);
    }
    public Collection<ArenaTeam> getTeams()
    {
        return teams.values();
    }

    public void setRegion(Cuboid cuboid)
    {
        this.region = cuboid;
    }
    public Cuboid getRegion()
    {
        return region;
    }

    public void setPhase(ArenaPhase phase)
    {
        this.phase = phase;
    }
    public ArenaPhase getPhase()
    {
        return phase;
    }

    public void setExit(Location exit)
    {
        this.exit = exit;
    }
    public Location getExit()
    {
        return exit;
    }

    public ArenaTeam getTeam(Player player)
    {
        for (ArenaTeam team : teams.values())
        {
            if (team.isMember(player))
            {
                return team;
            }
        }

        return null;
    }

    /*
     * Methods
     */
    public boolean isEmpty()
    {
        for (ArenaTeam team : getTeams())
        {
            if (!team.getMembers().isEmpty())
            {
                return false;
            }
        }

        return true;
    }
    public boolean hasEmptyTeam()
    {
        for (ArenaTeam team : getTeams())
        {
            if (team.getMembers().isEmpty())
            {
                return true;
            }
        }

        return false;
    }
    public void kickAllPlayers()
    {
        for (ArenaTeam team : getTeams())
        {
            if (exit != null)
            {
                for (UUID playerId : team.getMemberIds())
                {
                    Player player = Bukkit.getPlayer(playerId);
                    if (player != null)
                    {
                        player.teleport(exit);
                    }
                }
            }

            team.clearMemberIds();
        }
    }
    public int getSize()
    {
        int size = 0;
        for (ArenaTeam team : getTeams())
        {
            size += team.getMembers().size();
        }
        return size;
    }
}