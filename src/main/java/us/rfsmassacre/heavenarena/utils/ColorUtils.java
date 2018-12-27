package us.rfsmassacre.heavenarena.utils;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.boss.BarColor;

public class ColorUtils
{
    public static DyeColor toDye(ChatColor color)
    {
        return DyeColor.valueOf(color.name());
    }

    public static ChatColor toChat(DyeColor color)
    {
        return ChatColor.valueOf(color.name());
    }

    public static BarColor toBar(ChatColor color)
    {
        return BarColor.valueOf(color.name());
    }
}
