# Autowire example on Jetty Server

Demonstration of a Scala.js web client communicating with a Jetty server over Ajax, using [Autowire](https://github.com/lihaoyi/autowire) and [uPickle](http://lihaoyi.github.io/upickle-pprint/upickle/). Adapted from [here](https://github.com/lihaoyi/workbench-example-app/tree/autowire).

A WEB-INF directory is included to help setup your Jetty webapp. Once you have copied across the contents of WEB-INF included here consult WEB-INF/lib/README.txt, which has a listing of the correct jar files that can be obtained from your local artifact repository once the build has completed. More manual copying sorry!

These are the steps for completing the build:

```
sbt
clean
package
fastOptJS

```

You will then need to copy the jar file 

`C:\dev\Autowire-Jetty-wiring\jvm\target\scala-2.11\server_2.11-0.1-SNAPSHOT.jar` 

to 

`C:\jetty-9.2.6\webapps\exampleWiring\WEB-INF\lib` 

(where of course `C:\dev\Autowire-Jetty-wiring\` and `C:\jetty-9.2.6\webapps\exampleWiring` might be different on your machine). 

Start your Jetty server then go to `localhost:8080/exampleWiring/`. You should see a simple filesystem browsing application.
