package us.rfsmassacre.heavenarena.tasks.arena;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.rfsmassacre.heavenarena.arenas.Arena;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaPhase;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaTeam;
import us.rfsmassacre.heavenarena.events.arena.ArenaEndingEvent;
import us.rfsmassacre.heavenarena.scoreboards.ArenaScoreboard;
import us.rfsmassacre.heavenlib.managers.LocaleManager;

public class BattleCountdownTask extends BukkitRunnable
{
    protected LocaleManager locale;
    protected Arena arena;
    protected ArenaScoreboard scoreboard;

    public BattleCountdownTask(LocaleManager locale, Arena arena, ArenaScoreboard scoreboard)
    {
        if (!arena.getPhase().equals(ArenaPhase.BATTLE))
        {
            throw new IllegalArgumentException(arena.getName() + " Arena must be BATTLING.");
        }
        else
        {
            this.locale = locale;
            this.arena = arena;
            this.scoreboard = scoreboard;
        }
    }

    @Override
    public void run()
    {
        for (ArenaTeam team : arena.getTeams())
        {
            for (Player player : team.getMembers())
            {
                String redPoints = "0";
                String bluePoints = "0";
                String maxPoints = "0";

                if (scoreboard != null)
                {
                    redPoints = Integer.toString(scoreboard.getScore(ChatColor.RED));
                    bluePoints = Integer.toString(scoreboard.getScore(ChatColor.BLUE));
                    maxPoints = Integer.toString(scoreboard.getMaxScore());
                }

                String message = "&cRED &4(&e" + redPoints + "&4/&e" + maxPoints + "&4) &7&l| &9BLUE &1(&e"
                        + bluePoints + "&1/&e" + maxPoints + "&1)";

                locale.sendActionMessage(player, message);

                if (arena.hasEmptyTeam())
                {
                    locale.sendLocale(player, "game.not-enough-players");
                }
            }
        }

        //End game early if there's an empty team.
        if (arena.hasEmptyTeam())
        {
            ArenaEndingEvent endingEvent = new ArenaEndingEvent(arena, true);
            Bukkit.getPluginManager().callEvent(endingEvent);

            this.cancel();
            return;
        }

        //End this whole thing if the battle phase suddenly changed.
        if (!arena.getPhase().equals(ArenaPhase.BATTLE))
        {
            this.cancel();
            return;
        }
    }
}
