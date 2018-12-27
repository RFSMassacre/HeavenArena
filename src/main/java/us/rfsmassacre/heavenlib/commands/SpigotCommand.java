package us.rfsmassacre.heavenlib.commands;

/*
 * SpigotCommand is structured to avoid using long
 * if-else chains and instead sets up a list of
 * sub-commands to cycle through when running.
 *
 * If the sub-command equals the argument it calls
 * for, then it runs the function to execute.
 */

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public abstract class SpigotCommand implements CommandExecutor
{
    private String pluginName;
    private String commandName;

    protected ArrayList<SubCommand> subCommands;

    public SpigotCommand(String pluginName, String commandName)
    {
        this.pluginName = pluginName;
        this.commandName = commandName;

        this.subCommands = new ArrayList<SubCommand>();
        //Remember to define the main command when extending
        //this class.
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (subCommands.isEmpty())
        {
            //All commands MUST have a main sub-command.
            return false;
        }
        else if (args.length == 0)
        {
            //If no arguments are given, always run the first sub-command.
            subCommands.get(0).execute(sender, args);
            return true;
        }
        else
        {
            //If arguments are given, cycle through the right one.
            //If none found, it'll give an error defined.
            for (SubCommand subCommand : subCommands)
            {
                if (subCommand.equals(args[0]))
                {
                    subCommand.execute(sender, args);
                    return true;
                }
            }
        }

        onInvalidArgs(sender);
        return true;
    }

    /*
     * Define what to run when player has invalid arguments.
     */
    protected abstract void onInvalidArgs(CommandSender sender);

    /*
     * Define what to run when player doesn't have permission.
     */
    protected abstract void onCommandFail(CommandSender sender);


    /*
     * SubCommand
     */
    protected abstract class SubCommand
    {
        protected String name;
        protected String permission;

        public SubCommand(String name)
        {
            //Ensures that permissions are set properly
            this.name = name;
            this.permission = pluginName + "." + commandName;
            if (!name.isEmpty())
            {
                this.permission = this.permission + "." + name;
            }
        }

        public boolean isConsole(CommandSender sender)
        {
            return !(sender instanceof Player);
        }

        public boolean equals(String commandName)
        {
            return name.equalsIgnoreCase(commandName);
        }

        public void execute(CommandSender sender, String[] args)
        {
            if (sender.hasPermission(this.permission))
                onCommandRun(sender, args);
            else
                onCommandFail(sender);
        }

        /*
         * Define what to run when player has permission.
         */
        protected abstract void onCommandRun(CommandSender sender, String[] args);
    }
}
