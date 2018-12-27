package us.rfsmassacre.heavenarena.tasks.arena;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.rfsmassacre.heavenarena.managers.QueueManager;
import us.rfsmassacre.heavenlib.managers.LocaleManager;

import java.util.UUID;

public class WaitingInfoTask extends BukkitRunnable
{
    private LocaleManager locale;
    private QueueManager queue;

    public WaitingInfoTask(LocaleManager locale, QueueManager queue)
    {
        this.locale = locale;
        this.queue = queue;
    }

    @Override
    public void run()
    {
        for (UUID playerId : queue.getQueue())
        {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null)
            {
                locale.sendActionMessage(player, "&e" + Integer.toString(queue.getSize()) + " players waiting...");
            }
        }

        if (queue.getSize() == 0)
        {
            this.cancel();
        }
    }
}
