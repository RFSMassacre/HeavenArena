package us.rfsmassacre.heavenlib.managers;

import org.bukkit.plugin.java.JavaPlugin;

public class DependencyManager extends Manager
{
    public DependencyManager(JavaPlugin instance)
    {
        super(instance);
    }

    public boolean hasPlugin(String pluginName)
    {
        //Easy way to check if needed plugin is enabled
        return instance.getServer().getPluginManager().isPluginEnabled(pluginName) &&
                instance.getServer().getPluginManager().getPlugin(pluginName) != null;
    }

    public String getServerVersion()
    {
        //Screw DogOnFire, server checking can be done in a few lines.
        String rawVersion = instance.getServer().getVersion();
        String mcVersion = rawVersion.substring(rawVersion.indexOf("("));
        String version = mcVersion.replace("(MC: ", "").replace(")", "");

        return version;
    }
}
