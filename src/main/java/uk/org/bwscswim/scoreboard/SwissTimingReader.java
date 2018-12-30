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

import java.io.IOException;
import java.io.InputStream;
import java.util.InputMismatchException;
import java.util.Scanner;

public class SwissTimingReader
{
    public static void main(String[] args)
    {
        SwissTimingReader reader = new SwissTimingReader();
        reader.logger();
    }

    public void logger()
    {
        SerialPort[] commPorts = SerialPort.getCommPorts();
        int i = 0;
        for (SerialPort commPort : commPorts)
        {
            System.out.println((i++) + ". " + commPort.getDescriptivePortName());
        }

        System.out.println("\nEnter the number of the port to use.");
        i = getInt(0, commPorts.length);

        SerialPort port = commPorts[i];
        System.out.println("Using "+i+" "+port);

        if (port.openPort())
        {
            try
            {
                InputStream inputStream = port.getInputStream();
                for (; ; )
                {
                    int b = inputStream.read();
                    if (b == -1)
                    {
                        break;
                    }
                    if (b != 0)
                    {
                        System.out.println("byte " + b);
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                port.closePort();
            }
        }
        else
        {
            System.err.println("The port failed to open for an unknown reason.");
        }
    }

    private int getInt(int min, int max)
    {
        int p;
        do
        {
            try
            {
                Scanner input = new Scanner(System.in);
                p = input.nextInt();
            }
            catch (InputMismatchException e)
            {
                p = -1;
            }
        } while (p < min || p >= max);
        return p;
    }
}
