package us.rfsmassacre.heavenarena.events.queue;

import org.bukkit.entity.Player;

public class QueueLeaveEvent extends QueueEvent
{
    public QueueLeaveEvent(Player player)
    {
        super(player);
    }
}
