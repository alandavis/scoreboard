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
java -jar scoreboard-<version>.jar <arg>
~~~
* When started without any arguments, the program lists all available serial ports.
* The argument should be the number of the serial port to which the SWISS TIMING equipment is connected.
* To trace the output from the port make the argument a negative number.
* There is an alternative form -test=\<filename>. In this case a file matching the trace output format is read rather than the serial port.
