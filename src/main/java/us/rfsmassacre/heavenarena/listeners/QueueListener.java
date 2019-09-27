package us.rfsmassacre.heavenarena.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import us.rfsmassacre.heavenarena.ArenaPlugin;
import us.rfsmassacre.heavenarena.arenas.Arena;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaPhase;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaTeam;
import us.rfsmassacre.heavenarena.events.arena.ArenaJoinEvent;
import us.rfsmassacre.heavenarena.events.arena.ArenaStartingEvent;
import us.rfsmassacre.heavenarena.events.queue.QueueJoinEvent;
import us.rfsmassacre.heavenarena.events.queue.QueueLeaveEvent;
import us.rfsmassacre.heavenarena.managers.ArenaManager;
import us.rfsmassacre.heavenarena.managers.QueueManager;
import us.rfsmassacre.heavenarena.tasks.arena.WaitingInfoTask;
import us.rfsmassacre.heavenlib.managers.LocaleManager;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class QueueListener implements Listener
{
    private ArenaPlugin plugin;
    private LocaleManager locale;
    private ArenaManager arenas;
    private QueueManager queue;

    private Arena currentArena;

    public QueueListener(ArenaPlugin plugin)
    {
        this.plugin = plugin;
        this.locale = plugin.getLocaleManager();
        this.arenas = plugin.getArenaManager();
        this.queue = plugin.getQueueManager();
        this.currentArena = nextArena();

        //Keep changing arenas every second until finding the next open one.
        //This keeps it from never having an arena ready.
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if ((currentArena == null || currentArena.getPhase().equals(ArenaPhase.CLOSED))
                || currentArena.getPhase().equals(ArenaPhase.ENDING))
                {
                    currentArena = nextArena();
                }
            }
        }.runTaskTimer(plugin, 0, 5);
    }

    //Picks the next random arena for players to join.
    //Once this arena is full, another one is to be picked.
    private Arena nextArena()
    {
        LinkedList<Arena> openArenas = new LinkedList<Arena>();
        for (Arena arena : arenas.getArenas())
        {
            if (arena.getPhase().equals(ArenaPhase.OPEN))
            {
                openArenas.add(arena);
            }
        }

        if (!openArenas.isEmpty())
        {
            Collections.shuffle(openArenas, new Random(System.currentTimeMillis()));
            return openArenas.getFirst();
        }

        return null;
    }

    /*
     * Set arena to waiting when the first player queues.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onQueueInitiate(QueueJoinEvent event)
    {
        if (currentArena != null && currentArena.getPhase().equals(ArenaPhase.OPEN))
        {
            currentArena.setPhase(ArenaPhase.WAITING);
        }

        //Start a new task only when the queue was empty before
        if (queue.getSize() == 1)
        {
            //Keep players in queue informed of the progress
            WaitingInfoTask task = new WaitingInfoTask(locale, queue);
            task.runTaskTimer(plugin, 0, 20);
        }
    }

    /*
     * Clear the current arena when the queue is empty.
     */
    @EventHandler(ignoreCancelled = true)
    public void onQueueLeave(QueueLeaveEvent event)
    {
        if (queue.getSize() == 0)
        {
            if (currentArena != null && currentArena.getPhase().equals(ArenaPhase.WAITING))
            {
                currentArena.setPhase(ArenaPhase.OPEN);
                currentArena = null;
            }
        }
    }

    /*
     * Once there's a minimum amount of players, start the transition.
     * Make sure to keep the arena in memory while waiting.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onQueueStart(QueueJoinEvent event)
    {
        if (currentArena != null)
        {
            if (currentArena.getPhase().equals(ArenaPhase.WAITING))
            {
                int minimum = 0;
                for (ArenaTeam team : currentArena.getTeams())
                {
                    minimum += team.getMinMembers();
                }

                if (queue.getSize() >= minimum || event.isForced())
                {
                    LinkedList<ArenaTeam> teams = new LinkedList<ArenaTeam>();
                    teams.addAll(currentArena.getTeams());

                    while (queue.getSize() > 0 && !currentArena.isFull())
                    {
                        //Sort teams and add player to least member team
                        //If full, it'll try again for a different team.
                        Collections.sort(teams);
                        ArenaTeam team = teams.getFirst();
                        Player player = queue.peekPlayer();
                        if (player != null && team.addMember(player))
                        {
                            ArenaJoinEvent joinEvent = new ArenaJoinEvent(player, currentArena);
                            Bukkit.getPluginManager().callEvent(joinEvent);

                            String teamName = team.getColor().toString() + team.getName();
                            locale.sendLocale(player, "game.joined.team", "{team}", teamName);
                            queue.removePlayer(player);
                        }
                    }

                    //Let the plugin know to start the arena
                    ArenaStartingEvent arenaEvent = new ArenaStartingEvent(currentArena);
                    Bukkit.getPluginManager().callEvent(arenaEvent);
                }
            }
        }
    }

    /*
     * Allow players to continue joining arena
     */
    @EventHandler(ignoreCancelled = true)
    public void onQueueJoin(QueueJoinEvent event)
    {
        if (currentArena != null)
        {
            if (currentArena.getPhase().equals(ArenaPhase.STARTING)
            || currentArena.getPhase().equals(ArenaPhase.BATTLE))
            {
                LinkedList<ArenaTeam> teams = new LinkedList<ArenaTeam>();
                teams.addAll(currentArena.getTeams());

                if (!currentArena.isFull())
                {
                    //Sort teams and add player to least member team
                    //If full, it'll try again for a different team.
                    Collections.sort(teams);
                    ArenaTeam team = teams.getFirst();
                    Player player = event.getPlayer();
                    if (player != null && team.addMember(player))
                    {
                        ArenaJoinEvent joinEvent = new ArenaJoinEvent(player, currentArena);
                        Bukkit.getPluginManager().callEvent(joinEvent);

                        String teamName = team.getColor().toString() + team.getName();
                        locale.sendLocale(player, "game.joined.team", "{team}", teamName);
                        queue.removePlayer(player);

                        player.teleport(team.getSpawn());
                        String typeName = currentArena.getType().toString();
                        locale.sendLocale(player, "game.joined.game", "{type}", locale.title(typeName));
                    }
                }
            }
        }
    }

    /*
     * Remove players from the queue when they log off
     */
    @EventHandler
    public void onQueueLogout(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        queue.removePlayer(player);
    }
}
