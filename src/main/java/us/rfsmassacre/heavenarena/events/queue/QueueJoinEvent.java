package us.rfsmassacre.heavenarena.events.queue;

import org.bukkit.entity.Player;

public class QueueJoinEvent extends QueueEvent
{
    private boolean force;

    public QueueJoinEvent(Player player, boolean force)
    {
        super(player);

        this.force = force;
    }

    public boolean isForced()
    {
        return force;
    }
    public void setForced(boolean force)
    {
        this.force = force;
    }
}
