package us.rfsmassacre.heavenarena.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.UUID;

public class QueueManager
{
    private LinkedList<UUID> queue; //First one to queue, first one to find a game

    public QueueManager()
    {
        this.queue = new LinkedList<UUID>();
    }

    /*
     * Player Queue
     */
    public Player pollPlayer()
    {
        return Bukkit.getPlayer(queue.pollFirst());
    }
    public Player peekPlayer()
    {
        if (!queue.isEmpty())
        {
            return Bukkit.getPlayer(queue.getFirst());
        }

        return null;
    }
    public boolean queuePlayer(Player player)
    {
        if (!queue.contains(player.getUniqueId()))
        {
            return queue.offer(player.getUniqueId());
        }

        return false;
    }
    public boolean removePlayer(Player player)
    {
        if (queue.contains(player.getUniqueId()))
        {
            queue.remove(player.getUniqueId());
            return true;
        }

        return false;
    }
    public boolean isQueued(Player player)
    {
        return queue.contains(player.getUniqueId());
    }
    public LinkedList<UUID> getQueue()
    {
        return queue;
    }
    public int getSize()
    {
        return queue.size();
    }
}
