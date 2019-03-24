package us.rfsmassacre.heavenarena.listeners;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.events.SpellCastEvent;
import com.nisovin.magicspells.events.SpellTargetEvent;
import com.nisovin.magicspells.spells.BuffSpell;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import us.rfsmassacre.heavenarena.ArenaPlugin;
import us.rfsmassacre.heavenarena.arenas.Arena;
import us.rfsmassacre.heavenarena.arenas.CTFArena;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaFlag;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaTeam;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaType;
import us.rfsmassacre.heavenarena.events.ctf.FlagPickupEvent;
import us.rfsmassacre.heavenarena.managers.ArenaManager;
import us.rfsmassacre.heavenlib.managers.ConfigManager;
import us.rfsmassacre.heavenlib.managers.LocaleManager;

import java.util.List;

public class SpellListener implements Listener
{
    private ConfigManager config;
    private LocaleManager locale;
    private ArenaManager arenas;

    public SpellListener(ArenaPlugin plugin)
    {
        this.config = plugin.getConfigManager();
        this.locale = plugin.getLocaleManager();
        this.arenas = plugin.getArenaManager();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onSpellTarget(SpellTargetEvent event)
    {
        Player caster = event.getCaster();
        if (caster != null)
        {
            Arena arena = arenas.getArena(caster);
            LivingEntity targetEntity = event.getTarget();
            if (arena != null && targetEntity != null && targetEntity instanceof Player)
            {
                Player target = (Player)event.getTarget();

                ArenaTeam casterTeam = arena.getTeam(caster);
                ArenaTeam targetTeam = arena.getTeam(target);
                boolean beneficial = event.getSpell().isBeneficial();

                if (casterTeam.equals(targetTeam))
                {
                    if (!beneficial)
                    {
                        event.setCancelled(true);
                    }
                }
                else
                {
                    if (beneficial)
                    {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    /*
     * Flag carriers cannot cast spells.
     */
    @EventHandler(ignoreCancelled = true)
    public void onFlagSpellCast(SpellCastEvent event)
    {
        Player caster = event.getCaster();
        if (caster != null)
        {
            Arena arena = arenas.getArena(caster);
            if (arena != null && arena.getType().equals(ArenaType.CAPTURE_THE_FLAG))
            {
                CTFArena ctfArena = (CTFArena) arena;
                ArenaFlag flag = ctfArena.getFlag(caster);
                List<String> allowedSpells = config.getStringList("ctf.allowed-spells");
                List<String> blockedSpells = config.getStringList("ctf.blocked-spells");

                if (flag != null)
                {
                    if (!config.getBoolean("ctf.cast-spells") && !allowedSpells.contains(event.getSpell().getInternalName()))
                    {
                        event.setCancelled(true);
                    }
                    else if (config.getBoolean("ctf.cast-spells") && blockedSpells.contains(event.getSpell().getInternalName()))
                    {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    /*
     * Flag carriers will lose all their current buffs.
     */
    @EventHandler(ignoreCancelled = true)
    public void onFlagPickup(FlagPickupEvent event)
    {
        if (!config.getBoolean("ctf.cast-spells"))
        {
            Player carrier = event.getCarrier();
            if (MagicSpells.getBuffManager().getActiveBuffs(carrier) != null)
            {
                for (BuffSpell buff : MagicSpells.getBuffManager().getActiveBuffs(carrier))
                {
                    buff.turnOff(carrier);
                    locale.sendLocale(carrier, "ctf.cant-cast");
                }
            }
        }
    }
}
