package us.rfsmassacre.heavenarena.data;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import us.rfsmassacre.heavenarena.arenas.TDMArena;

import java.io.IOException;

public class TDMDataManager extends ArenaDataManager
{
    public TDMDataManager(JavaPlugin instance)
    {
        super(instance);
    }

    @Override
    protected void storeData(Object object, YamlConfiguration data) throws IOException
    {
        if (!(object instanceof TDMArena))
        {
            return;
        }

        TDMArena arena = (TDMArena)object;
        storeArenaData(arena, data);

        //Store Skirmish data
    }

    @Override
    protected Object loadData(YamlConfiguration data) throws IOException
    {
        return new TDMArena(loadArenaData(data));
    }
}
