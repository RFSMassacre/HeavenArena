package us.rfsmassacre.heavenarena.arenas;

import com.faris.Cuboid;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaFlag;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaType;

import java.util.Collection;
import java.util.HashMap;

public class CTFArena extends Arena
{
    private HashMap<ChatColor, ArenaFlag> flags;

    public CTFArena(String name, Cuboid region, Location exit)
    {
        super(name, ArenaType.CAPTURE_THE_FLAG, region, exit);

        this.flags = new HashMap<ChatColor, ArenaFlag>();
    }
    public CTFArena(Arena arena)
    {
        super(arena);

        this.flags = new HashMap<ChatColor, ArenaFlag>();
    }

    public void setFlag(ChatColor color, ArenaFlag flag)
    {
        flags.put(color, flag);
    }
    public ArenaFlag getFlag(ChatColor color)
    {
        return flags.get(color);
    }
    public boolean isFlag(ArenaFlag flag)
    {
        return flags.values().contains(flag);
    }
    public Collection<ArenaFlag> getFlags()
    {
        return flags.values();
    }

    //Get the flag of whoever is carrying it.
    public ArenaFlag getFlag(Player player)
    {
        for (ArenaFlag flag : flags.values())
        {
            if (flag.isCarrier(player))
            {
                return flag;
            }
        }

        return null;
    }
    public HashMap<ChatColor, Player> getFlagCarriers()
    {
        HashMap<ChatColor, Player> flagCarriers = new HashMap<ChatColor, Player>();
        for (ArenaFlag flag : flags.values())
        {
            if (flag.getCarrier() != null)
            {
                flagCarriers.put(flag.getColor(), flag.getCarrier());
            }
        }
        return flagCarriers;
    }
}
