package us.rfsmassacre.heavenarena.tasks.skirmish;

import us.rfsmassacre.heavenarena.arenas.Arena;
import us.rfsmassacre.heavenarena.scoreboards.ArenaScoreboard;
import us.rfsmassacre.heavenarena.tasks.arena.BattleCountdownTask;
import us.rfsmassacre.heavenlib.managers.LocaleManager;

public class SkirmishCountdownTask extends BattleCountdownTask
{
    public SkirmishCountdownTask(LocaleManager locale, Arena arena, ArenaScoreboard scoreboard, int seconds)
    {
        super(locale, arena, scoreboard, seconds);
    }

    @Override
    protected boolean overtime()
    {
        //Skirmish can never reach overtime
        return false;
    }
}
