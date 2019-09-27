package us.rfsmassacre.heavenarena.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import us.rfsmassacre.heavenarena.ArenaPlugin;
import us.rfsmassacre.heavenarena.arenas.Arena;
import us.rfsmassacre.heavenarena.arenas.TDMArena;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaTeam;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaType;
import us.rfsmassacre.heavenarena.events.arena.*;
import us.rfsmassacre.heavenarena.managers.ArenaManager;
import us.rfsmassacre.heavenarena.scoreboards.ArenaScoreboard;
import us.rfsmassacre.heavenarena.scoreboards.TeamScore;
import us.rfsmassacre.heavenarena.tasks.tdm.TDMCountdownTask;
import us.rfsmassacre.heavenlib.managers.ConfigManager;
import us.rfsmassacre.heavenlib.managers.LocaleManager;

import java.util.ArrayList;
import java.util.HashMap;

public class TDMListener implements Listener
{
    private ArenaPlugin plugin;
    private ConfigManager config;
    private LocaleManager locale;
    private ArenaManager arenas;

    private HashMap<TDMArena, ArenaScoreboard> scoreboards;

    public TDMListener(ArenaPlugin plugin)
    {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        this.locale = plugin.getLocaleManager();
        this.arenas = plugin.getArenaManager();

        this.scoreboards = new HashMap<TDMArena, ArenaScoreboard>();
    }

    /*
     * Prepare scoreboards for this game.
     */
    @EventHandler(ignoreCancelled = true)
    public void onTDMStarting(ArenaStartingEvent event)
    {
        Arena arena = event.getArena();
        if (arena.getType().equals(ArenaType.TEAM_DEATHMATCH))
        {
            TDMArena tdmArena = (TDMArena)arena;

            //Prepare scoreboard
            int maxKills = config.getInt("tdm.max-points");
            ArenaScoreboard scoreboard = new ArenaScoreboard(plugin, tdmArena, 0, maxKills);
            scoreboards.put(tdmArena, scoreboard);
        }
    }

    /*
     * Start countdown and keep track of scores
     */
    @EventHandler(ignoreCancelled = true)
    public void onTDMBattle(ArenaBattleEvent event)
    {
        Arena arena = event.getArena();
        if (arena.getType().equals(ArenaType.TEAM_DEATHMATCH))
        {
            TDMArena tdmArena = (TDMArena)arena;

            //Start countdown
            int time = config.getInt("tdm.battle-time");
            ArenaScoreboard scoreboard = scoreboards.get(tdmArena);

            //Set max score based on how many players per team
            int maxScore = 0;
            int maxKills = config.getInt("tdm.max-points");
            for (ArenaTeam team : tdmArena.getTeams())
            {
                maxScore += team.getMembers().size() * maxKills;
            }
            scoreboard.setMaxScore(maxScore);
            TDMCountdownTask battleTask = new TDMCountdownTask(locale, tdmArena, scoreboard, time);
            battleTask.runTaskTimer(plugin, 0, 20);
        }
    }

    /*
     * Record each kill as a positive point for the killing team.
     *
     * When a certain score has been reached, end the game early.
     */
    @EventHandler(ignoreCancelled = true)
    public void onTDMKill(PlayerDeathEvent event)
    {
        Player victim = event.getEntity();
        Arena arena = arenas.getArena(victim);
        if (arena != null)
        {
            if (arena.getType().equals(ArenaType.TEAM_DEATHMATCH))
            {
                TDMArena tdmArena = (TDMArena)arena;

                ArenaTeam victimTeam = arena.getTeam(victim);
                for (ArenaTeam enemyTeam : arena.getTeams())
                {
                    if (!enemyTeam.equals(victimTeam))
                    {
                        ArenaScoreboard scoreboard = scoreboards.get(tdmArena);
                        if (scoreboard != null)
                        {
                            int maxKills = scoreboard.getMaxScore();

                            scoreboard.addScore(enemyTeam.getColor(), 1);
                            if (scoreboard.getScore(enemyTeam.getColor()) >= maxKills)
                            {
                                ArenaEndingEvent endingEvent = new ArenaEndingEvent(tdmArena, false);
                                Bukkit.getPluginManager().callEvent(endingEvent);
                            }
                        }
                    }
                }
            }
        }
    }

    /*
     * Winners and losers are calculated here.
     */
    @EventHandler(ignoreCancelled = true)
    public void onTDMEnding(ArenaEndingEvent event)
    {
        //Don't calculate winners if the game was forfeited
        if (event.isForfeit())
        {
            return;
        }

        Arena arena = event.getArena();
        if (arena.getType().equals(ArenaType.TEAM_DEATHMATCH))
        {
            TDMArena tdmArena = (TDMArena)arena;

            //Get winning teams
            ArenaScoreboard scoreboard = scoreboards.get(tdmArena);
            if (scoreboard != null)
            {
                ArrayList<TeamScore> teamScores = scoreboard.getTeamScores();
                ArrayList<TeamScore> winners = scoreboard.getWinningTeamScores();
                //If there's a tie, then no team gets a reward.
                if (winners.size() > 1)
                {
                    for (TeamScore winner : winners)
                    {
                        ArenaTeam winningTeam = tdmArena.getTeam(winner.getColor());
                        ArenaTieEvent tieEvent = new ArenaTieEvent(tdmArena, winningTeam, false);
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
                            ArenaWinEvent winEvent = new ArenaWinEvent(tdmArena, winningTeam, false);
                            Bukkit.getPluginManager().callEvent(winEvent);
                        }
                        else
                        {
                            ArenaTeam losingTeam = arena.getTeam(teamScore.getColor());
                            ArenaLoseEvent loseEvent = new ArenaLoseEvent(tdmArena, losingTeam, false);
                            Bukkit.getPluginManager().callEvent(loseEvent);
                        }
                    }
                }
            }
        }
    }
}
