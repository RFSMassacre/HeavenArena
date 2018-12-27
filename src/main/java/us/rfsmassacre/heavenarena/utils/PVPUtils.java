package us.rfsmassacre.heavenarena.utils;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

public class PVPUtils
{
    public static Player getAttackingPlayer(Entity entity)
    {
        if (entity instanceof Player)
        {
            return (Player)entity;
        }
        else if (entity instanceof Projectile)
        {
            Projectile projectile = (Projectile)entity;
            if (projectile.getShooter() instanceof Player)
            {
                return (Player)projectile.getShooter();
            }
        }

        return null;
    }
}
