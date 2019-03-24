package us.rfsmassacre.heavenarena.tasks.tdm;

import us.rfsmassacre.heavenarena.arenas.Arena;
import us.rfsmassacre.heavenarena.scoreboards.ArenaScoreboard;
import us.rfsmassacre.heavenarena.tasks.arena.BattleCountdownTask;
import us.rfsmassacre.heavenlib.managers.LocaleManager;

public class TDMCountdownTask extends BattleCountdownTask
{
    public TDMCountdownTask(LocaleManager locale, Arena arena, ArenaScoreboard scoreboard, int seconds)
    {
        super(locale, arena, scoreboard, seconds);
    }

    @Override
    protected boolean overtime()
    {
        //TDM can never reach overtime.
        return false;
    }
}
