Why are there no/only random commits?

This was made a long, long time ago with SVN version control so I just uploaded all files here at once

# Cue
A game that is a mixture of classic 2D top-down shooters and billiards for 2 players to play over a TCP/IP connection (the 2 players can play with each other as long as they are on the same network)

# Running App
To run the app, download/clone the repo

Go to the releases and download the 2 JAR files and place them in the same directory as the "Images" folder in the repo

Run one instance of CueServer and 2 instances of CueClient; Either both on one machine or on 2 different machines both on the same network

In the prompted fields of the clients, type in the host name and port number (both of which are displayed in the server GUI) and click connect to game

# Run Source Code
If you want to run the code directly, the entrypoints are ServerMain.java in Cue/tree/master/src/ip/milton/cue/server/execution/ServerMain.java and CueMain.java in Cue/tree/master/src/ip/milton/cue/Client/execution/CueMain.java

