package uk.org.bwscswim.scoreboard;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * @author adavis
 */
public class FileLoader
{
    public static BufferedReader getBufferedReader(String filename, Config config) throws FileNotFoundException
    {
        try
        {
            InputStreamReader inputStreamReader;
            if (filename.startsWith(":"))
            {
                String resourceName = filename.substring(1);
                URL resource = FileLoader.class.getClassLoader().getResource(resourceName);
                if (resource == null)
                {
                    throw new FileNotFoundException("Resource "+resourceName+" does not exit.");
                }
                inputStreamReader = new InputStreamReader(resource.openStream());
            }
            else
            {
                String baseDir = config.getString("baseDir", ".");
                baseDir += baseDir.endsWith("/") ? "" : "/";
                inputStreamReader = new FileReader(baseDir+filename);
            }
            return new BufferedReader(inputStreamReader);
        }
        catch (IOException e)
        {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    public static byte[] getBytes(String filename, Config config) throws FileNotFoundException
    {
        try
        {
            InputStream is;
            if (filename.startsWith(":"))
            {
                String resourceName = filename.substring(1);
                URL resource = FileLoader.class.getClassLoader().getResource(resourceName);
                if (resource == null)
                {
                    throw new FileNotFoundException("Resource "+resourceName+" does not exit.");
                }
                is = resource.openStream();
            }
            else
            {
                String baseDir = config.getString("baseDir", ".");
                baseDir += baseDir.endsWith("/") ? "" : "/";
                is = new FileInputStream(baseDir+filename);
            }
            BufferedInputStream inputStream = new BufferedInputStream(is);
            int size = inputStream.available();
            byte[] bytes = new byte[size];
            inputStream.read(bytes, 0, size);
            return bytes;
        }
        catch (IOException e)
        {
            throw new FileNotFoundException(e.getMessage());
        }
    }
}
