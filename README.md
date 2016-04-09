# jLastFMgrabber
Grabs currently playing track from <a href="http://last.fm">last.fm</a> and writes info to a text file.

## Setup
Simply put the compiled jar file at any location you want, for instance your desktop.<br>
The program will create 2 files:

* configuration file: jlastfmgrabber.cfg - same root as the jar
* output file: custom path and name as defined in config file

*The first time you run the application it will automatically create the config file and exit since it's missing configuration values.*<br>
Configuration values are:

* **user**: the Last.FM username which you want to poll from
* **api_key**: your Last.FM API key, see <a href="http://www.last.fm/api/account/create">here</a> on how to obtain your own API key
* **output_file**: the path (relative to the application root) to write the info in.<br>
Example: now_playing.txt | c:/now_playing.txt
* **format (optional)**: the format to save the track info in, default "$artist - $track".<br>
 Currently supported values are:
  * $artist
  * $track
  * $album

If any of the non-optional values are missing the application will not run.

After you run the application you can access it via the System Tray icon. There is no GUI apart from the Tray Icon.

## Functionality
The application uses the Last.FM API to periodically (currently every 10 seconds) poll the currently playing track and process those information. The track has to be marked as "nowPlaying" in order to get processed further. If there is a track currently being played the information will get saved to the specified file in the specified format from which it can be read for instance by OBS.<br>
The file only gets updated if there is a new track being played. If there is no track being played the output file will be empty. It may take several minutes until Last.FM determines there is no track playing, unfortunately there is nothing that can be done about that as far as I'm aware.

If the application is crashing and you don't know why, open a command prompt/shell and execute it from there to see the console output.

## Requirements
This application uses Java 8 and thus requires the **Java 8 JRE** to be installed on your system.

## Modifications
You are free to modify parts of this application as you see fit as long as you **give credit where it's due**. Feel free to create a pull-request if you think your changes are something that should be included in the original.