package us.rfsmassacre.heavenarena.commands;

import com.faris.Cuboid;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import us.rfsmassacre.heavenarena.ArenaPlugin;
import us.rfsmassacre.heavenarena.arenas.*;
import us.rfsmassacre.heavenarena.arenas.enums.*;
import us.rfsmassacre.heavenarena.managers.ArenaManager;
import us.rfsmassacre.heavenlib.commands.SpigotCommand;
import us.rfsmassacre.heavenlib.managers.ConfigManager;
import us.rfsmassacre.heavenlib.managers.LocaleManager;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;

/*
 * Arena Management Command
 *
 * This command is meant to be used to create and edit arenas.
 * There should be only one arena per type since NH is small.
 */
public class ArenaCommand extends SpigotCommand
{
    private ArenaPlugin plugin;
    private ConfigManager config;
    private LocaleManager locale;

    private ArenaManager arenas;

    public ArenaCommand(ArenaPlugin plugin)
    {
        super("heavenarena", "arena");

        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        this.locale = plugin.getLocaleManager();

        this.arenas = plugin.getArenaManager();

        subCommands.add(new VersionCommand());
        subCommands.add(new InfoCommand());
        subCommands.add(new ReloadCommand());
        subCommands.add(new CreateCommand());
        subCommands.add(new DeleteCommand());
        subCommands.add(new ListCommand());
        subCommands.add(new RegionCommand());
        subCommands.add(new PhaseCommand());
        subCommands.add(new ExitCommand());
        subCommands.add(new TeamCreateCommand());
        subCommands.add(new TeamDeleteCommand());
        subCommands.add(new TeamLobbyCommand());
        subCommands.add(new TeamSpawnCommand());
        subCommands.add(new TeamSizeCommand());
        subCommands.add(new FlagPoleCommand());
        subCommands.add(new PointRegionCommand());
        subCommands.add(new PointMeterCommand());
    }

    @Override
    protected void onInvalidArgs(CommandSender sender)
    {
        locale.sendLocale(sender, "error.invalid-args", "{command}", "arena");
    }
    @Override
    protected void onCommandFail(CommandSender sender)
    {
        locale.sendLocale(sender, "error.no-perms");
    }

    /*
     * Version Command
     */
    private class VersionCommand extends SubCommand
    {
        public VersionCommand()
        {
            super("version");
        }

        protected void onCommandRun(CommandSender sender, String[] args)
        {
            locale.sendLocale(sender, "arena.info");
        }
    }

    /*
     * Info Command
     */
    private class InfoCommand extends SubCommand
    {
        public InfoCommand()
        {
            super("info");
        }

        protected void onCommandRun(CommandSender sender, String[] args)
        {
            //arena info <arena>
            if (args.length >= 2)
            {
                Arena arena = arenas.getArena(args[1]);
                if (arena != null)
                {
                    String name = arena.getName();
                    String type = arena.getType().toString();
                    String phase = arena.getPhase().toString();

                    locale.sendLocale(sender, "info", "{arena}", name, "{type}", type,
                            "{phase}", phase);
                    return;
                }
            }

            locale.sendLocale(sender, "arena.invalid-arena");
        }
    }

    /*
     * Reload Command
     */
    private class ReloadCommand extends SubCommand
    {
        public ReloadCommand()
        {
            super("reload");
        }

        @Override
        protected void onCommandRun(CommandSender sender, String[] args)
        {
            plugin.reloadPlugin();
            locale.sendLocale(sender, "arena.reloaded");
        }
    }

    /*
     * Create Command
     */
    private class CreateCommand extends SubCommand
    {
        public CreateCommand()
        {
            super("create");
        }

        protected void onCommandRun(CommandSender sender, String[] args)
        {
            //arena create <name> <type>
            if (args.length >= 3)
            {
                String name = args[1];
                ArenaType type = ArenaType.fromString(args[2]);
                if (type != null)
                {
                    arenas.createArena(new Arena(name, type, null, null));
                    locale.sendLocale(sender, "arena.create-successful");
                    return;
                }

                locale.sendLocale(sender, "arena.invalid-type");
            }

            locale.sendLocale(sender, "arena.invalid-arena");
        }
    }

    /*
     * Delete Command
     */
    private class DeleteCommand extends SubCommand
    {
        public DeleteCommand()
        {
            super("delete");
        }

        @Override
        protected void onCommandRun(CommandSender sender, String[] args)
        {
            //arena delete <name>
            if (args.length >= 2)
            {
                String name = args[1];
                Arena arena = arenas.getArena(name);
                if (arena != null)
                {
                    arenas.removeArena(arena);
                    locale.sendLocale(sender, "arena.delete-successful");
                    return;
                }

                locale.sendLocale(sender, "arena.invalid-arena");
            }

            locale.sendLocale(sender, "arena.invalid-arena");
        }
    }

    /*
     * List Command
     */
    private class ListCommand extends SubCommand
    {
        public ListCommand()
        {
            super("list");
        }

        protected void onCommandRun(CommandSender sender, String[] args)
        {
            //arena list
            if (arenas.getArenas().size() > 0)
            {
                ArrayList<String> arenaNames = new ArrayList<String>();
                for (Arena arena : arenas.getArenas())
                {
                    arenaNames.add(arena.getName());
                }
                String arenaString = String.join("&f,&7 ", arenaNames);

                locale.sendLocale(sender, "list.all-arenas", "{arenas}", arenaString);
                return;
            }

            locale.sendLocale(sender, "list.no-arenas");
        }
    }

    /*
     * Region Command
     */
    private class RegionCommand extends SubCommand
    {
        private HashMap<String, Location> positions;

        public RegionCommand()
        {
            super("region");

            this.positions = new HashMap<String, Location>();
        }

        @Override
        protected void onCommandRun(CommandSender sender, String[] args)
        {
            if (!isConsole(sender))
            {
                Player player = (Player)sender;

                //arena region <name> <position>
                if (args.length >= 3)
                {
                    String name = args[1];
                    Arena arena = arenas.getArena(name);
                    if (arena != null)
                    {
                        try
                        {
                            int position = Integer.parseInt(args[2]);
                            Location location = player.getLocation();

                            switch (position)
                            {
                                case 1:
                                    positions.put(arena.getName(), location);
                                    locale.sendLocale(sender, "arena.position.position1-successful");
                                    return;
                                case 2:
                                    if (positions.containsKey(arena.getName()))
                                    {
                                        Location location1 = positions.get(arena.getName());
                                        arena.setRegion(new Cuboid(location1, location));
                                        locale.sendLocale(sender, "arena.position.position2-successful");
                                        positions.remove(arena.getName());
                                        return;
                                    }
                                    else
                                    {
                                        locale.sendLocale(sender, "arena.position.position2-failed");
                                        return;
                                    }
                                default:
                                    locale.sendLocale(sender, "arena.position.invalid-position");
                                    return;
                            }
                        }
                        catch (NumberFormatException exception)
                        {
                            locale.sendLocale(sender, "arena.position.invalid-position");
                            return;
                        }
                    }

                    locale.sendLocale(sender, "arena.position.invalid-args");
                    return;
                }

                locale.sendLocale(sender, "arena.position.invalid-position");
                return;
            }

            locale.sendLocale(sender, "error.no-console");
        }
    }

    /*
     * Phase Command
     */
    private class PhaseCommand extends SubCommand
    {
        public PhaseCommand()
        {
            super("phase");
        }

        @Override
        protected void onCommandRun(CommandSender sender, String[] args)
        {
            //arena phase <arena> <phase>
            if (args.length >= 3)
            {
                Arena arena = arenas.getArena(args[1]);
                if (arena != null)
                {
                    try
                    {
                        ArenaPhase phase = ArenaPhase.valueOf(args[2].toUpperCase());
                        arena.setPhase(phase);

                        locale.sendLocale(sender, "arena.phase.successful", "{phase}", phase.toString());
                        return;
                    }
                    catch (IllegalArgumentException exception)
                    {
                        locale.sendLocale(sender, "arena.phase.invalid-phase");
                        return;
                    }
                }

                locale.sendLocale(sender, "arena.invalid-arena");
                return;
            }

            locale.sendLocale(sender, "arena.phase.invalid-args");
        }
    }

    /*
     * Exit Command
     */
    private class ExitCommand extends SubCommand
    {
        public ExitCommand()
        {
            super("exit");
        }

        @Override
        protected void onCommandRun(CommandSender sender, String[] args)
        {
            if (!isConsole(sender))
            {
                Player player = (Player)sender;

                //arena exit <arena>
                if (args.length >= 2)
                {
                    Arena arena = arenas.getArena(args[1]);
                    if (arena != null)
                    {
                        arena.setExit(player.getLocation());
                        locale.sendLocale(sender, "arena.exit.successful");
                        return;
                    }

                    locale.sendLocale(sender, "arena.invalid-arena");
                    return;
                }

                locale.sendLocale(sender, "arena.exit.invalid-args");
                return;
            }

            locale.sendLocale(sender, "error.no-console");
        }
    }

    /*
     * Team Create Command
     */
    private class TeamCreateCommand extends SubCommand
    {
        public TeamCreateCommand()
        {
            super("teamcreate");
        }

        @Override
        protected void onCommandRun(CommandSender sender, String[] args)
        {
            //arena teamcreate <arena> <color> <alt>
            if (args.length >= 4)
            {
                Arena arena = arenas.getArena(args[1]);
                if (arena != null)
                {
                    try
                    {
                        ChatColor color = ChatColor.valueOf(args[2].toUpperCase());
                        ChatColor alt = ChatColor.valueOf(args[3].toUpperCase());
                        ArenaTeam team = new ArenaTeam(color, alt,null, null);
                        arena.addTeam(team);

                        locale.sendLocale(sender, "team.create.successful", "{team}", team.getName());
                        return;
                    }
                    catch (IllegalArgumentException exception)
                    {
                        locale.sendLocale(sender,"team.invalid-color");
                        return;
                    }
                }

                locale.sendLocale(sender, "arena.invalid-arena");
                return;
            }

            locale.sendLocale(sender, "team.create.invalid-args");
        }
    }

    /*
     * Team Delete Command
     */
    private class TeamDeleteCommand extends SubCommand
    {
        public TeamDeleteCommand()
        {
            super("teamdelete");
        }

        @Override
        protected void onCommandRun(CommandSender sender, String[] args)
        {
            //arena teamdelete <arena> <color>
            if (args.length >= 3)
            {
                Arena arena = arenas.getArena(args[1]);
                if (arena != null)
                {
                    try
                    {
                        ChatColor color = ChatColor.valueOf(args[2].toUpperCase());
                        ArenaTeam team = arena.getTeam(color);
                        if (team != null)
                        {
                            arena.removeTeam(color);

                            locale.sendLocale(sender, "team.delete.successful", "{team}", team.getName());
                            return;
                        }

                        locale.sendLocale(sender, "team.invalid-team");
                        return;
                    }
                    catch (IllegalArgumentException exception)
                    {
                        locale.sendLocale(sender, "team.invalid-color");
                        return;
                    }
                }
            }

            locale.sendLocale(sender, "team.delete.invalid-args");
        }
    }

    /*
     * Team Lobby Command
     */
    private class TeamLobbyCommand extends SubCommand
    {
        private HashMap<String, HashMap<ChatColor, Location>> teamPositions;

        public TeamLobbyCommand()
        {
            super("teamlobby");

            this.teamPositions = new HashMap<String, HashMap<ChatColor, Location>>();
        }

        @Override
        protected void onCommandRun(CommandSender sender, String[] args)
        {
            if (!isConsole(sender))
            {
                Player player = (Player)sender;

                //arena teamlobby <arena> <color> <position>
                if (args.length >= 4)
                {
                    Arena arena = arenas.getArena(args[1]);
                    if (arena != null)
                    {
                        try
                        {
                            ChatColor color = ChatColor.valueOf(args[2].toUpperCase());
                            int position = Integer.parseInt(args[3]);

                            ArenaTeam team = arena.getTeam(color);
                            if (team != null)
                            {
                                Location location = player.getLocation();
                                HashMap<ChatColor, Location> teamPosition = teamPositions.get(arena.getName());
                                if (teamPosition == null)
                                {
                                    //First position is equal to 1 or less
                                    if (position <= 1)
                                    {
                                        teamPosition = new HashMap<ChatColor, Location>();
                                        teamPosition.put(color, location);
                                        teamPositions.put(arena.getName(), teamPosition);

                                        locale.sendLocale(sender, "arena.position.position1-successful");
                                        return;
                                    }
                                    //Second position is equal to 2 or more
                                    else
                                    {
                                        locale.sendLocale(sender, "arena.position.position2-failed");
                                        return;
                                    }
                                }
                                else
                                {
                                    if (position <= 1)
                                    {
                                        teamPosition.put(color, location);
                                        teamPositions.put(arena.getName(), teamPosition);

                                        locale.sendLocale(sender, "arena.position.position1-successful");
                                        return;
                                    }
                                    else
                                    {
                                        Location location1 = teamPosition.get(color);
                                        team.setLobby(new Cuboid(location1, location));
                                        teamPosition.remove(color);
                                        if (teamPosition.isEmpty())
                                        {
                                            teamPositions.remove(arena.getName());
                                        }

                                        locale.sendLocale(sender, "team.lobby.successful", "{team}", team.getName());
                                        return;
                                    }
                                }
                            }

                            locale.sendLocale(sender, "team.invalid-team");
                            return;
                        }
                        catch (NumberFormatException exception)
                        {
                            locale.sendLocale(sender, "arena.invalid-position");
                            return;
                        }
                        catch (IllegalArgumentException exception)
                        {
                            locale.sendLocale(sender, "team.invalid-color");
                            return;
                        }
                    }

                    locale.sendLocale(sender, "arena.invalid-arena");
                    return;
                }

                locale.sendLocale(sender, "team.lobby.invalid-args");
                return;
            }

            locale.sendLocale(sender, "error.no-console");
        }
    }

    /*
     * Team Spawn Command
     */
    private class TeamSpawnCommand extends SubCommand
    {
        public TeamSpawnCommand()
        {
            super("teamspawn");
        }

        @Override
        protected void onCommandRun(CommandSender sender, String[] args)
        {
            if (!isConsole(sender))
            {
                Player player = (Player)sender;

                //arena teamspawn <arena> <color>
                if (args.length >= 3)
                {
                    Arena arena = arenas.getArena(args[1]);
                    if (arena != null)
                    {
                        try
                        {
                            ChatColor color = ChatColor.valueOf(args[2].toUpperCase());
                            ArenaTeam team = arena.getTeam(color);
                            if (team != null)
                            {
                                team.setSpawn(player.getLocation());

                                locale.sendLocale(sender, "team.spawn.successful", "{team}", team.getName());
                                return;
                            }

                            locale.sendLocale(sender, "team.invalid-team");
                            return;
                        }
                        catch (IllegalArgumentException exception)
                        {
                            locale.sendLocale(sender, "team.invalid-color");
                            return;
                        }
                    }

                    locale.sendLocale(sender, "arena.invalid-arena");
                    return;
                }

                locale.sendLocale(sender, "team.spawn.invalid-args");
                return;
            }

            locale.sendLocale(sender, "error.no-console");
        }
    }

    /*
     * Team Size Command
     */
    private class TeamSizeCommand extends SubCommand
    {
        public TeamSizeCommand()
        {
            super("teamsize");
        }

        @Override
        protected void onCommandRun(CommandSender sender, String[] args)
        {
            //arena teamsize <arena> <color> <min> <max>
            if (args.length >= 5)
            {
                Arena arena = arenas.getArena(args[1]);
                if (arena != null)
                {
                    try
                    {
                        ChatColor color = ChatColor.valueOf(args[2].toUpperCase());
                        ArenaTeam team = arena.getTeam(color);
                        if (team != null)
                        {
                            int min = Integer.parseInt(args[3]);
                            int max = Integer.parseInt(args[4]);

                            team.setMinMembers(min);
                            team.setMaxMembers(max);

                            locale.sendLocale(sender, "team.size.successful", "{team}", team.getName());
                            return;
                        }

                        locale.sendLocale(sender, "team.invalid-team");
                        return;
                    }
                    catch (NumberFormatException exception)
                    {
                        locale.sendLocale(sender, "team.size.invalid-size");
                        return;
                    }
                    catch (IllegalArgumentException exception)
                    {
                        locale.sendLocale(sender, "team.invalid-color");
                        return;
                    }
                }

                locale.sendLocale(sender, "arena.invalid-arena");
                return;
            }

            locale.sendLocale(sender, "team.size.invalid-args");
        }
    }

    /*
     * CTF - Flag Poles
     */
    private class FlagPoleCommand extends SubCommand
    {
        public FlagPoleCommand()
        {
            super("flagpole");
        }

        @Override
        protected void onCommandRun(CommandSender sender, String[] args)
        {
            if (!isConsole(sender))
            {
                Player player = (Player)sender;

                //arena flagpole <arena> <color>
                if (args.length >= 3)
                {
                    Arena arena = arenas.getArena(args[1]);
                    if (arena != null)
                    {
                        if (!arena.getType().equals(ArenaType.CAPTURE_THE_FLAG))
                        {
                            locale.sendLocale(sender, "arena.invalid-state", "{type}", "Capture The Flag");
                            return;
                        }

                        try
                        {
                            CTFArena ctfArena = (CTFArena)arena;
                            ChatColor color = ChatColor.valueOf(args[2].toUpperCase());
                            ArenaTeam team = ctfArena.getTeam(color);
                            if (team != null)
                            {
                                ctfArena.setFlag(color, new ArenaFlag(color, player.getLocation()));

                                locale.sendLocale(sender, "team.flag.successful", "{team}", team.getName());
                                return;
                            }

                            locale.sendLocale(sender, "team.invalid-team");
                            return;
                        }
                        catch (IllegalArgumentException exception)
                        {
                            locale.sendLocale(sender, "team.invalid-color");
                            return;
                        }
                    }

                    locale.sendLocale(sender, "arena.invalid-arena");
                    return;
                }

                locale.sendLocale(sender, "team.flag.invalid-args");
                return;
            }

            locale.sendLocale(sender, "error.no-console");
        }
    }

    /*
     * KOTH - Point Region
     */
    private class PointRegionCommand extends SubCommand
    {
        private HashMap<String, Location> positions;

        public PointRegionCommand()
        {
            super("pointregion");

            this.positions = new HashMap<String, Location>();
        }

        @Override
        protected void onCommandRun(CommandSender sender, String[] args)
        {
            if (!isConsole(sender))
            {
                Player player = (Player)sender;

                //arena pointregion <arena> <position>
                if (args.length >= 3)
                {
                    String name = args[1];
                    Arena arena = arenas.getArena(name);
                    if (arena != null)
                    {
                        if (!arena.getType().equals(ArenaType.KING_OF_THE_HILL))
                        {
                            locale.sendLocale(sender, "arena.invalid-state", "{type}", "King Of The Hill");
                            return;
                        }

                        try
                        {
                            KOTHArena kothArena = (KOTHArena)arena;

                            int position = Integer.parseInt(args[2]);
                            Location location = player.getLocation();

                            switch (position)
                            {
                                case 1:
                                    positions.put(arena.getName(), location);
                                    locale.sendLocale(sender, "arena.position.position1-successful");
                                    return;
                                case 2:
                                    if (positions.containsKey(arena.getName()))
                                    {
                                        Location location1 = positions.get(arena.getName());
                                        Cuboid region = new Cuboid(location1, location);
                                        kothArena.setPoint(new ArenaPoint(region));
                                        locale.sendLocale(sender, "arena.position.position2-successful");
                                        positions.remove(arena.getName());
                                        return;
                                    }
                                    else
                                    {
                                        locale.sendLocale(sender, "arena.position.position2-failed");
                                        return;
                                    }
                                default:
                                    locale.sendLocale(sender, "arena.position.invalid-position");
                                    return;
                            }
                        }
                        catch (NumberFormatException exception)
                        {
                            locale.sendLocale(sender, "arena.position.invalid-position");
                            return;
                        }
                    }

                    locale.sendLocale(sender, "arena.invalid-arena");
                    return;
                }

                locale.sendLocale(sender, "arena.position.invalid-position");
                return;
            }

            locale.sendLocale(sender, "error.no-console");
        }
    }

    /*
     * KOTH - Point Max Meter
     */
    private class PointMeterCommand extends SubCommand
    {
        public PointMeterCommand()
        {
            super("pointmeter");
        }

        @Override
        protected void onCommandRun(CommandSender sender, String[] args)
        {
            //arena pointmeter <arena> <int>
            if (args.length >= 3)
            {
                Arena arena = arenas.getArena(args[1]);
                if (arena != null)
                {
                    if (!arena.getType().equals(ArenaType.KING_OF_THE_HILL))
                    {
                        locale.sendLocale(sender, "arena.invalid-state", "{type}", "King Of The Hill");
                        return;
                    }

                    try
                    {
                        KOTHArena kothArena = (KOTHArena)arena;

                        int seconds = Integer.parseInt(args[2]);
                        if (seconds > 0)
                        {
                            ArenaPoint point = kothArena.getPoint();
                            if (point != null)
                            {
                                point.setMaxMeter(seconds);

                                locale.sendLocale(sender, "arena.point.successful");
                                return;
                            }

                            locale.sendLocale(sender, "arena.point.no-point");
                            return;
                        }

                        locale.sendLocale(sender, "arena.invalid-seconds");
                        return;
                    }
                    catch (NumberFormatException exception)
                    {
                        locale.sendLocale(sender, "arena.point.invalid-args");
                        return;
                    }
                }

                locale.sendLocale(sender, "arena.invalid-arena");
                return;
            }

            locale.sendLocale(sender, "arena.point.invalid-args");
        }
    }
}