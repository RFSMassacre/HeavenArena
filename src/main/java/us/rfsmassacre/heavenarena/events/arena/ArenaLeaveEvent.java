package us.rfsmassacre.heavenarena.events.arena;

import org.bukkit.entity.Player;
import us.rfsmassacre.heavenarena.arenas.Arena;

public class ArenaLeaveEvent extends ArenaEvent
{
    private Player player;

    public ArenaLeaveEvent(Player player, Arena arena)
    {
        super(arena);

        this.player = player;
    }

    public Player getPlayer()
    {
        return player;
    }
}
