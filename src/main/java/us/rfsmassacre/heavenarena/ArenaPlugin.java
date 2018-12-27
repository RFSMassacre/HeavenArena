package us.rfsmassacre.heavenarena;

import us.rfsmassacre.heavenarena.commands.ArenaCommand;
import us.rfsmassacre.heavenarena.commands.QueueCommand;
import us.rfsmassacre.heavenarena.listeners.*;
import us.rfsmassacre.heavenarena.managers.ArenaManager;
import us.rfsmassacre.heavenarena.managers.QueueManager;
import us.rfsmassacre.heavenlib.managers.ConfigManager;
import us.rfsmassacre.heavenlib.managers.DependencyManager;
import us.rfsmassacre.heavenlib.managers.LocaleManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ArenaPlugin extends JavaPlugin
{
    private static ArenaPlugin plugin;
    public static ArenaPlugin getInstance()
    {
        return plugin;
    }

    private ConfigManager config;
    private LocaleManager locale;
    private DependencyManager dependency;

    private ArenaManager arenas;
    private QueueManager queue;

    @Override
    public void onEnable()
    {
        plugin = this;

        if (!this.getDataFolder().exists())
        {
            this.getDataFolder().mkdir();
        }

        this.config = new ConfigManager(this);
        this.locale = new LocaleManager(this);
        this.dependency = new DependencyManager(this);

        this.arenas = new ArenaManager(this);
        this.queue = new QueueManager();

        this.getCommand("arena").setExecutor(new ArenaCommand(this));
        this.getCommand("queue").setExecutor(new QueueCommand(this));

        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        this.getServer().getPluginManager().registerEvents(new QueueListener(this), this);
        this.getServer().getPluginManager().registerEvents(new ArenaListener(this), this);
        this.getServer().getPluginManager().registerEvents(new SkirmishListener(this), this);
        this.getServer().getPluginManager().registerEvents(new TDMListener(this), this);
        this.getServer().getPluginManager().registerEvents(new CTFListener(this), this);
        this.getServer().getPluginManager().registerEvents(new KOTHListener(this), this);
        if (dependency.hasPlugin("MagicSpells"))
        {
            this.getServer().getPluginManager().registerEvents(new SpellListener(this), this);
        }
    }

    @Override
    public void onDisable()
    {
        arenas.storeArenas();
    }

    public void reloadPlugin()
    {
        config.reloadFiles();
        locale.reloadFiles();
    }

    public ConfigManager getConfigManager()
    {
        return config;
    }
    public LocaleManager getLocaleManager()
    {
        return locale;
    }
    public DependencyManager getDependencyManager()
    {
        return dependency;
    }

    public ArenaManager getArenaManager()
    {
        return arenas;
    }
    public QueueManager getQueueManager()
    {
        return queue;
    }
}
