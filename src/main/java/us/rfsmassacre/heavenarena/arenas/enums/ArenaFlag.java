package us.rfsmassacre.heavenarena.arenas.enums;

import org.bukkit.*;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import us.rfsmassacre.heavenarena.utils.ColorUtils;

public class ArenaFlag
{
    private ItemStack banner;
    private Player carrier; //When someone has the flag
    private Location current; //Where it's located
    private Location home; //Where it'll be reset to

    private ItemStack helmet; //Previous helmet the carrier had
    private BlockState state; //Previous state the block had

    public ArenaFlag(ChatColor color, Location home)
    {
        this.banner = new ItemStack(Material.BANNER);
        this.current = home;
        this.home = home;

        //Set the base color to this dye
        BannerMeta meta = (BannerMeta)banner.getItemMeta();
        meta.setBaseColor(ColorUtils.toDye(color));
        banner.setItemMeta(meta);
    }

    public ItemStack getItemStack()
    {
        return banner;
    }
    public DyeColor getDye()
    {
        BannerMeta meta = (BannerMeta)banner.getItemMeta();
        return meta.getBaseColor();
    }
    public ChatColor getColor()
    {
        return ColorUtils.toChat(getDye());
    }

    public void setCarrier(Player player)
    {
        this.carrier = player;
    }
    public Player getCarrier()
    {
        return carrier;
    }
    public boolean isCarrier(Player player)
    {
        return carrier != null && carrier.equals(player);
    }

    public void setHelmet(ItemStack helmet)
    {
        this.helmet = helmet;
    }
    public ItemStack getHelmet()
    {
        return helmet;
    }
    public void setState(BlockState state)
    {
        this.state = state;
    }
    public BlockState getState()
    {
        return state;
    }

    public void setCurrentLocation(Location current)
    {
        this.current = current;
    }
    public Location getCurrentLocation()
    {
        return current;
    }
    public void setHomeLocation(Location home)
    {
        this.home = home;
    }
    public Location getHomeLocation()
    {
        return home;
    }

    public void placeBanner(Location location)
    {
        Block block = location.getBlock();

        setState(block.getState());
        block.setType(Material.STANDING_BANNER);
        block.getState().update();

        Banner banner = (Banner)block.getState();
        banner.setBaseColor(getDye());
        banner.update();
    }
    public void removeBanner(Location location)
    {
        Block block = location.getBlock();
        if (state != null)
        {
            block.setType(state.getType());
            state.update();
            setState(null);
        }
        else
        {
            block.setType(Material.AIR);
            block.getState().update();
        }
    }
    public boolean isBanner(Location location)
    {
        Block block = location.getBlock();
        if (block.getType().equals(Material.STANDING_BANNER))
        {
            Banner banner = (Banner)block.getState();
            return banner.getBaseColor().equals(getDye());
        }

        return false;
    }

    public Location getGroundLocation(Location location)
    {
        //Check below the block to ensure you can place a banner
        World world = location.getWorld();
        int x = location.getBlockX();
        int z = location.getBlockZ();
        for (int y = location.getBlockY(); y > 0; y--)
        {
            Location below = new Location(world, x, y, z);
            if (below.getBlock().getType().isSolid())
            {
                return new Location(world, x, y + 1, z);
            }
        }

        return location;
    }
}
