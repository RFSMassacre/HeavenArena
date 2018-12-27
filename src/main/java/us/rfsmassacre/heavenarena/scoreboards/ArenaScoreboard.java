package us.rfsmassacre.heavenarena.scoreboards;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import us.rfsmassacre.heavenarena.ArenaPlugin;
import us.rfsmassacre.heavenarena.arenas.Arena;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaTeam;
import us.rfsmassacre.heavenlib.managers.ConfigManager;
import us.rfsmassacre.heavenlib.managers.LocaleManager;

import java.util.*;

public class ArenaScoreboard
{
    private ConfigManager config;
    private LocaleManager locale;
    private Arena arena;
    private Scoreboard scoreboard;
    private int maxScore;

    //Keep track of actual scores
    private HashMap<ChatColor, TeamScore> scores;

    public ArenaScoreboard(ArenaPlugin plugin, Arena arena, int startingScore, int maxScore)
    {
        this.config = plugin.getConfigManager();
        this.locale = plugin.getLocaleManager();
        this.arena = arena;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.maxScore = maxScore;

        this.scores = new HashMap<ChatColor, TeamScore>();
        for (ArenaTeam team : arena.getTeams())
        {
            scores.put(team.getColor(), new TeamScore(team.getColor(), startingScore));
        }

        //Don't sync anything yet. Just use this to keep track of scores.
    }

    public Arena getArena()
    {
        return arena;
    }
    public Scoreboard getScoreboard()
    {
        return scoreboard;
    }

    public void syncTeams()
    {
        for (ArenaTeam arenaTeam : arena.getTeams())
        {
            String teamName = arenaTeam.getAltColor() + "[" + arenaTeam.getColor()
            + arenaTeam.getName() + arenaTeam.getAltColor() + "] " + ChatColor.RESET;

            //Prepare team
            Team scoreTeam = scoreboard.getTeam(arenaTeam.getName());
            if (scoreTeam == null)
            {
                scoreTeam = scoreboard.registerNewTeam(arenaTeam.getName());
                scoreTeam.setPrefix(teamName);
                scoreTeam.setCanSeeFriendlyInvisibles(true);
            }

            //Sync all entries
            for (UUID playerId : arenaTeam.getMemberIds())
            {
                Player player = Bukkit.getPlayer(playerId);
                if (player != null)
                {
                    if (!scoreTeam.hasEntry(player.getName()))
                    {
                        scoreTeam.addEntry(player.getName());
                        player.setScoreboard(scoreboard);
                    }
                }
            }
            for (String entry : scoreTeam.getEntries())
            {
                Player player = Bukkit.getPlayer(entry);
                if (arenaTeam.getMember(entry) == null)
                {
                    scoreTeam.removeEntry(entry);
                    if (player != null)
                    {
                        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                    }
                }
            }
        }
    }
    public void syncHealthBar()
    {
        Objective healthBar = scoreboard.getObjective("health");
        if (healthBar == null)
        {
            healthBar = scoreboard.registerNewObjective("health", "health");
            healthBar.setDisplaySlot(DisplaySlot.BELOW_NAME);
            healthBar.setDisplayName(locale.format(config.getString("scoreboards.health-icon")));
        }
    }
    public void syncScores()
    {
        Objective sideBar = scoreboard.getObjective("sidebar");
        if (sideBar == null)
        {
            sideBar = scoreboard.registerNewObjective("sidebar", "dummy");
            sideBar.setDisplaySlot(DisplaySlot.SIDEBAR);
            sideBar.setDisplayName(ChatColor.YELLOW + locale.title(arena.getType().toString()));
        }

        for (ArenaTeam arenaTeam : arena.getTeams())
        {
            int points = 0;
            if (scores.get(arenaTeam.getColor()) != null)
            {
                points = scores.get(arenaTeam.getColor()).getScore();
            }

            Score score = sideBar.getScore(arenaTeam.getColor() + arenaTeam.getName() + " Points");
            score.setScore(points);
        }
    }
    public void clearEntries()
    {
        for (ArenaTeam team : arena.getTeams())
        {
            for (UUID playerId : team.getMemberIds())
            {
                Player player = Bukkit.getPlayer(playerId);
                player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            }
        }
    }

    /*
     * These should only run if they were previously synced.
     */
    public void addScore(ChatColor color, int score)
    {
        TeamScore teamScore = scores.get(color);
        if (scores.get(color) == null)
        {
            teamScore = new TeamScore(color, 0);
            scores.put(color, teamScore);
        }
        teamScore.setScore(teamScore.getScore() + score);
    }
    public void substractScore(ChatColor color, int score)
    {
        TeamScore teamScore = scores.get(color);
        if (scores.get(color) == null)
        {
            teamScore = new TeamScore(color, 0);
            scores.put(color, teamScore);
        }
        teamScore.setScore(teamScore.getScore() - score);
    }
    public void setScore(ChatColor color, int score)
    {
        TeamScore teamScore = scores.get(color);
        if (scores.get(color) == null)
        {
            teamScore = new TeamScore(color, 0);
            scores.put(color, teamScore);
        }
        teamScore.setScore(score);
    }
    public int getScore(ChatColor color)
    {
        TeamScore teamScore = scores.get(color);
        if (scores.get(color) == null)
        {
            teamScore = new TeamScore(color, 0);
            scores.put(color, teamScore);
        }
        return teamScore.getScore();
    }
    public int getMaxScore()
    {
        return maxScore;
    }

    /*
     * Calculating which teams are currently ahead
     */
    public ArrayList<TeamScore> getTeamScores()
    {
        ArrayList<TeamScore> teamScores = new ArrayList<TeamScore>();
        teamScores.addAll(scores.values());
        Collections.sort(teamScores);
        return teamScores;
    }
    public ArrayList<TeamScore> getWinningTeamScores()
    {
        ArrayList<TeamScore> teamScores = getTeamScores();
        try
        {
            TeamScore lead = teamScores.get(0);
            ArrayList<TeamScore> runnerUps = new ArrayList<TeamScore>();
            for (int slot = 0; slot < teamScores.size(); slot++)
            {
                TeamScore runnerUp = teamScores.get(slot);
                if (lead.getScore() == runnerUp.getScore())
                {
                    runnerUps.add(runnerUp);
                }
            }

            return runnerUps;
        }
        catch (ArrayIndexOutOfBoundsException exception)
        {
            //Do nothing
        }

        return null;
    }
}
