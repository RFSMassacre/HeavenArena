package us.rfsmassacre.heavenarena.listeners;

import be.maximmvdw.titlemotd.ui.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import us.rfsmassacre.heavenarena.ArenaPlugin;
import us.rfsmassacre.heavenarena.arenas.Arena;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaPhase;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaTeam;
import us.rfsmassacre.heavenarena.events.arena.*;
import us.rfsmassacre.heavenarena.managers.ArenaManager;
import us.rfsmassacre.heavenarena.tasks.arena.EndingCountdownTask;
import us.rfsmassacre.heavenarena.tasks.arena.StartingCountdownTask;
import us.rfsmassacre.heavenlib.managers.ConfigManager;
import us.rfsmassacre.heavenlib.managers.LocaleManager;

import java.util.UUID;

public class ArenaListener implements Listener
{
    private ArenaPlugin plugin;
    private ConfigManager config;
    private LocaleManager locale;
    private ArenaManager arenas;

    public ArenaListener(ArenaPlugin plugin)
    {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        this.locale = plugin.getLocaleManager();
        this.arenas = plugin.getArenaManager();
    }

    /*
     * Make arena stand by for the next game.
     * Prepare scoreboard
     */
    @EventHandler(ignoreCancelled = true)
    public void onArenaOpen(ArenaOpenEvent event)
    {
        Arena arena = event.getArena();
        arena.kickAllPlayers();
        arena.setPhase(ArenaPhase.OPEN);
    }

    /*
     * Bring all players to their respective lobbies.
     * Count down until the prep phase is over.
     */
    @EventHandler(ignoreCancelled = true)
    public void onArenaStarting(ArenaStartingEvent event)
    {
        Arena arena = event.getArena();
        arena.setPhase(ArenaPhase.STARTING);

        //TP everyone to their lobbies
        for (ArenaTeam team : arena.getTeams())
        {
            for (UUID playerId : team.getMemberIds())
            {
                Player player = Bukkit.getPlayer(playerId);
                if (player != null)
                {
                    player.teleport(team.getSpawn());
                    locale.sendLocale(player, "game.joined.game",
                            "{type}", locale.title(arena.getType().toString()));
                }
            }
        }

        if (config.getBoolean("announcements.game-starting"))
        {
            //Announce to everyone not in the arena
            for (Player player : Bukkit.getOnlinePlayers())
            {
                if (arena.getTeam(player) == null)
                {
                    locale.sendLocale(player, "game.starting",
                            "{type}", locale.title(arena.getType().toString()));
                }
            }
        }

        //Start countdown
        int time = config.getInt("game.start-time");
        StartingCountdownTask task = new StartingCountdownTask(locale, arena, time);
        task.runTaskTimer(plugin, 0, 20);
    }

    /*
     * Set a timer for the battle phase. This is universal for all arenas.
     */
    @EventHandler(ignoreCancelled = true)
    public void onArenaBattle(ArenaBattleEvent event)
    {
        Arena arena = event.getArena();
        arena.setPhase(ArenaPhase.BATTLE);

        if (config.getBoolean("announcements.game-started")
        && !arena.hasEmptyTeam())
        {
            //Announce to everyone not in the arena
            for (Player player : Bukkit.getOnlinePlayers())
            {
                if (arena.getTeam(player) == null)
                {
                    locale.sendLocale(player, "game.started",
                            "{type}", locale.title(arena.getType().toString()));
                }
            }
        }
    }

    /*
     * After a set time, players will be teleported out.
     * During this time, PVP is disabled.
     *
     * Winners and losers are calculated here on separate listeners.
     */
    @EventHandler(ignoreCancelled = true)
    public void onArenaEnding(ArenaEndingEvent event)
    {
        Arena arena = event.getArena();
        arena.setPhase(ArenaPhase.ENDING);

        if (config.getBoolean("announcements.game-ended")
        && !arena.hasEmptyTeam())
        {
            //Announce to everyone not in the arena
            for (Player player : Bukkit.getOnlinePlayers())
            {
                if (arena.getTeam(player) == null)
                {
                    locale.sendLocale(player, "game.ended", "{type}", locale.title(arena.getType().toString()));
                }
            }
        }

        //Start countdown
        int time = config.getInt("game.end-time");
        EndingCountdownTask task = new EndingCountdownTask(locale, arena, time);
        task.runTaskTimer(plugin, 0, 20);
    }

    /*
     * A game where the enemy team all left is forfeited.
     */
    @EventHandler(ignoreCancelled = true)
    public void onArenaForfeit(ArenaEndingEvent event)
    {
        if (!event.isForfeit())
        {
            return;
        }

        Arena arena = event.getArena();
        for (ArenaTeam team : arena.getTeams())
        {
            if (!team.getMembers().isEmpty())
            {
                ArenaWinEvent winEvent = new ArenaWinEvent(arena, team, true);
                Bukkit.getPluginManager().callEvent(winEvent);
            }
            else
            {
                ArenaLoseEvent loseEvent = new ArenaLoseEvent(arena, team, true);
                Bukkit.getPluginManager().callEvent(loseEvent);
            }
        }
    }

    /*
     * These announce after a team has won, tied, or lost their game.
     */
    @EventHandler(ignoreCancelled = true)
    public void onArenaWin(ArenaWinEvent event)
    {
        Title title = new Title("VICTORY");
        title.setTitleColor(ChatColor.GREEN);
        title.setSubtitle("Your team has won!");
        title.setSubtitleColor(ChatColor.DARK_GREEN);
        title.setTimingsToTicks();
        title.setFadeInTime(config.getInt("titles.conclude.fade-in"));
        title.setStayTime(config.getInt("titles.conclude.stay"));
        title.setFadeInTime(config.getInt("titles.conclude.fade-out"));

        for (Player player : event.getTeam().getMembers())
        {
            title.send(player);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onArenaTie(ArenaTieEvent event)
    {
        Title title = new Title("DRAW");
        title.setTitleColor(ChatColor.GOLD);
        title.setSubtitle("Your team has tied.");
        title.setSubtitleColor(ChatColor.YELLOW);
        title.setTimingsToTicks();
        title.setFadeInTime(config.getInt("titles.conclude.fade-in"));
        title.setStayTime(config.getInt("titles.conclude.stay"));
        title.setFadeInTime(config.getInt("titles.conclude.fade-out"));

        for (Player player : event.getTeam().getMembers())
        {
            title.send(player);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onArenaLose(ArenaLoseEvent event)
    {
        Title title = new Title("DEFEAT");
        title.setTitleColor(ChatColor.GRAY);
        title.setSubtitle("Your team has lost...");
        title.setSubtitleColor(ChatColor.WHITE);
        title.setTimingsToTicks();
        title.setFadeInTime(config.getInt("titles.conclude.fade-in"));
        title.setStayTime(config.getInt("titles.conclude.stay"));
        title.setFadeInTime(config.getInt("titles.conclude.fade-out"));

        for (Player player : event.getTeam().getMembers())
        {
            title.send(player);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBattleAnnounce(ArenaBattleEvent event)
    {
        Title title = new Title("FIGHT");
        title.setTitleColor(ChatColor.RED);
        title.setTimingsToTicks();
        title.setFadeInTime(config.getInt("titles.battle.fade-in"));
        title.setStayTime(config.getInt("titles.battle.stay"));
        title.setFadeInTime(config.getInt("titles.battle.fade-out"));

        for (ArenaTeam team : event.getArena().getTeams())
        {
            for (Player player : team.getMembers())
            {
                title.send(player);
            }
        }
    }

    /*
     * These give rewards per player based on what rank they are on.
     */
    @EventHandler(ignoreCancelled = true)
    public void onArenaWinPrize(ArenaWinEvent event)
    {
        if (!event.isForfeit())
        {
            int prize = config.getInt("rewards");
            for (Player player : event.getTeam().getMembers())
            {
                for (int multiply = 10; multiply > 0; multiply--)
                {
                    String permission = "heavenarena.rewards." + multiply;
                    if (player.hasPermission(permission))
                    {
                        int fullPrize = prize * multiply;
                        String command = "eco give " + player.getName() + " " + fullPrize;
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);

                        break;
                    }
                }
            }
        }
    }
}
