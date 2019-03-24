package us.rfsmassacre.heavenarena.tasks.koth;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.rfsmassacre.heavenarena.arenas.Arena;
import us.rfsmassacre.heavenarena.arenas.KOTHArena;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaTeam;
import us.rfsmassacre.heavenarena.scoreboards.ArenaScoreboard;
import us.rfsmassacre.heavenarena.tasks.arena.BattleCountdownTask;
import us.rfsmassacre.heavenlib.managers.LocaleManager;

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

        ChatColor winningColor = scoreboard.getWinningTeamScores().get(0).getColor();
        if (!color.equals(winningColor))
        {
            return true;
        }

        return false;
    }
}
