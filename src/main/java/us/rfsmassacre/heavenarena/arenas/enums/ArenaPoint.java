package us.rfsmassacre.heavenarena.arenas.enums;

import com.faris.Cuboid;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.material.Wool;
import us.rfsmassacre.heavenarena.utils.ColorUtils;

import java.util.HashMap;
import java.util.HashSet;

public class ArenaPoint
{
    private Cuboid region; //Region where it'll count as contesting the point
    private HashMap<ChatColor, Integer> meter; //Meter for which the region is contesting
    private int maxMeter;
    private HashSet<Player> contestants;

    private BossBar progress; //Changes progress and color based on who is taking the objective

    public ArenaPoint(Cuboid region)
    {
        this.region = region;
        this.meter = new HashMap<ChatColor, Integer>();
        this.maxMeter = 10;
        this.contestants = new HashSet<Player>();

        this.progress = Bukkit.createBossBar("Uncontested...", BarColor.WHITE, BarStyle.SOLID);
    }
    public ArenaPoint(Cuboid region, int maxMeter)
    {
        this.region = region;
        this.meter = new HashMap<ChatColor, Integer>();
        this.maxMeter = maxMeter;
        this.contestants = new HashSet<Player>();

        this.progress = Bukkit.createBossBar("Uncontested...", BarColor.WHITE, BarStyle.SOLID);
    }

    public void setRegion(Cuboid region)
    {
        this.region = region;
    }
    public Cuboid getRegion()
    {
        return region;
    }

    public void setMaxMeter(int maxMeter)
    {
        this.maxMeter = maxMeter;
    }
    public int getMaxMeter()
    {
        return maxMeter;
    }

    public ChatColor getControllingColor()
    {
        for (ChatColor color : meter.keySet())
        {
            if (getMeter(color) == maxMeter)
            {
                return color;
            }
        }

        return null;
    }
    public int getMeter(ChatColor color)
    {
        if (meter.get(color) == null)
        {
            meter.put(color, 0);
        }

        return meter.get(color);
    }
    //When adding meter to one team, deplete it from the others.
    public void addMeter(ChatColor color)
    {
        for (ChatColor teamColor : meter.keySet())
        {
            if (teamColor.equals(color))
            {
                if (getMeter(color) < maxMeter)
                {
                    int number = getMeter(color);
                    meter.put(color, number + 1);
                }
            }
            else
            {
                if (getMeter(teamColor) > -maxMeter)
                {
                    int number = getMeter(teamColor);
                    meter.put(teamColor, number - 1);
                }
            }
        }
    }
    public void resetMeter()
    {
        meter.clear();
    }

    public void addContestant(Player player)
    {
        contestants.add(player);
    }
    public void removeContestant(Player player)
    {
        contestants.remove(player);
    }
    public boolean isContestant(Player player)
    {
        return contestants.contains(player);
    }
    public void clearContestants()
    {
        contestants.clear();
    }
    public HashSet<Player> getContestants()
    {
        return contestants;
    }

    public void setBarTitle(String title)
    {
        progress.setTitle(title);
    }
    public void setBarProgress(int meter)
    {
        progress.setProgress((double)meter / maxMeter);
    }
    public void setBarColor(ChatColor color)
    {
        progress.setColor(ColorUtils.toBar(color));
    }
    public void addPlayerBar(Player player)
    {
        progress.addPlayer(player);
    }
    public void removePlayerBar(Player player)
    {
        progress.removePlayer(player);
    }
    public void resetPlayerBar()
    {
        progress.removeAll();
    }
    public BossBar getBossBar()
    {
        return progress;
    }

    public void updateColors(ChatColor color)
    {
        for (Block block : region.getBlocks())
        {
            if (block.getType().equals(Material.WOOL))
            {
                BlockState state = block.getState();
                state.setData(new Wool(ColorUtils.toDye(color)));
                state.update();
            }
        }
    }
}
