<h1>mobiLert <sub><sup><sub>A safetynet in motion</sub></sup></sub> </h1> 

![mobiLert Dashboard](https://github.com/gunalankarun/mobilert/blob/master/dashboard.png)

## Overview

Mobilert is a gait and stability monitoring application that can intelligently monitor walking patterns and detect abnormalities such as falls or limping. In addition it provides a web interface to monitor walking patterns of a user in real time (uses d3 visualization) and notifies of the abnormalities as they occur. For the monitoring system there are two parts, and android application that sends accelerometer data and a web server that recieves the data, processes it, and displays it on the web interface. While at the current state we chose to create an android device to send the acceleromter data, practically any device with an accelerometer and internet connection can be used.

This project was created in 24 hours during the 2015 MDP Hackathon in Austin, Texas and won 2nd place out of 21 teams. See below for team members.

Note: This is a copy of the web server and android application as it was at the end of the MDP Hackathon. The primary repos for the two can be viewed at:
+ https://github.com/casenh/athena_website
+ https://github.com/mbartling/athena-project

## How it works
##### Android Application
The Android application is a very barebones application with basically no ui. It simply collects x, y, and z accelererometer data and sends it to the specified web address (post request). The application can be closed and should still run in the background and send data.

##### Web Server
The server collects the data and performes visualization using d3 to display the data in realtime. In addition to visualization, the server performs Fast Fourier Transformations with the data to find an average magnitude value between the x, y, and z data. Using this magnitude we were able to set a threshold through experimentation that would determine if a person fell.


## Usage and Installation
##### Android Application
To install the android application simply build and run the MDP-Hack application inside of athena-project with android studio or similar software. The application should install on your android device using USB debugging. To change the web server address where the application is sending data check the dataservice file inside the application and change the corresponding line.

##### Web Server
The Web Server is a flask application that can be run locally or hosted on a site/IP. Note that for the the service to work the android application will need to be able to send data to the server so hosting the website on localhost will not work. In order to change the web server hosting address, modify the line in __init__.py and config.py inside of athena_website/controllers.

After setting up both the server and android application open the android application and the website should display a live feed from your device. Try refreshing the web page if it does not work the first time.

## Future
While the current version of the application performs real time monitoring and fall detection, we see greater potential in the application. The walking pattern data obtained by the application can be analyzed using machine learning to form a baseline classification for every user. This baseline can then be used to detect long term abnormalities such as limping or a preferance for a certain leg that may be a symptom of a greater long term problem. With this detailed information doctors can provide specialized treatment and diagnose problems early on.

## The Team

**Casen Hunger** <br/>
http://github.com/casenh
 <br/>

**Michael Bartling** <br/>
http://github.com/mbartling
 <br/>

**Gunalan Karun** <br/>
http://github.com/gunalankarun
 <br/>

**Abbas Ally** <br/>
http://github.com/abbasally5
 <br/>

**Jeremiah Lee** <br/>
http://github.com/twb119
 <br/>

## Contact

Casen, Michael, Gunalan, and Abbas are part of Spark Lab (Security, Privasy, and Computer Architecture) at UT Austin and conduct research related to security and machine learning. For more information please contact the UT Austin Spark lab.
