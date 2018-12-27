package us.rfsmassacre.heavenarena.scoreboards;

import org.bukkit.ChatColor;

public class TeamScore implements Comparable<TeamScore>
{
    private ChatColor color;
    private int score;

    public TeamScore(ChatColor color, int score)
    {
        this.color = color;
        this.score = score;
    }

    @Override
    public int compareTo(TeamScore otherScore)
    {
        return otherScore.getScore() - this.score;
    }

    public ChatColor getColor()
    {
        return color;
    }

    public void setScore(int score)
    {
        this.score = score;
    }
    public int getScore()
    {
        return score;
    }
}
