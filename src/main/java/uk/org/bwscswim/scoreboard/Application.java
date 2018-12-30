/*
 * #%L
 * BWSC Scoreboard
 * %%
 * Copyright (C) 2018-2019 Bracknell and Wokingham Swimming Club (BWSC)
 * %%
 * This file is part of BWSC Scoreboard.
 *
 * BWSC Scoreboard is free software: you can redistribute it and/or modify
 * it under the terms of the LGNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BWSC Scoreboard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * LGNU Lesser General Public License for more details.
 *
 * You should have received a copy of the LGNU Lesser General Public License
 * along with BWSC Scoreboard.  If not, see <https://www.gnu.org/licenses/>.
 * #L%
 */
package uk.org.bwscswim.scoreboard;

import com.fazecast.jSerialComm.SerialPort;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;

@SpringBootApplication
public class Application
{
    private static SerialPort port;
    private static String dummyFilename;
    private static Boolean trace;

    @Bean
    public SerialPort port()
    {
        return port;
    }

    @Bean
    public String dummyFilename()
    {
        return dummyFilename;
    }

    @Bean
    public Boolean trace()
    {
        return trace;
    }

    public static void main(String[] args)
    {
        if (validArgs(args))
        {
            SpringApplication.run(Application.class, args);
        }
    }

    private static boolean validArgs(String[] args)
    {
        if (args.length == 0)
        {
            System.err.println("Add the port number to the end of the command. Use a negative number to add trace output.");
            return false;
        }

        if (args[0].startsWith("-test="))
        {
            port = listPorts()[0]; // so the beans exists
            trace = false;

            dummyFilename = args[0].substring("-test=".length());
            File file = new File(dummyFilename);
            return file.canRead();
        }
        else
        {
            dummyFilename = ""; // so the bean exists

            int portNumber;
            try
            {
                portNumber = Integer.parseInt(args[0]);
                if (portNumber < 0)
                {
                    trace = true;
                    portNumber = Math.abs(portNumber);
                }
                else
                {
                    trace = false;
                }
                SerialPort[] commPorts = listPorts();
                if (portNumber == 0 || --portNumber >= commPorts.length)
                {
                    throw new NumberFormatException("");
                }
                port = commPorts[portNumber];
                return true;
            }
            catch (NumberFormatException e)
            {
                System.err.println("Invalid port number: " + args[0]);
                return false;
            }
        }
    }

    private static SerialPort[] listPorts()
    {
        SerialPort[] commPorts = SerialPort.getCommPorts();
        for (int i = 0; i < commPorts.length; i++)
        {
            System.out.println((i+1) + ". " + commPorts[i].getPortDescription());
        }
        return commPorts;
    }
}
