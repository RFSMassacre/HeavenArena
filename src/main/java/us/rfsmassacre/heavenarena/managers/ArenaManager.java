package us.rfsmassacre.heavenarena.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import us.rfsmassacre.heavenarena.ArenaPlugin;
import us.rfsmassacre.heavenarena.arenas.*;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaPhase;
import us.rfsmassacre.heavenarena.arenas.enums.ArenaTeam;
import us.rfsmassacre.heavenarena.data.*;

import java.io.File;
import java.util.HashSet;
import java.util.UUID;

/*
 * Manages the loading and saving of arenas.
 */
public class ArenaManager
{
    private ArenaDataManager arenaData;
    private SkirmishDataManager skirmishData;
    private TDMDataManager tdmData;
    private CTFDataManager ctfData;
    private KOTHDataManager kothData;

    private HashSet<Arena> arenas;

    public ArenaManager(ArenaPlugin plugin)
    {
        this.arenaData = new ArenaDataManager(plugin);
        this.skirmishData = new SkirmishDataManager(plugin);
        this.tdmData = new TDMDataManager(plugin);
        this.ctfData = new CTFDataManager(plugin);
        this.kothData = new KOTHDataManager(plugin);

        this.arenas = new HashSet<Arena>();

        loadArenas();
    }

    public void createArena(Arena arena)
    {
        switch (arena.getType())
        {
            case SKIRMISH:
                arenas.add(new SkirmishArena(arena));
                break;
            case TEAM_DEATHMATCH:
                arenas.add(new TDMArena(arena));
                break;
            case CAPTURE_THE_FLAG:
                arenas.add(new CTFArena(arena));
                break;
            case KING_OF_THE_HILL:
                arenas.add(new KOTHArena(arena));
                break;
            default:
                arenas.add(arena);
                break;
        }
    }
    public void addArena(Arena arena)
    {
        arenas.add(arena);
        arenaData.saveToFile(arena, arena.getName());
    }
    public void removeArena(Arena arena)
    {
        arenas.remove(arena);
        arenaData.deleteFile(arena.getName());
    }
    public void clearArenas()
    {
        //Kick all players then clear the arenas
        for (Arena arena : arenas)
        {
            for (ArenaTeam team : arena.getTeams())
            {
                for (Player player : team.getMembers())
                {
                    player.teleport(arena.getExit());
                }
            }
        }

        arenas.clear();
    }
    public Arena getArena(String name)
    {
        for (Arena arena : arenas)
        {
            if (arena.getName().equals(name))
            {
                return arena;
            }
        }

        return null;
    }
    public HashSet<Arena> getArenas()
    {
        return arenas;
    }

    /*
     * Ensure that arenas save and load from the proper data manager.
     */
    public void loadArenas()
    {
        for (File file : arenaData.listFiles())
        {
            Arena arena = (Arena)arenaData.loadFromFile(file);
            switch (arena.getType())
            {
                case SKIRMISH:
                    SkirmishArena skirmishArena = (SkirmishArena)skirmishData.loadFromFile(file);
                    arenas.add(skirmishArena);
                    if (isValid(skirmishArena))
                    {
                        skirmishArena.setPhase(ArenaPhase.OPEN);
                    }
                    break;
                case TEAM_DEATHMATCH:
                    TDMArena tdmArena = (TDMArena)tdmData.loadFromFile(file);
                    arenas.add(tdmArena);
                    if (isValid(tdmArena))
                    {
                        tdmArena.setPhase(ArenaPhase.OPEN);
                    }
                    break;
                case CAPTURE_THE_FLAG:
                    CTFArena ctfArena = (CTFArena)ctfData.loadFromFile(file);
                    arenas.add(ctfArena);
                    if (isValid(ctfArena))
                    {
                        ctfArena.setPhase(ArenaPhase.OPEN);
                    }
                    break;
                case KING_OF_THE_HILL:
                    KOTHArena kothArena = (KOTHArena)kothData.loadFromFile(file);
                    arenas.add(kothArena);
                    if (isValid(kothArena))
                    {
                        kothArena.setPhase(ArenaPhase.OPEN);
                    }
                    break;
                default:
                    arenas.add(arena);
                    break;
            }
        }
    }
    public void storeArenas()
    {
        for (Arena arena : arenas)
        {
            switch (arena.getType())
            {
                case SKIRMISH:
                    skirmishData.saveToFile((SkirmishArena)arena, arena.getName());
                    break;
                case TEAM_DEATHMATCH:
                    tdmData.saveToFile((TDMArena)arena, arena.getName());
                    break;
                case CAPTURE_THE_FLAG:
                    ctfData.saveToFile((CTFArena)arena, arena.getName());
                    break;
                case KING_OF_THE_HILL:
                    kothData.saveToFile((KOTHArena)arena, arena.getName());
                    break;
                default:
                    arenaData.saveToFile(arena, arena.getName());
                    break;
            }
        }
    }

    /*
     * Player Checks
     */
    public Arena getArena(Player player)
    {
        for (Arena arena : arenas)
        {
            for (ArenaTeam team : arena.getTeams())
            {
                if (team.isMemberId(player.getUniqueId()))
                {
                    return arena;
                }
            }
        }

        return null;
    }

    /*
     * Validity Checks
     */
    public boolean isValid(KOTHArena arena)
    {
        if (arena.getName() != null && arena.getType() != null && arena.getTeams() != null
                && !arena.getTeams().isEmpty() && arena.getRegion() != null && arena.getExit() != null
                && arena.getPoint() != null)
        {
            return true;
        }

        return false;
    }

    public boolean isValid(CTFArena arena)
    {
        if (arena.getName() != null && arena.getType() != null && arena.getTeams() != null
                && !arena.getTeams().isEmpty() && arena.getRegion() != null && arena.getExit() != null
                && arena.getFlags() != null && !arena.getFlags().isEmpty())
        {
            return true;
        }

        return false;
    }
    public boolean isValid(TDMArena arena)
    {
        if (arena.getName() != null && arena.getType() != null && arena.getTeams() != null
                && !arena.getTeams().isEmpty() && arena.getRegion() != null && arena.getExit() != null)
        {
            return true;
        }

        return false;
    }
    public boolean isValid(SkirmishArena arena)
    {
        if (arena.getName() != null && arena.getType() != null && arena.getTeams() != null
                && !arena.getTeams().isEmpty() && arena.getRegion() != null && arena.getExit() != null)
        {
            return true;
        }

        return false;
    }
}
