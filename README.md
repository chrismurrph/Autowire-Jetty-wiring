# Autowire example on Jetty Server

Demonstration of a Scala.js web client communicating with a Jetty server over Ajax, using [Autowire](https://github.com/lihaoyi/autowire) and [uPickle](http://lihaoyi.github.io/upickle-pprint/upickle/). Adapted from [here](https://github.com/lihaoyi/workbench-example-app/tree/autowire).

A WEB-INF directory is included to help setup your Jetty WebApp. WEB-INF/lib contains a README.txt that has a listing of the correct jar files that can be obtained from your local artifact repository once the build has completed:

```
sbt
clean
fastOptJS
package

```

You will then need to copy the jar file `C:\dev\wiring\jvm\target\scala-2.11\server_2.11-0.1-SNAPSHOT.jar` to `C:\jetty-9.2.6\webapps\exampleWiring\WEB-INF\lib` (where of course `C:\dev\wiring\` and `C:\jetty-9.2.6\webapps\exampleWiring` are local to your machine). Note that there is a variable (`WebAppDirName`) in the source code for `"exampleWiring"` that will need to be changed if you want your webapps dir to be something other than `"exampleWiring"`.

Go to `localhost:8080/exampleWiring/` and if you do not see a simple filesystem browsing application please lodge an issue.
