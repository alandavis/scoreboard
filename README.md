# This version has been mothballed in favor of a Java Application version.

### BWSC Scoreboard
BWSC Scoreboard is a Spring Boot application using [jSerialComm](http://fazecast.github.io/jSerialComm/) for connectivity to the SWISS TIMING equipment used by the [BWSC](https://www.bwscswim.org.uk/).

### Building and testing
The project can be built and tested by running Maven command:
~~~
mvn clean install
~~~

### Artifacts
**WIP:** The artifacts can be obtained by:
* downloading from ...
* getting as Maven dependency by adding the dependency to your pom file:
~~~
<dependency>
  <groupId>uk.org.bwscswim</groupId>
  <artifactId>scoreboard</artifactId>
  <version>version</version>
</dependency>
~~~

### Running

BWSC Scoreboard is built as an executable jar file which takes one argument.
~~~
java -jar scoreboard-<version>.jar <args>
~~~
To access the user interface:
~~~
http://localhost:8080
~~~
To access the JSON interface:
~~~
http://localhost:8080/data
~~~

#### Args
Arguments may be added to the command to modify the default behaviour.
 * -port=\<portName> serial port description. Default: COM4
 * -baudRate=\<baudRate> serial port baud rate. Default 19200
 * -stopBits=\<stopBits> serial port stop bits. Default: 8
 * -parity=\<parity> serial port parity. Default: NO_PARITY
 * -flow=\<flowControl> serial port flow control. Default: FLOW_CONTROL_DISABLED
 * -timeoutMode=\<mode> jSerialComm timeoutMode. Default: TIMEOUT_READ_BLOCKING
 * -readTimeout=\<ms> jSerialComm readTimeout. Default: 0 (none)
 * -trace data read from the the port to stderr. Default: not set (no trace)
 * -testFile=\<path> use data from a test file rather than a port
 * -test1 use data from a test file included in the jar file rather than a port
 * -testLoop results in the data in the test file being repeated once it has been read.
