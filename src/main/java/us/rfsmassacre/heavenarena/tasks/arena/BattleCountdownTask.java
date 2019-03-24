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

import java.util.concurrent.TimeUnit;

public abstract class BattleCountdownTask extends BukkitRunnable
{
    protected LocaleManager locale;
    protected Arena arena;
    protected ArenaScoreboard scoreboard;
    protected int seconds;

    public BattleCountdownTask(LocaleManager locale, Arena arena, ArenaScoreboard scoreboard, int seconds)
    {
        if (!arena.getPhase().equals(ArenaPhase.BATTLE))
        {
            throw new IllegalArgumentException(arena.getName() + " Arena must be BATTLING.");
        }
        else if (seconds <= 0)
        {
            throw new IllegalArgumentException("Seconds must be higher than 0!");
        }
        else
        {
            this.locale = locale;
            this.arena = arena;
            this.scoreboard = scoreboard;
            this.seconds = seconds;
        }
    }

    @Override
    public void run()
    {
        showScores();
        if (!overtime())
        {
            endCycle();
        }
    }

    //Define this and it should be run first.
    protected abstract boolean overtime();

    //Shows the scores for the game.
    protected void showScores()
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

                String redMessage = "&cRED &4(&e" + redPoints + "&4/&e" + maxPoints + "&4) &7&l| &9BLUE &1(&e"
                        + bluePoints + "&1/&e" + maxPoints + "&1)";
                String blueMessage = "&9BLUE &1(&e" + bluePoints + "&1/&e" + maxPoints + "&1) &7&l| &4RED &4(&e"
                        + redPoints + "&4/&e" + maxPoints + "&4)";

                //When there's no time left, assume it's overtime
                if (seconds <= 0)
                {
                    redMessage += " &7&l| &6OVERTIME";
                    blueMessage += " &7&l| &6OVERTIME";
                }
                else
                {
                    redMessage += " &7&l| &6" + seconds + " Seconds";
                    blueMessage += " &7&l| &6" + seconds + " Seconds";
                }

                if (team.getName().equals("RED"))
                {
                    locale.sendActionMessage(player, redMessage);
                }
                else
                {
                    locale.sendActionMessage(player, blueMessage);
                }

                //Notifies if team is empty for ending
                if (arena.hasEmptyTeam())
                {
                    locale.sendLocale(player, "game.not-enough-players");
                }

                seconds--;
            }
        }
    }

    //Run this at the end of the cycle.
    private void endCycle()
    {
        //End game if time is up.
        if (seconds <= 0)
        {
            ArenaEndingEvent endingEvent = new ArenaEndingEvent(arena, false);
            Bukkit.getPluginManager().callEvent(endingEvent);

            this.cancel();
            return;
        }

        //End game early if there's an empty team.
        if (arena.hasEmptyTeam())
        {
            ArenaEndingEvent endingEvent = new ArenaEndingEvent(arena, true);
            Bukkit.getPluginManager().callEvent(endingEvent);

            this.cancel();
            return;
        }

        //End this whole thing if the phase suddenly changed.
        if (!arena.getPhase().equals(ArenaPhase.BATTLE))
        {
            this.cancel();
            return;
        }
    }
}
