# Java ME GPS tracker

## Goals
To create a GPS tracker for simple phones that is usable in specific conditions:
- low visibility (poor light, phone stored in the handle bar holder or in a backpack)
- low GPS signal (need to wait long for GPS location to be obtained)
- limited ability to use the keyboard (wet hands, wearing gloves)

![Screenshot](https://raw.githubusercontent.com/SebastianCelejewski/jme-gps-tracker/master/doc/screenshot-01.png)

## Application characteristics
- clear user interface allowing to operate the application in poor conditions (big, clear information on the screen, usage of sound)
- ability to connect to various workout application servers (Endomondo, Strava, RunKeeper, etc.)

![Poor conditions - all OK](https://github.com/SebastianCelejewski/jme-gps-tracker/blob/master/doc/2016-08-25%200001.jpg?raw=true)

Visibility is usually very poor outdoors. Big colour rectangles indicate status of various application components. User can barely see text, but "all green" indicates, that all is working correctly.

![Poor condidions - problem](https://github.com/SebastianCelejewski/jme-gps-tracker/blob/master/doc/2016-08-25%200003.jpg?raw=true)

Red rectangle? A component sending GPS track data to Endonondo server reports a problem.

## How to contribute
- The most important thing at the moment is to test this application running on various hardware.
- All remarks about the user interface are very helpful.
- Application behaviour on long distances is a mystery at the moment.

## Task list
https://trello.com/b/rHA1pG8C/jme-gps-tracker

## Documentation
### Application lifecycle
![Lifecycle](https://raw.githubusercontent.com/SebastianCelejewski/jme-gps-tracker/master/doc/jme-gps-tracker-workflow.png)

## Tests

- Samsung Solid GT-B2710
- Nokia 2710
- Sony Erricson Walkman W890i
