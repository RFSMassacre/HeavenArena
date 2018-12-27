package us.rfsmassacre.heavenarena.tasks.arena;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.rfsmassacre.heavenarena.arenas.Arena;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaPhase;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaTeam;
import us.rfsmassacre.heavenarena.events.arena.ArenaBattleEvent;
import us.rfsmassacre.heavenlib.managers.LocaleManager;

import java.util.UUID;

public class StartingCountdownTask extends BukkitRunnable
{
    private LocaleManager locale;
    private Arena arena;
    private int seconds;

    public StartingCountdownTask(LocaleManager locale, Arena arena, int seconds)
    {
        if (!arena.getPhase().equals(ArenaPhase.STARTING))
        {
            throw new IllegalArgumentException(arena.getName() + " Arena must be STARTING.");
        }
        else if (seconds <= 0)
        {
            throw new IllegalArgumentException("Seconds must be higher than 0!");
        }
        else
        {
            this.locale = locale;
            this.arena = arena;
            this.seconds = seconds;
        }
    }

    @Override
    public void run()
    {
        for (ArenaTeam team : arena.getTeams())
        {
            for (Player player : team.getMembers())
            {
                if (seconds > 0)
                {
                    locale.sendActionMessage(player, "&eStarting in... " + Integer.toString(seconds));
                }
                else if (seconds == 0)
                {
                    locale.sendActionMessage(player, "&eGO!");
                }
            }
        }

        //Once the countdown is over, start the battle phase.
        if (seconds < 0 || arena.isEmpty())
        {
            ArenaBattleEvent battleEvent = new ArenaBattleEvent(arena);
            Bukkit.getPluginManager().callEvent(battleEvent);

            this.cancel();
            return;
        }

        //End this if not in the right phase
        if (!arena.getPhase().equals(ArenaPhase.STARTING))
        {
            this.cancel();
            return;
        }

        seconds--;
    }
}
