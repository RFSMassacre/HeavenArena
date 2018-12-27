package us.rfsmassacre.heavenarena.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import us.rfsmassacre.heavenarena.ArenaPlugin;
import us.rfsmassacre.heavenarena.arenas.Arena;
import us.rfsmassacre.heavenarena.arenas.KOTHArena;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaPhase;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaPoint;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaTeam;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaType;
import us.rfsmassacre.heavenarena.events.arena.*;
import us.rfsmassacre.heavenarena.events.koth.PointCaptureEvent;
import us.rfsmassacre.heavenarena.events.koth.PointEnterEvent;
import us.rfsmassacre.heavenarena.events.koth.PointLeaveEvent;
import us.rfsmassacre.heavenarena.managers.ArenaManager;
import us.rfsmassacre.heavenarena.scoreboards.ArenaScoreboard;
import us.rfsmassacre.heavenarena.scoreboards.TeamScore;
import us.rfsmassacre.heavenarena.tasks.arena.BattleCountdownTask;
import us.rfsmassacre.heavenarena.tasks.koth.MeterUpdateTask;
import us.rfsmassacre.heavenarena.tasks.koth.PointUpdateTask;
import us.rfsmassacre.heavenlib.managers.ConfigManager;
import us.rfsmassacre.heavenlib.managers.LocaleManager;

import java.util.ArrayList;
import java.util.HashMap;

public class KOTHListener implements Listener
{
    private ArenaPlugin plugin;
    private ConfigManager config;
    private LocaleManager locale;
    private ArenaManager arenas;

    private HashMap<KOTHArena, ArenaScoreboard> scoreboards;

    public KOTHListener(ArenaPlugin plugin)
    {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        this.locale = plugin.getLocaleManager();
        this.arenas = plugin.getArenaManager();

        this.scoreboards = new HashMap<KOTHArena, ArenaScoreboard>();
    }

    /*
     * Reset all the capture point.
     */
    @EventHandler(ignoreCancelled = true)
    public void onKOTHOpen(ArenaOpenEvent event)
    {
        Arena arena = event.getArena();
        if (arena.getType().equals(ArenaType.KING_OF_THE_HILL))
        {
            KOTHArena kothArena = (KOTHArena)arena;
            ArenaPoint point = kothArena.getPoint();
            if (point != null)
            {
                point.resetMeter();
                point.updateColors(ChatColor.WHITE);
            }
        }
    }

    /*
     * Prepare scoreboards for this game
     */
    @EventHandler(ignoreCancelled = true)
    public void onKOTHStarting(ArenaStartingEvent event)
    {
        Arena arena = event.getArena();
        if (arena.getType().equals(ArenaType.KING_OF_THE_HILL))
        {
            KOTHArena kothArena = (KOTHArena)arena;

            //Prepare scoreboard
            int maxPoints = config.getInt("koth.max-points");
            ArenaScoreboard scoreboard = new ArenaScoreboard(plugin, arena, 0, maxPoints);
            scoreboards.put(kothArena, scoreboard);

            //Also reset map here just in case
            ArenaPoint point = kothArena.getPoint();
            if (point != null)
            {
                point.resetMeter();
                point.updateColors(ChatColor.WHITE);
            }
        }
    }

    /*
     * Start countdown, keep track of scores.
     */
    @EventHandler(ignoreCancelled = true)
    public void onKOTHBattle(ArenaBattleEvent event)
    {
        Arena arena = event.getArena();
        if (arena.getType().equals(ArenaType.KING_OF_THE_HILL))
        {
            KOTHArena kothArena = (KOTHArena)arena;

            //Start countdown
            ArenaScoreboard scoreboard = scoreboards.get(kothArena);
            BattleCountdownTask battleTask = new BattleCountdownTask(locale, kothArena, scoreboard);
            battleTask.runTaskTimer(plugin, 0, 20);

            //Start meter updater
            MeterUpdateTask meterTask = new MeterUpdateTask(kothArena);
            meterTask.runTaskTimer(plugin, 0, 20);

            //Start point updater
            PointUpdateTask pointTask = new PointUpdateTask(kothArena, scoreboard, config.getInt("koth.max-points"));
            pointTask.runTaskTimer(plugin, 0, 20);
        }
    }

    /*
     * Remove players from the boss bar.
     */
    @EventHandler(ignoreCancelled = true)
    public void onPointEnding(ArenaEndingEvent event)
    {
        Arena arena = event.getArena();
        if (arena.getType().equals(ArenaType.KING_OF_THE_HILL))
        {
            KOTHArena kothArena = (KOTHArena)arena;
            ArenaPoint point = kothArena.getPoint();

            point.resetPlayerBar();
            point.clearContestants();
        }
    }

    /*
     * Winners and losers are calculated here.
     */
    @EventHandler(ignoreCancelled = true)
    public void onKOTHEnding(ArenaEndingEvent event)
    {
        //Don't calculate winners if the game was forfeited
        if (event.isForfeit())
        {
            return;
        }

        Arena arena = event.getArena();
        if (arena.getType().equals(ArenaType.KING_OF_THE_HILL))
        {
            KOTHArena kothArena = (KOTHArena)arena;

            //Get winning teams
            ArenaScoreboard scoreboard = scoreboards.get(kothArena);
            if (scoreboard != null)
            {
                ArrayList<TeamScore> teamScores = scoreboard.getTeamScores();
                ArrayList<TeamScore> winners = scoreboard.getWinningTeamScores();
                //If there's a tie, then no team gets a reward.
                if (winners.size() > 1)
                {
                    for (TeamScore winner : winners)
                    {
                        ArenaTeam winningTeam = arena.getTeam(winner.getColor());
                        ArenaTieEvent tieEvent = new ArenaTieEvent(arena, winningTeam, false);
                        Bukkit.getPluginManager().callEvent(tieEvent);
                    }
                }
                //Else there's a winner and a loser
                else
                {
                    for (TeamScore teamScore : teamScores)
                    {
                        if (winners.contains(teamScore))
                        {
                            ArenaTeam winningTeam = arena.getTeam(teamScore.getColor());
                            ArenaWinEvent winEvent = new ArenaWinEvent(arena, winningTeam, false);
                            Bukkit.getPluginManager().callEvent(winEvent);
                        }
                        else
                        {
                            ArenaTeam losingTeam = arena.getTeam(teamScore.getColor());
                            ArenaLoseEvent loseEvent = new ArenaLoseEvent(arena, losingTeam, false);
                            Bukkit.getPluginManager().callEvent(loseEvent);
                        }
                    }
                }
            }
        }
    }

    /*
     * When a player enters or exits the point.
     */
    @EventHandler
    public void onPointEntry(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();
        Arena arena = arenas.getArena(player);
        if (arena != null && arena.getType().equals(ArenaType.KING_OF_THE_HILL)
        && arena.getPhase().equals(ArenaPhase.BATTLE))
        {
            KOTHArena kothArena = (KOTHArena)arena;
            ArenaTeam team = arena.getTeam(player);
            if (team != null)
            {
                ArenaPoint point = kothArena.getPoint();
                Location from = event.getFrom();
                Location to = event.getTo();

                //If a player enters a region
                if (point.getRegion().contains(to) & !point.getRegion().contains(from))
                {
                    point.addContestant(player);
                    point.addPlayerBar(player);

                    PointEnterEvent enterEvent = new PointEnterEvent(kothArena, player);
                    Bukkit.getPluginManager().callEvent(enterEvent);
                }

                //If a player leaves the region
                if (point.getRegion().contains(from) & !point.getRegion().contains(to))
                {
                    point.removeContestant(player);
                    point.removePlayerBar(player);

                    PointLeaveEvent leaveEvent = new PointLeaveEvent(kothArena, player);
                    Bukkit.getPluginManager().callEvent(leaveEvent);
                }
            }
        }
    }

    /*
     * When a player dies from the point.
     */
    @EventHandler(ignoreCancelled = true)
    public void onPointDeath(PlayerDeathEvent event)
    {
        Player player = event.getEntity();
        Arena arena = arenas.getArena(player);
        if (arena != null && arena.getType().equals(ArenaType.KING_OF_THE_HILL)
        && arena.getPhase().equals(ArenaPhase.BATTLE))
        {
            KOTHArena kothArena = (KOTHArena)arena;
            ArenaTeam team = arena.getTeam(player);
            if (team != null)
            {
                ArenaPoint point = kothArena.getPoint();

                point.removeContestant(player);
                point.removePlayerBar(player);

                PointLeaveEvent leaveEvent = new PointLeaveEvent(kothArena, player);
                Bukkit.getPluginManager().callEvent(leaveEvent);
            }
        }
    }

    /*
     * When a player logs off from the point.
     */
    @EventHandler(ignoreCancelled = true)
    public void onPointLogout(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        Arena arena = arenas.getArena(player);
        if (arena != null && arena.getType().equals(ArenaType.KING_OF_THE_HILL))
        {
            KOTHArena kothArena = (KOTHArena)arena;
            ArenaTeam team = arena.getTeam(player);
            if (team != null)
            {
                ArenaPoint point = kothArena.getPoint();

                point.removeContestant(player);
                point.removePlayerBar(player);

                PointLeaveEvent leaveEvent = new PointLeaveEvent(kothArena, player);
                Bukkit.getPluginManager().callEvent(leaveEvent);
            }
        }
    }

    /*
     * When a player leaves the arena from the point.
     */
    @EventHandler(ignoreCancelled = true)
    public void onPointLeave(ArenaLeaveEvent event)
    {
        Player player = event.getPlayer();
        Arena arena = event.getArena();
        if (arena.getType().equals(ArenaType.KING_OF_THE_HILL))
        {
            KOTHArena kothArena = (KOTHArena)arena;
            ArenaPoint point = kothArena.getPoint();

            point.removeContestant(player);
            point.removePlayerBar(player);

            PointLeaveEvent leaveEvent = new PointLeaveEvent(kothArena, player);
            Bukkit.getPluginManager().callEvent(leaveEvent);
        }
    }

    /*
     * Message Events
     */
    @EventHandler(ignoreCancelled = true)
    public void onEnterPoint(PointEnterEvent event)
    {
        KOTHArena arena = event.getArena();
        ArenaPoint point = event.getPoint();
        Player player = event.getPlayer();

        //If this player is the only one contesting, announce it
        if (point.getContestants().size() == 1)
        {
            ArenaTeam team = arena.getTeam(player);

            if (point.getControllingColor() == null || !team.getColor().equals(point.getControllingColor()))
            {
                //Announce to everyone in the arena
                for (ArenaTeam otherTeam : arena.getTeams())
                {
                    for (Player contestant : otherTeam.getMembers())
                    {
                        locale.sendLocale(contestant, "koth.point.contesting",
                                "{color}", team.getColor() + team.getName());
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onCapturePoint(PointCaptureEvent event)
    {
        KOTHArena arena = event.getArena();
        ArenaTeam team = event.getTeam();

        //Announce to everyone in the arena
        for (ArenaTeam otherTeam : arena.getTeams())
        {
            for (Player contestant : otherTeam.getMembers())
            {
                locale.sendLocale(contestant, "koth.point.captured",
                        "{color}", team.getColor() + team.getName());
            }
        }
    }
}
