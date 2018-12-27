package us.rfsmassacre.heavenlib.managers;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

/*
 * STORAGE MANAGERS HANDLE STORING SIMPLE OBJECTS TO A FILE.
 * To be extended to define which simple object.
 */
public abstract class StorageManager extends Manager
{
    protected String fileName;
    protected YamlConfiguration file;

    public StorageManager(JavaPlugin instance, String fileName)
    {
        super(instance);
        this.fileName = fileName;

        reloadFile();
    }

    public void reloadFile()
    {
        File newFile = new File(instance.getDataFolder(), fileName);

        try
        {
            if (!newFile.exists())
            {
                newFile.createNewFile();
            }

            file = YamlConfiguration.loadConfiguration(newFile);
        }
        catch (IOException exception)
        {
            //Print error neatly on console
            exception.printStackTrace();
        }
    }

    public void writeFile()
    {
        File newFile = new File(instance.getDataFolder(), fileName);

        try
        {
            if (newFile.exists())
            {
                newFile.delete();
            }

            newFile.createNewFile();
            file.save(newFile);
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }
    }

    public void clearFile()
    {
        for (String key : file.getKeys(false))
        {
            file.set(key, null);
        }
    }
}
