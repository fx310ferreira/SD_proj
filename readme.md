# googol

## dependencies
As dependencies:
 - docker: 25.0.4
 - jdk: 21.0.2
 - gradle: 8.6


```bash
# Before running you should have the database setup on docker

# Windows
./docker/run.bat

# Unix
./docker/run.sh

# Run Client this has a  bug because of gradle
gralde client:run

# Gateway must be always running
gralde gateway:run 

# Run barrels
gradle barrel:run

# Run downloaders
gradle downloader:run

# Run the webclient
gradle webclient:run
```


## Managing DB
```bash
# Connect using psql
# change depending on docker parameters
psql -h localhost -p 5433 -U user

# List databases
\list


```

## Database Structure

![[University/LEI/Year_3/SD/SD_proj/ER.png]]

## Rmi and Multicast Relation Diagram

![[diagram.png]]
At any time, as long as the server (gateway) is running, any number of clients, barrels and downloaders can be created. All the necessary configurable settings are accessible in the respective resources.properties.