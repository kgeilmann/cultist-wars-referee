# Updated Referee and CLI Runner for Cultist Wars

This repository contains an updated version of the official referee for [CodinGame's Cultist Wars](https://www.codingame.com/multiplayer/bot-programming/cultist-wars).
It was cloned from the original referee, which can be found at https://bitbucket.org/Nixerrr/cultist-wars/src/master/.

Main Changes:
- made the referee compatible with jvms >= version 17
- added a command line runner to make the referee compatible with [cg-brutaltester](https://github.com/dreignier/cg-brutaltester) 

## Build

- install Java 17
- run `mvnw package` (or `.\mvnw package` on windows) in the root dir where the `pom.xml` file is

The compiled jar file is in ./target/cultist-wars-1.0-SNAPSHOT-jar-with-dependencies.jar

## Run

Run `java -jar cultist-wars-1.0-SNAPSHOT-jar-with-dependencies.jar` to get a list of available options of the referee. 

To use with [cg-brutaltester](https://github.com/dreignier/cg-brutaltester), copy the JAR files for the referee and for cg-brutaltester into the same directory and then follow the readme of cg-brutaltester.

This is a sample command for some python bots

```
java -jar cg-brutaltester-1.0.0-SNAPSHOT.jar \
    -r "java -jar cultist-wars-1.0-SNAPSHOT-jar-with-dependencies.jar" \
    -p1 "python3 player1.py" \
    -p2 "python3 player2.py" \    
    -l logs/     \
    -t 4   \
    -n 10
```
