package us.rfsmassacre.heavenarena.arenas.enums;

/*
 * Determines what gamemode the arena will be.
 */
public enum ArenaType
{
    SKIRMISH("SK"),
    TEAM_DEATHMATCH("TDM"),
    KING_OF_THE_HILL("KOTH"),
    CAPTURE_THE_FLAG("CTF");

    private String alias;

    ArenaType(String alias)
    {
        this.alias = alias;
    }

    //Easily convert string into proper enum.
    public static ArenaType fromString(String string)
    {
        for (ArenaType type : ArenaType.values())
        {
            if (string.equalsIgnoreCase(type.name())
                    || string.equalsIgnoreCase(type.alias))
            {
                return type;
            }
        }

        return null;
    }
}
