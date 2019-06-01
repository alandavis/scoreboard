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

import java.io.File;

import static com.fazecast.jSerialComm.SerialPort.FLOW_CONTROL_DISABLED;
import static com.fazecast.jSerialComm.SerialPort.NO_PARITY;
import static com.fazecast.jSerialComm.SerialPort.ONE_STOP_BIT;
import static com.fazecast.jSerialComm.SerialPort.TIMEOUT_READ_BLOCKING;

public class Args
{
    private SerialPort port = SerialPort.getCommPort("COM4");
    private String testFilename = "";
    private boolean testLoop = false;
    private boolean trace = false;

    private int f0 = 0;
    private int f1 = 3;
    private int f2 = 20;
    private int f3 = 25;
    private int f4 = 34;

    public SerialPort getPort()
    {
        return port;
    }

    public String getTestFilename()
    {
        return testFilename;
    }

    public void setTrace(boolean trace)
    {
        this.trace = trace;
    }

    public boolean isTestLoop()
    {
        return testLoop;
    }

    public boolean isTrace()
    {
        return trace;
    }

    public int getF0()
    {
        return f0;
    }

    public int getF1()
    {
        return f1;
    }

    public int getF2()
    {
        return f2;
    }

    public int getF3()
    {
        return f3;
    }

    public int getF4()
    {
        return f4;
    }

    public void check(String[] args)
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
                testFilename = ":test1.txt";
            }
            else if (arg.equals("-test2"))
            {
                testFilename = ":test2.txt";
            }
            else if (arg.startsWith("-testFile="))
            {
                testFilename = arg.substring("-testFile=".length());
                File file = new File(testFilename);
                if (!file.canRead())
                {
                    error("Cannot read test file: "+ testFilename);
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
            else if (arg.equalsIgnoreCase("-testLoop"))
            {
                testLoop = true;
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
            else if (arg.startsWith("-f0="))
            {
                f0 = parseInt(arg, "f0");
            }
            else if (arg.startsWith("-f1="))
            {
                f1 = parseInt(arg, "f1");
            }
            else if (arg.startsWith("-f2="))
            {
                f2 = parseInt(arg, "f2");
            }
            else if (arg.startsWith("-f3="))
            {
                f3 = parseInt(arg, "f3");
            }
            else if (arg.startsWith("-f4="))
            {
                f4 = parseInt(arg, "f4");
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
