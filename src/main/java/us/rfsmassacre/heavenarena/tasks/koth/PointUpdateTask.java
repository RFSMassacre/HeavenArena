package us.rfsmassacre.heavenarena.tasks.koth;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.rfsmassacre.heavenarena.arenas.KOTHArena;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaPhase;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaPoint;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaTeam;
import us.rfsmassacre.heavenarena.events.arena.ArenaEndingEvent;
import us.rfsmassacre.heavenarena.events.koth.PointCaptureEvent;
import us.rfsmassacre.heavenarena.scoreboards.ArenaScoreboard;
import us.rfsmassacre.heavenlib.managers.ConfigManager;
import us.rfsmassacre.heavenlib.managers.LocaleManager;

public class PointUpdateTask extends BukkitRunnable
{
    private ConfigManager config;
    private KOTHArena arena;
    private ArenaPoint point;
    private ArenaScoreboard scoreboard;
    private int maxCap;

    private ChatColor previousColor;

    public PointUpdateTask(ConfigManager config, KOTHArena arena, ArenaScoreboard scoreboard, int maxCap)
    {
        this.config = config;
        this.arena = arena;
        this.point = arena.getPoint();
        this.scoreboard = scoreboard;
        this.maxCap = maxCap;
    }

    @Override
    public void run()
    {
        ChatColor color = point.getControllingColor();
        if (color != null)
        {
            //Cancel points if enemy is on it
            if (config.getBoolean("stop-points"))
            {
                for (Player player : point.getContestants())
                {
                    ArenaTeam team = arena.getTeam(player);
                    if (!team.getColor().equals(color))
                    {
                        return;
                    }
                }
            }

            //Else give points
            point.updateColors(color);

            if (previousColor == null || !color.equals(previousColor))
            {
                previousColor = color;

                PointCaptureEvent event = new PointCaptureEvent(arena, null, arena.getTeam(color));
                Bukkit.getPluginManager().callEvent(event);
            }

            if (scoreboard.getScore(color) > maxCap)
            {
                ArenaEndingEvent endingEvent = new ArenaEndingEvent(arena, false);
                Bukkit.getPluginManager().callEvent(endingEvent);
            }

            scoreboard.addScore(color, 1);
        }

        if (!arena.getPhase().equals(ArenaPhase.BATTLE))
        {
            this.cancel();
        }
    }
}
