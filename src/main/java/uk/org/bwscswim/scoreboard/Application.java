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

import static com.fazecast.jSerialComm.SerialPort.FLOW_CONTROL_DISABLED;
import static com.fazecast.jSerialComm.SerialPort.NO_PARITY;
import static com.fazecast.jSerialComm.SerialPort.ONE_STOP_BIT;
import static com.fazecast.jSerialComm.SerialPort.TIMEOUT_READ_BLOCKING;

@SpringBootApplication
public class Application
{
    private static SerialPort port = SerialPort.getCommPort("COM1");
    private static String dummyFilename = "";
    private static Boolean trace = false;

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

    public static void main(String[] args) // Spring reloads this class and runs this method again, so it gets called twice.
    {
        validArgs(args);
        SpringApplication.run(Application.class, args);
    }

    private static void validArgs(String[] args)
    {
        int baudRate = 19200;
        int dataBits = 8;
        int stopBits = ONE_STOP_BIT;
        int parity = NO_PARITY;
        int flow = FLOW_CONTROL_DISABLED;
        int timeoutMode = TIMEOUT_READ_BLOCKING;
        int readTimeout = 0;

        for (String arg: args)
        {
            if (arg.equals("-test1"))
            {
                dummyFilename = ":test1.txt";
            }
            else if (arg.startsWith("-testFile="))
            {
                dummyFilename = arg.substring("-testFile=".length());
                File file = new File(dummyFilename);
                if (!file.canRead())
                {
                    error("Cannot read test file: "+dummyFilename);
                }

            }
            else if (arg.startsWith("-port="))
            {
                String portName = arg.substring("-port=".length());
                port = SerialPort.getCommPort(portName);
                if (port.getPortDescription().equals("Bad Port"))
                {
                    error("Cannot read serial port: "+portName);
                }
            }
            else if (arg.equalsIgnoreCase("-trace"))
            {
                trace = true;
            }
            else if (arg.startsWith("-baudRate="))
            {
                baudRate = parseInt(arg, "baudRate");
            }
            else if (arg.startsWith("-dataBits="))
            {
                dataBits = parseInt(arg, "dataBits");
            }
            else if (arg.startsWith("-stopBits="))
            {
                stopBits = parseInt(arg, "stopBits");
            }
            else if (arg.startsWith("-parity="))
            {
                parity = parseInt(arg, "parity");
            }
            else if (arg.startsWith("-flow="))
            {
                flow = parseInt(arg, "flow");
            }
            else if (arg.startsWith("-timeoutMode="))
            {
                timeoutMode = parseInt(arg, "timeoutMode");
            }
            else if (arg.startsWith("-readTimeout="))
            {
                readTimeout = parseInt(arg, "readTimeout");
            }
        }

        port.setBaudRate(baudRate);
        port.setNumDataBits(dataBits);
        port.setNumStopBits(stopBits);
        port.setParity(parity);
        port.setFlowControl(flow);
        port.setComPortTimeouts(timeoutMode, readTimeout, 0);
    }

    private static int parseInt(String arg, String flagName)
    {
        String n = arg.substring(flagName.length()+2);
        try
        {
            return Integer.parseInt(n);
        }
        catch (NumberFormatException e)
        {
            String message = "Invalid " + flagName + ": " + n;
            error(message);
            return -1;
        }
    }

    private static void error(String message)
    {
        SerialPort[] commPorts = SerialPort.getCommPorts();
        for (int i = 0; i < commPorts.length; i++)
        {
            System.err.println((i+1) + ". " + commPorts[i].getPortDescription());
        }
        System.err.println("");
        System.err.println(message);
        System.exit(-1);
    }
}
