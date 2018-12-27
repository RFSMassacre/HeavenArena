package us.rfsmassacre.heavenarena.tasks.arena;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.rfsmassacre.heavenarena.arenas.Arena;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaPhase;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaTeam;
import us.rfsmassacre.heavenarena.events.arena.ArenaOpenEvent;
import us.rfsmassacre.heavenlib.managers.LocaleManager;

import java.util.UUID;

public class EndingCountdownTask extends BukkitRunnable
{
    private LocaleManager locale;
    private Arena arena;
    private int seconds;

    public EndingCountdownTask(LocaleManager locale, Arena arena, int seconds)
    {
        if (!arena.getPhase().equals(ArenaPhase.ENDING))
        {
            throw new IllegalArgumentException(arena.getName() + " Arena must be in ENDING.");
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
                    locale.sendActionMessage(player, "&c" + Integer.toString(seconds));
                }
                else if (seconds == 0)
                {
                    locale.sendActionMessage(player, "&cENDED!");
                }
            }
        }

        //Once the countdown is over, clear and reset the arena
        if (seconds < 0 || arena.isEmpty())
        {
            //Heal everyone in the arena
            for (ArenaTeam team : arena.getTeams())
            {
                for (Player player : team.getMembers())
                {
                    if (!player.isDead())
                    {
                        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                    }
                }
            }

            ArenaOpenEvent openEvent = new ArenaOpenEvent(arena);
            Bukkit.getPluginManager().callEvent(openEvent);

            this.cancel();
            return;
        }

        //End this if not in the right phase
        if (!arena.getPhase().equals(ArenaPhase.ENDING))
        {
            this.cancel();
            return;
        }

        seconds--;
    }
}
