# AirSim Middleware
This project provides a middleware between the [AirSim](https://github.com/Microsoft/AirSim)
 simulator and solver controlling the vehicles.

This is an upgrade of [this project](https://github.com/jelaredulla/thesis) to the current version of AirSim.
The purpose of this was to re-create Ann's experiment results. 
Her (heavily updated) original Java code is in the src/main/java folder.

The whole thing is then re-created as a Scala/Akka project under src/main/scala using actors for concurrency.
Because it's a simulation of evader/pursuer chase, the vehicle need to be controlled independently and not in the single-threaded loop.

## Quick Start
You will need AirSim running (preferably on a GPU and a different machine). The settings file is under src/main/resources/settings.json.
Copy this file to ~/Documents/AirSim/ on the machine running the AirSim.
I prefer to use the ground camera because that way you can see both drones and their actual movements. 
The camera needs to be pointed up - press W 5 or 6 times.

### Build and Run
    
    ./gradlew run

This will start the game settings window. Set the IP of the Simulator (the first field). 
To test if everything is working, keep the default settings and click Start Simulation.

The results are written in results/json/.

This is the [AirSim view](https://drive.google.com/file/d/1FpvNmZjIzIqehShiZJcGWPfyqkQRIskQ/view?usp=sharing)
and this is the [Settings/Visualizer view](https://drive.google.com/file/d/1FikohU1DVHKhRdBkIKxqSHDelCDuLUtG/view?usp=sharing).

### Postgresql Persistence
You can enable the Postgresql persistence by enabling the database in the src/main/resources/application.conf file.
It's necessary for the [visualizer API](https://github.com/homicidal-chauffeur/hc-analyzer-api) 
and the [visualizer](https://github.com/homicidal-chauffeur/hc-analyzer-ng) (although it's quite easy to change this to use the JSON files from results).

After creating the database config, you need to run the migrations:

    ./gradlew flywayMigrate
