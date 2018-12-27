package us.rfsmassacre.heavenarena.listeners;

import com.faris.Cuboid;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import us.rfsmassacre.heavenarena.ArenaPlugin;
import us.rfsmassacre.heavenarena.arenas.Arena;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaPhase;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaTeam;
import us.rfsmassacre.heavenarena.events.arena.ArenaLeaveEvent;
import us.rfsmassacre.heavenarena.managers.ArenaManager;
import us.rfsmassacre.heavenarena.utils.PVPUtils;
import us.rfsmassacre.heavenlib.managers.ConfigManager;

import java.util.HashMap;
import java.util.UUID;

public class PlayerListener implements Listener
{
    private ArenaPlugin plugin;
    private ConfigManager config;
    private ArenaManager arenas;

    private HashMap<UUID, Arena> deadPlayers;

    public PlayerListener(ArenaPlugin plugin)
    {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        this.arenas = plugin.getArenaManager();

        this.deadPlayers = new HashMap<UUID, Arena>();
    }

    /*
     * Players cannot leave the lobby when preparing team.
     */
    @EventHandler(ignoreCancelled = true)
    public void onLeaveLobbyEarly(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();
        Arena arena = arenas.getArena(player);
        if (arena != null)
        {
            if (arena.getPhase().equals(ArenaPhase.WAITING)
            || arena.getPhase().equals(ArenaPhase.STARTING))
            {
                ArenaTeam team = arena.getTeam(player);
                if (team != null)
                {
                    Cuboid lobby = team.getLobby();
                    if (!lobby.contains(event.getTo()) && lobby.contains(event.getFrom()))
                    {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    /*
     * Players cannot enter enemy lobbies.
     */
    @EventHandler(ignoreCancelled = true)
    public void onEnemyLobbyEnter(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();
        Arena arena = arenas.getArena(player);
        if (arena != null)
        {
            for (ArenaTeam team : arena.getTeams())
            {
                if (!team.isMember(player))
                {
                    Cuboid lobby = team.getLobby();
                    if (lobby.contains(event.getTo()))
                    {
                        event.setCancelled(true);
                        player.setVelocity(player.getVelocity().multiply(config.getDouble("barrier.velocity")));
                        return;
                    }
                }
            }
        }
    }

    /*
     * Players cannot TP into enemy lobbies.
     */
    @EventHandler(ignoreCancelled = true)
    public void onEnemyLobbyTP(PlayerTeleportEvent event)
    {
        Player player = event.getPlayer();
        Arena arena = arenas.getArena(player);
        if (arena != null)
        {
            for (ArenaTeam team : arena.getTeams())
            {
                if (!team.isMember(player))
                {
                    Cuboid lobby = team.getLobby();
                    if (lobby.contains(event.getTo()))
                    {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    /*
     * Players cannot damage allies
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamageAlly(EntityDamageByEntityEvent event)
    {
        if (event.getEntity() instanceof Player)
        {
            Player defendingPlayer = (Player)event.getEntity();
            Arena arena = arenas.getArena(defendingPlayer);
            if (arena != null)
            {
                Player attackingPlayer = PVPUtils.getAttackingPlayer(event.getDamager());
                if (attackingPlayer != null)
                {
                    ArenaTeam defendingTeam = arena.getTeam(defendingPlayer);
                    ArenaTeam attackingTeam = arena.getTeam(attackingPlayer);
                    if (defendingTeam.equals(attackingTeam))
                    {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    /*
     * Players cannot die when the game is ending.
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamageWhileEnding(EntityDamageEvent event)
    {
        if (event.getEntity() instanceof Player)
        {
            Player player = (Player)event.getEntity();
            Arena arena = arenas.getArena(player);
            if (arena != null)
            {
                if (arena.getPhase().equals(ArenaPhase.ENDING))
                {
                    event.setCancelled(true);
                }
            }
        }
    }

    /*
     * Players cannot die when in their lobby.
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamageInLobby(EntityDamageEvent event)
    {
        if (event.getEntity() instanceof Player)
        {
            Player player = (Player)event.getEntity();
            Arena arena = arenas.getArena(player);
            if (arena != null)
            {
                ArenaTeam team = arena.getTeam(player);
                if (team != null)
                {
                    if (team.getLobby().contains(player.getLocation()))
                    {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    /*
     * If players die in battle, mark them as dead.
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerBattleDeath(PlayerDeathEvent event)
    {
        Player player = event.getEntity();
        Arena arena = arenas.getArena(player);
        if (arena != null)
        {
            if (arena.getPhase().equals(ArenaPhase.BATTLE))
            {
                deadPlayers.put(player.getUniqueId(), arena);
            }
        }
    }

    /*
     * If players respawn after the game is over, teleport them to the exit.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerLateRespawn(PlayerRespawnEvent event)
    {
        Player player = event.getPlayer();
        Arena arena = arenas.getArena(player);
        if (arena == null && deadPlayers.containsKey(player.getUniqueId()))
        {
            event.setRespawnLocation(deadPlayers.get(player.getUniqueId()).getExit());
            deadPlayers.remove(player.getUniqueId());
        }
    }

    /*
     * Players respawn in their respective lobby.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event)
    {
        Player player = event.getPlayer();
        Arena arena = arenas.getArena(player);
        if (arena != null)
        {
            ArenaTeam team = arena.getTeam(player);
            if (team != null)
            {
                event.setRespawnLocation(team.getSpawn());
            }
        }
    }

    /*
     * If a player leaves the game, treat them as if they're still in it.
     * TP them to the ally spawn on login.
     */
    /*
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event)
    {
        Player player = event.getPlayer();
        Arena arena = arenas.getArena(player);
        if (arena != null)
        {
            ArenaTeam team = arena.getTeam(player);
            if (team != null)
            {
                new BukkitRunnable()
                {
                    @Override
                    public void run()
                    {
                        player.teleport(team.getSpawn());
                    }
                }.runTaskLater(plugin, 1);
            }
        }
    }
    */

    /*
     * If a player leaves, remove them from the game.
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerLogout(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        Arena arena = arenas.getArena(player);
        if (arena != null)
        {
            ArenaTeam team = arena.getTeam(player);
            if (team != null)
            {
                team.removeMember(player);

                ArenaLeaveEvent leaveEvent = new ArenaLeaveEvent(player, arena);
                Bukkit.getPluginManager().callEvent(leaveEvent);
            }
        }
    }

    /*
     * If a player tries to teleport out of the arena, cancel it
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerTeleportOut(PlayerTeleportEvent event)
    {
        Player player = event.getPlayer();
        Arena arena = arenas.getArena(player);
        if (arena != null)
        {
            if (!arena.getPhase().equals(ArenaPhase.ENDING))
            {
                ArenaTeam team = arena.getTeam(player);
                if (team != null)
                {
                    Location from = event.getFrom();
                    Location to = event.getTo();
                    Cuboid region = arena.getRegion();

                    if (region.contains(from) & !region.contains(to))
                    {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
