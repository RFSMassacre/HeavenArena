package us.rfsmassacre.heavenlib.managers;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class TextManager extends Manager
{
    private String fileName;
    private ArrayList<String> lines;

    public TextManager(JavaPlugin instance, String fileName)
    {
        super(instance);
        this.fileName = fileName;
        this.lines = loadTextFile();
    }

    public String getText()
    {
        return String.join("\n", this.lines);
    }
    public ArrayList<String> getTextLines()
    {
        return this.lines;
    }

    private ArrayList<String> loadTextFile()
    {
        try
        {
            InputStream is = instance.getResource(fileName);
            BufferedReader bfReader = new BufferedReader(new InputStreamReader(is));

            ArrayList<String> lines = new ArrayList<String>();
            String line;

            while ((line = bfReader.readLine()) != null)
            {
                lines.add(line);
            }

            return lines;
        }
        catch (IOException exception)
        {
            //Print error on console neatly
            exception.printStackTrace();
        }

        return null;
    }
}
