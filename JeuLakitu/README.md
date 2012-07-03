Lakitu 
======
<img src="http://imgur.com/ixcsO.png" />

Description
-----------
[Lakitu](http://www.zenetproject.com/pages/lakitu) is a research project from UC Santa Cruz's [Expressive Intelligence Studio](http://eis-blog.ucsc.edu). It uses the [Zenet framework](http://www.zenetproject.com) to monitor events from a Super Mario World-style game, and issues repairs when the game fails. It is the smallest complete implementation of the Zenet architecture.

The game can be switched between the correct, original, implementation, and a buggy implementation that contains examples from as many applicable [video game failure taxonomy categories](http://www.zenetproject.com/pages/taxonomy) as possible.

It is a clone of [Infinite Mario Bros](http://www.mojang.com/notch/mario/), converted to use a rule engine to specify game integrity.

A full description of Lakitu can be found at its [web page](http://www.zenetproject.com/pages/lakitu).


Running Lakitu
--------------
As Lakitu requires an embedded message broker to run, I haven't worked out how to package it up as an executable JAR file. Instead, users must use [Maven](http://maven.apache.org) to build and run Lakitu from [source](http://github.com/Lewisham/Lakitu/). Running: 

    mvn compile activemq:run exec:java

inside the downloaded Lakitu directory should do the trick. This starts the message broker first, runs Lakitu, then kills the message broker once Lakitu terminates.