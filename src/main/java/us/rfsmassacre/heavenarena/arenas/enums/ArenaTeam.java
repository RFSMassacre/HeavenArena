package us.rfsmassacre.heavenarena.arenas.enums;

import com.faris.Cuboid;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.UUID;

/*
 * Keeps track of who is on what team.
 */
public class ArenaTeam implements Comparable<ArenaTeam>
{
    private ChatColor color;
    private ChatColor altColor;
    private HashSet<UUID> memberIds;
    private Cuboid lobby;
    private Location spawn;

    private int maxMembers;
    private int minMembers;

    public ArenaTeam(ChatColor color, ChatColor altColor, Cuboid lobby, Location spawn)
    {
        this.color = color;
        this.altColor = altColor;
        this.memberIds = new HashSet<UUID>();
        this.lobby = lobby;
        this.spawn = spawn;

        this.maxMembers = 6;
        this.minMembers = 1;
    }

    public String getName()
    {
        return color.name();
    }
    public ChatColor getColor()
    {
        return color;
    }

    public void setAltColor(ChatColor altColor)
    {
        this.altColor = altColor;
    }
    public ChatColor getAltColor()
    {
        return altColor;
    }

    public void addMemberId(UUID playerId)
    {
        memberIds.add(playerId);
    }
    public void removeMemberId(UUID playerId)
    {
        memberIds.remove(playerId);
    }
    public boolean isMemberId(UUID playerId)
    {
        return memberIds.contains(playerId);
    }
    public void clearMemberIds()
    {
        memberIds.clear();
    }
    public HashSet<UUID> getMemberIds()
    {
        return memberIds;
    }

    public void setLobby(Cuboid cuboid)
    {
        this.lobby = cuboid;
    }
    public Cuboid getLobby()
    {
        return lobby;
    }

    public void setSpawn(Location location)
    {
        this.spawn = location;
    }
    public Location getSpawn()
    {
        return spawn;
    }

    public void setMaxMembers(int maxMembers)
    {
        this.maxMembers = maxMembers;
    }
    public int getMaxMembers()
    {
        return maxMembers;
    }

    public void setMinMembers(int minMembers)
    {
        this.minMembers = minMembers;
    }
    public int getMinMembers()
    {
        return minMembers;
    }

    @Override
    public boolean equals(Object object)
    {
        if (object != null && object instanceof ArenaTeam)
        {
            ArenaTeam otherTeam = (ArenaTeam)object;
            if (this.color.equals(otherTeam.color)
                    && this.memberIds.equals(otherTeam.memberIds)
                    && this.lobby.equals(otherTeam.lobby)
                    && this.spawn.equals(otherTeam.spawn))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public int compareTo(ArenaTeam otherTeam)
    {
        return this.memberIds.size() - otherTeam.memberIds.size();
    }

    /*
     * Use these functions to editing game play.
     * Functions above are for internal things.
     */
    public boolean addMember(Player player)
    {
        if (memberIds.size() <= maxMembers)
        {
            return memberIds.add(player.getUniqueId());
        }

        return false;
    }
    public Player getMember(String name)
    {
        for (UUID playerId : memberIds)
        {
            Player player = Bukkit.getPlayer(playerId);
            if (player.getName().equals(name))
            {
                return player;
            }
        }

        return null;
    }
    public boolean removeMember(Player player)
    {
        return memberIds.remove(player.getUniqueId());
    }
    public boolean isMember(Player player)
    {
        return memberIds.contains(player.getUniqueId());
    }
    public HashSet<Player> getMembers()
    {
        HashSet<Player> members = new HashSet<Player>();
        for (UUID playerId : memberIds)
        {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null)
            {
                members.add(player);
            }
        }
        return members;
    }
}
