package us.rfsmassacre.heavenarena.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.rfsmassacre.heavenarena.ArenaPlugin;
import us.rfsmassacre.heavenarena.arenas.Arena;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaTeam;
import us.rfsmassacre.heavenarena.events.arena.ArenaLeaveEvent;
import us.rfsmassacre.heavenarena.events.queue.QueueJoinEvent;
import us.rfsmassacre.heavenarena.events.queue.QueueLeaveEvent;
import us.rfsmassacre.heavenarena.managers.ArenaManager;
import us.rfsmassacre.heavenarena.managers.QueueManager;
import us.rfsmassacre.heavenlib.commands.SpigotCommand;
import us.rfsmassacre.heavenlib.managers.LocaleManager;

public class QueueCommand extends SpigotCommand
{
    private LocaleManager locale;
    private ArenaManager arenas;
    private QueueManager queue;

    public QueueCommand(ArenaPlugin plugin)
    {
        super("heavenarena", "queue");

        this.locale = plugin.getLocaleManager();
        this.arenas = plugin.getArenaManager();
        this.queue = plugin.getQueueManager();

        subCommands.add(new JoinCommand());
        subCommands.add(new LeaveCommand());
        subCommands.add(new StartCommand());
    }

    @Override
    protected void onInvalidArgs(CommandSender sender)
    {
        locale.sendLocale(sender, "error.invalid-args");
    }
    @Override
    protected void onCommandFail(CommandSender sender)
    {
        locale.sendLocale(sender, "error.no-perms");
    }

    /*
     * Join Skirmish Queue
     */
    private class JoinCommand extends SubCommand
    {
        public JoinCommand()
        {
            super("join");
        }

        @Override
        protected void onCommandRun(CommandSender sender, String[] args)
        {
            if (!isConsole(sender))
            {
                Player player = (Player)sender;
                Arena arena = arenas.getArena(player);
                if (arena == null)
                {
                    if (!queue.isQueued(player))
                    {
                        if (queue.queuePlayer(player))
                        {
                            locale.sendLocale(sender, "game.joined.queue");

                            QueueJoinEvent event = new QueueJoinEvent(false);
                            Bukkit.getPluginManager().callEvent(event);
                            return;
                        }
                    }

                    locale.sendLocale(sender, "error.in-queue");
                    return;
                }

                locale.sendLocale(sender, "error.in-game");
                return;
            }

            locale.sendLocale(sender, "error.no-console");
        }
    }

    /*
     * Leave Queue/Game
     */
    private class LeaveCommand extends SubCommand
    {
        public LeaveCommand()
        {
            super("leave");
        }

        @Override
        protected void onCommandRun(CommandSender sender, String[] args)
        {
            if (!isConsole(sender))
            {
                Player player = (Player)sender;

                if (queue.removePlayer(player))
                {
                    locale.sendLocale(sender, "game.left.queue");

                    QueueLeaveEvent event = new QueueLeaveEvent();
                    Bukkit.getPluginManager().callEvent(event);
                    return;
                }

                Arena arena = arenas.getArena(player);
                if (arena != null)
                {
                    ArenaTeam team = arena.getTeam(player);
                    if (team != null)
                    {
                        String typeName = arena.getType().toString();
                        locale.sendLocale(sender, "game.left.game", "{type}", locale.title(typeName));

                        team.removeMember(player);
                        player.teleport(arena.getExit());

                        ArenaLeaveEvent event = new ArenaLeaveEvent(player, arena);
                        Bukkit.getPluginManager().callEvent(event);
                        return;
                    }

                    locale.sendLocale(sender, "error.no-game");
                    return;
                }

                locale.sendLocale(sender, "error.no-queue");
                return;
            }

            locale.sendLocale(sender, "error.no-console");
        }
    }

    /*
     * Force Start
     */
    private class StartCommand extends SubCommand
    {
        public StartCommand()
        {
            super("start");
        }

        @Override
        protected void onCommandRun(CommandSender sender, String[] args)
        {
            if (queue.getSize() > 1)
            {
                QueueJoinEvent event = new QueueJoinEvent(true);
                Bukkit.getPluginManager().callEvent(event);

                locale.sendLocale(sender, "game.force-start");
                return;
            }

            locale.sendLocale(sender, "error.no-players");
        }
    }
}
