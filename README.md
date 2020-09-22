# StevesDB-Server
This is not the best database in the world, but at least I've tried.\
\
Creates a folder and a file in the current working directory:
* data/ - database storage folder
* config.xml - configuration file

\
The default user username is **stevesdb** and the password is **password**
# Database clients
Node.js: https://github.com/SteveTheEngineer/StevesDB-Client-Node
# Building the server from source
```
git clone https://github.com/SteveTheEngineer/StevesDB-Server
cd StevesDB-Server
./gradlew build
```
The output file is located inside `build/libs/`
