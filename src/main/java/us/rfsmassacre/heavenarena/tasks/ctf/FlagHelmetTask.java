package us.rfsmassacre.heavenarena.tasks.ctf;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaFlag;

public class FlagHelmetTask extends BukkitRunnable
{
    private ArenaFlag flag;

    public FlagHelmetTask(ArenaFlag flag)
    {
        this.flag = flag;
    }

    @Override
    public void run()
    {
        Player carrier = flag.getCarrier();
        if (carrier == null)
        {
            this.cancel();
        }
        else
        {
            carrier.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 72000,0));

            //Check if flag is dropped first so it's not given by accident.
            ItemStack helmet = carrier.getInventory().getHelmet();
            if (!helmet.getType().equals(flag.getItemStack().getType()))
            {
                flag.setHelmet(helmet);

                if (carrier.getInventory().contains(flag.getItemStack()))
                {
                    carrier.getInventory().remove(flag.getItemStack());
                    carrier.updateInventory();
                }

                carrier.getInventory().setHelmet(flag.getItemStack());
            }
        }
    }
}
