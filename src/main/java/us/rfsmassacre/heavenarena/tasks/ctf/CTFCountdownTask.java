package us.rfsmassacre.heavenarena.tasks.ctf;

import us.rfsmassacre.heavenarena.arenas.Arena;
import us.rfsmassacre.heavenarena.arenas.CTFArena;
import us.rfsmassacre.heavenarena.scoreboards.ArenaScoreboard;
import us.rfsmassacre.heavenarena.scoreboards.TeamScore;
import us.rfsmassacre.heavenarena.tasks.arena.BattleCountdownTask;
import us.rfsmassacre.heavenlib.managers.LocaleManager;

import java.util.ArrayList;

public class CTFCountdownTask extends BattleCountdownTask
{
    public CTFCountdownTask(LocaleManager locale, Arena arena, ArenaScoreboard scoreboard, int seconds)
    {
        super(locale, arena, scoreboard, seconds);
    }

    @Override
    protected boolean overtime()
    {
        CTFArena ctfArena = (CTFArena)arena;
        ArrayList<TeamScore> winningScores = scoreboard.getWinningTeamScores();
        if (winningScores.size() > 1)
        {
            if (!ctfArena.getFlagCarriers().isEmpty())
            {
                return true;
            }
        }

        return false;
    }
}
