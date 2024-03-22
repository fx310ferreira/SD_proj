# googol

## dependencies
As dependencies:
 - docker: 25.0.4
 - jdk: 21.0.2
 - gradle: 8.6


```bash
# Before running you should have the database setup on docker

# Windows
./docker/setup.bat

# Unix
./docker/setup.sh

# Run Client this has a  bug because of gradle
gralde client:run

# Gateway must be always running
gralde gateway:run 

# Run barrels
gradle barrel:run

# Run downloaders
gradle downloader:run
```
