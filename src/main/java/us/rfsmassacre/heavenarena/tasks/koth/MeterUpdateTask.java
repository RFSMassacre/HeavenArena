package us.rfsmassacre.heavenarena.tasks.koth;

import be.maximmvdw.titlemotd.ui.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.rfsmassacre.heavenarena.arenas.KOTHArena;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaPhase;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaPoint;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaTeam;

import java.util.ArrayList;

public class MeterUpdateTask extends BukkitRunnable
{
    private KOTHArena arena;
    private ArenaPoint point;

    public MeterUpdateTask(KOTHArena arena)
    {
        this.arena = arena;
        this.point = arena.getPoint();
    }

    @Override
    public void run()
    {
        if (!arena.getPhase().equals(ArenaPhase.BATTLE))
        {
            this.cancel();
        }
        else if (!point.getContestants().isEmpty())
        {
            //If only one team is contesting the point, add it to their meter every second.
            ArrayList<ChatColor> teams = new ArrayList<ChatColor>();
            for (Player player : point.getContestants())
            {
                ArenaTeam team = arena.getTeam(player);
                if (team != null && !teams.contains(team.getColor()))
                {
                    teams.add(team.getColor());
                }
            }

            //Also change the wool if contested
            if (!teams.isEmpty())
            {
                point.updateColors(ChatColor.YELLOW);

                //If there's only one team contesting
                if (teams.size() == 1)
                {
                    ChatColor team = teams.get(0);
                    ChatColor enemy = team.equals(ChatColor.RED) ? ChatColor.BLUE : ChatColor.RED;

                    point.addMeter(team);
                    if (point.getMeter(team) >= 0)
                    {
                        if (point.getMeter(team) == point.getMaxMeter())
                        {
                            point.setBarTitle(team + team.name() + " Team " + ChatColor.GRAY + "captured!");
                        }
                        else
                        {
                            point.setBarTitle(team + team.name() + " Team " + ChatColor.GRAY + "is capturing the objective!");
                        }

                        point.setBarProgress(point.getMeter(team));
                        point.setBarColor(team);
                    }
                    else
                    {
                        if (point.getMeter(enemy) == point.getMaxMeter())
                        {
                            point.setBarTitle(enemy + enemy.name() + " Team " + ChatColor.GRAY + "has the objective!");
                        }
                        else
                        {
                            point.setBarTitle(enemy + enemy.name() + " Team " + ChatColor.GRAY + "is losing the objective!");
                        }

                        point.setBarProgress(point.getMeter(enemy));
                        point.setBarColor(enemy);
                    }
                }
            }
        }
    }
}
