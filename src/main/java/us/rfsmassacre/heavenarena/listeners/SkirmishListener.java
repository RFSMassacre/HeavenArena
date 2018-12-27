package us.rfsmassacre.heavenarena.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import us.rfsmassacre.heavenarena.ArenaPlugin;
import us.rfsmassacre.heavenarena.arenas.Arena;
import us.rfsmassacre.heavenarena.arenas.SkirmishArena;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaTeam;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaType;
import us.rfsmassacre.heavenarena.events.arena.ArenaBattleEvent;
import us.rfsmassacre.heavenarena.events.arena.ArenaEndingEvent;
import us.rfsmassacre.heavenarena.events.arena.ArenaTieEvent;
import us.rfsmassacre.heavenarena.tasks.arena.BattleCountdownTask;
import us.rfsmassacre.heavenlib.managers.ConfigManager;
import us.rfsmassacre.heavenlib.managers.LocaleManager;

public class SkirmishListener implements Listener
{
    private ArenaPlugin plugin;
    private ConfigManager config;
    private LocaleManager locale;

    public SkirmishListener(ArenaPlugin plugin)
    {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        this.locale = plugin.getLocaleManager();
    }

    /*
     * Start countdown without any scores
     */
    @EventHandler(ignoreCancelled = true)
    public void onSkirmishBattle(ArenaBattleEvent event)
    {
        Arena arena = event.getArena();
        if (arena.getType().equals(ArenaType.SKIRMISH))
        {
            SkirmishArena skirmishArena = (SkirmishArena)arena;

            //Start countdown
            BattleCountdownTask battleTask = new BattleCountdownTask(locale, skirmishArena, null);
            battleTask.runTaskTimer(plugin, 0, 20);
        }
    }

    /*
     * All Skirmishes end in a tie since there's no objective.
     */
    @EventHandler(ignoreCancelled = true)
    public void onSkirmishEnding(ArenaEndingEvent event)
    {
        if (event.isForfeit())
        {
            return;
        }

        Arena arena = event.getArena();
        if (arena.getType().equals(ArenaType.SKIRMISH))
        {
            SkirmishArena skirmishArena = (SkirmishArena)arena;
            for (ArenaTeam team : arena.getTeams())
            {
                ArenaTieEvent tieEvent = new ArenaTieEvent(skirmishArena, team, false);
                Bukkit.getPluginManager().callEvent(tieEvent);
            }
        }
    }
}
