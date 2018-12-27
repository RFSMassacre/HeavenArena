package us.rfsmassacre.heavenarena.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;
import us.rfsmassacre.heavenarena.ArenaPlugin;
import us.rfsmassacre.heavenarena.arenas.Arena;
import us.rfsmassacre.heavenarena.arenas.CTFArena;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaFlag;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaPhase;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaTeam;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaType;
import us.rfsmassacre.heavenarena.events.arena.*;
import us.rfsmassacre.heavenarena.events.ctf.FlagCapEvent;
import us.rfsmassacre.heavenarena.events.ctf.FlagDropEvent;
import us.rfsmassacre.heavenarena.events.ctf.FlagPickupEvent;
import us.rfsmassacre.heavenarena.events.ctf.FlagResetEvent;
import us.rfsmassacre.heavenarena.managers.ArenaManager;
import us.rfsmassacre.heavenarena.scoreboards.ArenaScoreboard;
import us.rfsmassacre.heavenarena.scoreboards.TeamScore;
import us.rfsmassacre.heavenarena.tasks.arena.BattleCountdownTask;
import us.rfsmassacre.heavenarena.tasks.ctf.FlagHelmetTask;
import us.rfsmassacre.heavenlib.managers.ConfigManager;
import us.rfsmassacre.heavenlib.managers.LocaleManager;

import java.util.ArrayList;
import java.util.HashMap;

public class CTFListener implements Listener
{
    private ArenaPlugin plugin;
    private ConfigManager config;
    private LocaleManager locale;
    private ArenaManager arenas;

    private HashMap<CTFArena, ArenaScoreboard> scoreboards;

    public CTFListener(ArenaPlugin plugin)
    {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        this.locale = plugin.getLocaleManager();
        this.arenas = plugin.getArenaManager();

        this.scoreboards = new HashMap<CTFArena, ArenaScoreboard>();
    }

    /*
     * Reset all the flags and prepare for a new game
     */
    @EventHandler(ignoreCancelled = true)
    public void onCTFOpen(ArenaOpenEvent event)
    {
        Arena arena = event.getArena();
        if (arena.getType().equals(ArenaType.CAPTURE_THE_FLAG))
        {
            CTFArena ctfArena = (CTFArena)arena;

            //Remove all the flags from the map.
            for (ArenaFlag flag : ctfArena.getFlags())
            {
                if (flag.getCurrentLocation() != null)
                {
                    flag.removeBanner(flag.getCurrentLocation());
                }

                flag.removeBanner(flag.getHomeLocation());
                flag.placeBanner(flag.getHomeLocation());
            }
        }
    }

    /*
     * Prepare scoreboards for this game.
     */
    @EventHandler(ignoreCancelled = true)
    public void onCTFStarting(ArenaStartingEvent event)
    {
        Arena arena = event.getArena();
        if (arena.getType().equals(ArenaType.CAPTURE_THE_FLAG))
        {
            CTFArena ctfArena = (CTFArena)arena;

            //Prepare scoreboard
            int maxCaps = config.getInt("ctf.max-points");
            ArenaScoreboard scoreboard = new ArenaScoreboard(plugin, arena, 0, maxCaps);
            scoreboards.put(ctfArena, scoreboard);

            //Also run it here just in case
            for (ArenaFlag flag : ctfArena.getFlags())
            {
                if (flag.getCurrentLocation() != null)
                {
                    flag.removeBanner(flag.getCurrentLocation());
                }

                flag.removeBanner(flag.getHomeLocation());
                flag.placeBanner(flag.getHomeLocation());
            }
        }
    }

    /*
     * Start countdown, keep track of scores.
     */
    @EventHandler(ignoreCancelled = true)
    public void onCTFBattle(ArenaBattleEvent event)
    {
        Arena arena = event.getArena();
        if (arena.getType().equals(ArenaType.CAPTURE_THE_FLAG))
        {
            CTFArena ctfArena = (CTFArena)arena;

            //Start countdown
            ArenaScoreboard scoreboard = scoreboards.get(ctfArena);
            BattleCountdownTask battleTask = new BattleCountdownTask(locale, ctfArena, scoreboard);
            battleTask.runTaskTimer(plugin, 0, 20);
        }
    }

    /*
     * Winners and losers are calculated here.
     */
    @EventHandler(ignoreCancelled = true)
    public void onCTFEnding(ArenaEndingEvent event)
    {
        //Don't calculate winners if the game was forfeited
        if (event.isForfeit())
        {
            return;
        }

        Arena arena = event.getArena();
        if (arena.getType().equals(ArenaType.CAPTURE_THE_FLAG))
        {
            CTFArena ctfArena = (CTFArena)arena;

            //Get winning teams
            ArenaScoreboard scoreboard = scoreboards.get(ctfArena);
            if (scoreboard != null)
            {
                ArrayList<TeamScore> teamScores = scoreboard.getTeamScores();
                ArrayList<TeamScore> winners = scoreboard.getWinningTeamScores();
                //If there's a tie, then no team gets a reward.
                if (winners.size() > 1)
                {
                    for (TeamScore winner : winners)
                    {
                        ArenaTeam winningTeam = arena.getTeam(winner.getColor());
                        ArenaTieEvent tieEvent = new ArenaTieEvent(arena, winningTeam, false);
                        Bukkit.getPluginManager().callEvent(tieEvent);
                    }
                }
                //Else there's a winner and a loser
                else
                {
                    for (TeamScore teamScore : teamScores)
                    {
                        if (winners.contains(teamScore))
                        {
                            ArenaTeam winningTeam = arena.getTeam(teamScore.getColor());
                            ArenaWinEvent winEvent = new ArenaWinEvent(arena, winningTeam, false);
                            Bukkit.getPluginManager().callEvent(winEvent);
                        }
                        else
                        {
                            ArenaTeam losingTeam = arena.getTeam(teamScore.getColor());
                            ArenaLoseEvent loseEvent = new ArenaLoseEvent(arena, losingTeam, false);
                            Bukkit.getPluginManager().callEvent(loseEvent);
                        }
                    }
                }
            }
        }
    }

    /*
     * Message Events
     */
    @EventHandler(ignoreCancelled = true)
    public void onFlagPickup(FlagPickupEvent event)
    {
        CTFArena arena = event.getArena();
        ChatColor flagColor = event.getFlag().getColor();
        ArenaTeam flagTeam = arena.getTeam(flagColor);

        for (ArenaTeam team : arena.getTeams())
        {
            for (Player player : team.getMembers())
            {
                locale.sendLocale(player, "ctf.flag.picked-up",
            "{player}", event.getCarrier().getDisplayName(),
                    "{color}", flagColor + flagTeam.getName());

                try
                {
                    Sound sound = Sound.valueOf(config.getString("ctf.sounds.pick-up"));
                    player.playSound(player.getLocation(), sound, 1.0F, 1.0F);
                }
                catch (IllegalArgumentException exception)
                {
                    //Do nothing
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onFlagDrop(FlagDropEvent event)
    {
        CTFArena arena = event.getArena();
        ChatColor flagColor = event.getFlag().getColor();
        ArenaTeam flagTeam = arena.getTeam(flagColor);

        for (ArenaTeam team : arena.getTeams())
        {
            for (Player player : team.getMembers())
            {
                locale.sendLocale(player, "ctf.flag.dropped",
            "{color}", flagColor + flagTeam.getName());

                try
                {
                    Sound sound = Sound.valueOf(config.getString("ctf.sounds.drop"));
                    player.playSound(player.getLocation(), sound, 1.0F, 1.0F);
                }
                catch (IllegalArgumentException exception)
                {
                    //Do nothing
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onFlagCap(FlagCapEvent event)
    {
        CTFArena arena = event.getArena();
        ChatColor flagColor = event.getFlag().getColor();
        ArenaTeam flagTeam = arena.getTeam(flagColor);

        for (ArenaTeam team : arena.getTeams())
        {
            for (Player player : team.getMembers())
            {
                locale.sendLocale(player, "ctf.flag.capped",
                        "{player}", event.getCarrier().getDisplayName(),
                        "{color}", flagColor + flagTeam.getName());

                try
                {
                    Sound sound = Sound.valueOf(config.getString("ctf.sounds.cap"));
                    player.playSound(player.getLocation(), sound, 1.0F, 1.0F);
                }
                catch (IllegalArgumentException exception)
                {
                    //Do nothing
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onFlagReset(FlagResetEvent event)
    {
        CTFArena arena = event.getArena();
        ChatColor flagColor = event.getFlag().getColor();
        ArenaTeam flagTeam = arena.getTeam(flagColor);

        for (ArenaTeam team : arena.getTeams())
        {
            for (Player player : team.getMembers())
            {
                locale.sendLocale(player, "ctf.flag.reset",
                        "{color}", flagColor + flagTeam.getName());

                try
                {
                    Sound sound = Sound.valueOf(config.getString("ctf.sounds.reset"));
                    player.playSound(player.getLocation(), sound, 1.0F, 1.0F);
                }
                catch (IllegalArgumentException exception)
                {
                    //Do nothing
                }
            }
        }
    }

    /*
     * Below are the "flag events" the plugin will listen to in order to make the game work.
     */
    @EventHandler(ignoreCancelled = true)
    public void onCarrierPickup(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();
        Arena arena = arenas.getArena(player);
        if (arena != null && arena.getType().equals(ArenaType.CAPTURE_THE_FLAG)
        && arena.getPhase().equals(ArenaPhase.BATTLE))
        {
            CTFArena ctfArena = (CTFArena)arena;
            ArenaTeam team = ctfArena.getTeam(player);
            Location to = event.getTo();

            for (ArenaFlag flag : ctfArena.getFlags())
            {
                if (flag.isBanner(to))
                {
                    //If allied flag is picked up send it back to base.
                    if (flag.getColor().equals(team.getColor()))
                    {
                        Location home = flag.getHomeLocation();

                        if (!(home.getBlockX() == to.getBlockX()
                        && home.getBlockY() == to.getBlockY()
                        && home.getBlockZ() == to.getBlockZ()))
                        {
                            flag.removeBanner(to);

                            flag.setCarrier(null);
                            flag.setCurrentLocation(flag.getHomeLocation());
                            flag.placeBanner(flag.getHomeLocation());

                            FlagResetEvent resetEvent = new FlagResetEvent(ctfArena, flag, player);
                            Bukkit.getPluginManager().callEvent(resetEvent);
                        }
                    }
                    //If enemy flag is picked up, add it to their hat
                    else
                    {
                        if (flag.getCarrier() == null || !flag.getCarrier().equals(player))
                        {
                            flag.removeBanner(to);

                            flag.setCarrier(player);
                            flag.setCurrentLocation(player.getLocation());
                            flag.setHelmet(player.getInventory().getHelmet());
                            player.getInventory().setHelmet(flag.getItemStack());

                            //Keep flag on carrier's head at all times.
                            FlagHelmetTask task = new FlagHelmetTask(flag);
                            task.runTaskTimer(plugin, 0, 5);

                            FlagPickupEvent pickupEvent = new FlagPickupEvent(ctfArena, flag, player);
                            Bukkit.getPluginManager().callEvent(pickupEvent);
                        }
                    }

                    return;
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onCarrierMove(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();
        Arena arena = arenas.getArena(player);
        if (arena != null && arena.getType().equals(ArenaType.CAPTURE_THE_FLAG)
        && arena.getPhase().equals(ArenaPhase.BATTLE))
        {
            CTFArena ctfArena = (CTFArena)arena;
            Location from = event.getFrom();

            for (ArenaFlag flag : ctfArena.getFlags())
            {
                Player carrier = flag.getCarrier();
                if (carrier != null && carrier.equals(player))
                {
                    flag.setCurrentLocation(from);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onCarrierDrop(PlayerDeathEvent event)
    {
        Player player = event.getEntity();
        Arena arena = arenas.getArena(player);
        if (arena != null && arena.getType().equals(ArenaType.CAPTURE_THE_FLAG)
        && arena.getPhase().equals(ArenaPhase.BATTLE))
        {
            CTFArena ctfArena = (CTFArena)arena;
            ArenaFlag flag = ctfArena.getFlag(player);

            if (flag != null)
            {
                Location position = flag.getGroundLocation(flag.getCurrentLocation());

                player.getInventory().setHelmet(flag.getHelmet());
                flag.setCarrier(null);
                flag.setHelmet(null);
                flag.placeBanner(position);
                flag.setCurrentLocation(position);
                player.removePotionEffect(PotionEffectType.GLOWING);

                FlagDropEvent dropEvent = new FlagDropEvent(ctfArena, flag, player);
                Bukkit.getPluginManager().callEvent(dropEvent);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onCarrierDeliver(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();
        Arena arena = arenas.getArena(player);
        if (arena != null && arena.getType().equals(ArenaType.CAPTURE_THE_FLAG)
        && arena.getPhase().equals(ArenaPhase.BATTLE))
        {
            CTFArena ctfArena = (CTFArena)arena;
            ArenaTeam team = ctfArena.getTeam(player);
            Location to = event.getTo();

            ArenaFlag allyFlag = ctfArena.getFlag(team.getColor());
            if (allyFlag.isBanner(to))
            {
                Location home = allyFlag.getHomeLocation();

                if (home.getBlockX() == to.getBlockX()
                && home.getBlockY() == to.getBlockY()
                && home.getBlockZ() == to.getBlockZ())
                {
                    ArenaFlag enemyFlag = ctfArena.getFlag(player);
                    if (enemyFlag != null)
                    {
                        //Reset flag
                        player.getInventory().setHelmet(enemyFlag.getHelmet());
                        enemyFlag.setCarrier(null);
                        enemyFlag.setHelmet(null);
                        enemyFlag.placeBanner(enemyFlag.getHomeLocation());

                        player.removePotionEffect(PotionEffectType.GLOWING);

                        //Call flag capture event
                        FlagCapEvent capEvent = new FlagCapEvent(ctfArena, enemyFlag, player);
                        Bukkit.getPluginManager().callEvent(capEvent);

                        //Raise a score
                        ArenaScoreboard scoreboard = scoreboards.get(arena);
                        if (scoreboard != null)
                        {
                            int maxCaps = config.getInt("ctf.max-points");

                            scoreboard.addScore(team.getColor(), 1);
                            if (scoreboard.getScore(team.getColor()) >= maxCaps)
                            {
                                ArenaEndingEvent endingEvent = new ArenaEndingEvent(arena, false);
                                Bukkit.getPluginManager().callEvent(endingEvent);
                            }
                        }

                    }
                }
            }
        }
    }

    /*
     * Prevent flag carriers from hiding in spawn.
     */
    @EventHandler(ignoreCancelled = true)
    public void onCarrierHide(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();
        Arena arena = arenas.getArena(player);
        if (arena != null && arena.getType().equals(ArenaType.CAPTURE_THE_FLAG)
        && arena.getPhase().equals(ArenaPhase.BATTLE))
        {
            CTFArena ctfArena = (CTFArena)arena;
            ArenaTeam team = ctfArena.getTeam(player);
            ArenaFlag flag = ctfArena.getFlag(player);
            Location to = event.getTo();

            if (flag != null && team.getLobby().contains(to))
            {
                event.setCancelled(true);
            }
        }
    }

    /*
     * These are to ensure the game doesn't break while playing
     */
    @EventHandler(ignoreCancelled = true)
    public void onCTFLeave(ArenaLeaveEvent event)
    {
        Player player = event.getPlayer();
        Arena arena = event.getArena();
        if (arena.getType().equals(ArenaType.CAPTURE_THE_FLAG))
        {
            CTFArena ctfArena = (CTFArena)arena;
            ArenaFlag flag = ctfArena.getFlag(player);
            if (flag != null)
            {
                Location position = flag.getGroundLocation(flag.getCurrentLocation());

                player.getInventory().setHelmet(flag.getHelmet());
                flag.setCarrier(null);
                flag.setHelmet(null);
                flag.placeBanner(position);
                flag.setCurrentLocation(position);
                player.removePotionEffect(PotionEffectType.GLOWING);

                FlagDropEvent dropEvent = new FlagDropEvent(ctfArena, flag, player);
                Bukkit.getPluginManager().callEvent(dropEvent);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onCTFLogout(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        Arena arena = arenas.getArena(player);
        if (arena != null && arena.getType().equals(ArenaType.CAPTURE_THE_FLAG))
        {
            CTFArena ctfArena = (CTFArena)arena;
            ArenaFlag flag = ctfArena.getFlag(player);
            if (flag != null)
            {
                Location position = flag.getGroundLocation(flag.getCurrentLocation());

                player.getInventory().setHelmet(flag.getHelmet());
                flag.setCarrier(null);
                flag.setHelmet(null);
                flag.placeBanner(position);
                flag.setCurrentLocation(position);
                player.removePotionEffect(PotionEffectType.GLOWING);

                FlagDropEvent dropEvent = new FlagDropEvent(ctfArena, flag, player);
                Bukkit.getPluginManager().callEvent(dropEvent);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onFlagEnding(ArenaEndingEvent event)
    {
        Arena arena = event.getArena();
        if (arena.getType().equals(ArenaType.CAPTURE_THE_FLAG))
        {
            CTFArena ctfArena = (CTFArena)arena;
            for (ArenaFlag flag : ctfArena.getFlags())
            {
                //Reset flag
                if (flag.getCarrier() != null)
                {
                    Player carrier = flag.getCarrier();
                    carrier.getInventory().setHelmet(flag.getHelmet());
                }
                flag.setCarrier(null);
                flag.setHelmet(null);
                flag.placeBanner(flag.getHomeLocation());
            }
        }
    }
}
