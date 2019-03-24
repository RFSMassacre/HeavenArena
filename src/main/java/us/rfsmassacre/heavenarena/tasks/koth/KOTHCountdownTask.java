package us.rfsmassacre.heavenarena.tasks.koth;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.rfsmassacre.heavenarena.arenas.Arena;
import us.rfsmassacre.heavenarena.arenas.KOTHArena;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaTeam;
import us.rfsmassacre.heavenarena.scoreboards.ArenaScoreboard;
import us.rfsmassacre.heavenarena.scoreboards.TeamScore;
import us.rfsmassacre.heavenarena.tasks.arena.BattleCountdownTask;
import us.rfsmassacre.heavenlib.managers.LocaleManager;

import java.util.ArrayList;

public class KOTHCountdownTask extends BattleCountdownTask
{
    public KOTHCountdownTask(LocaleManager locale, Arena arena, ArenaScoreboard scoreboard, int seconds)
    {
        super(locale, arena, scoreboard, seconds);
    }

    @Override
    protected boolean overtime()
    {
        KOTHArena kothArena = (KOTHArena)arena;
        ChatColor color = kothArena.getPoint().getControllingColor();

        for (Player player : kothArena.getPoint().getContestants())
        {
            ArenaTeam team = kothArena.getTeam(player);
            if (!team.getColor().equals(color))
            {
                return true;
            }
        }

        ArrayList<TeamScore> winningScores = scoreboard.getWinningTeamScores();
        if (winningScores != null && !winningScores.isEmpty())
        {
            ChatColor winningColor = winningScores.get(0).getColor();
            if (!winningColor.equals(color))
            {
                return true;
            }
        }

        return false;
    }
}
