package us.rfsmassacre.heavenarena.events.queue;

public class QueueJoinEvent extends QueueEvent
{
    private boolean force;

    public QueueJoinEvent(boolean force)
    {
        super();

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
