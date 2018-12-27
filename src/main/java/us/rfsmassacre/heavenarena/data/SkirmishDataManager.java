package us.rfsmassacre.heavenarena.data;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import us.rfsmassacre.heavenarena.arenas.SkirmishArena;

import java.io.IOException;

public class SkirmishDataManager extends ArenaDataManager
{
    public SkirmishDataManager(JavaPlugin instance)
    {
        super(instance);
    }

    @Override
    protected void storeData(Object object, YamlConfiguration data) throws IOException
    {
        if (!(object instanceof SkirmishArena))
        {
            return;
        }

        SkirmishArena arena = (SkirmishArena)object;
        storeArenaData(arena, data);

        //Store Skirmish data
    }

    @Override
    protected Object loadData(YamlConfiguration data) throws IOException
    {
        return new SkirmishArena(loadArenaData(data));
    }
}
