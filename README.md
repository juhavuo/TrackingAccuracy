# TrackingAccuracy
This is for Bachelor's Thesis in Metropolia. Link to thesis: http://urn.fi/URN:NBN:fi:amk-2020102721693

This is application for Android devices made by Kotlin. Currently the development is in pause, but I will return to this project
during this spring 2021.

With this application one can track one's travelling route. There is live map, that shows the collected locations. 
Locations can be also collected while the application is not active or even closed, because locations are collected from
foreground service, that can bind to TrackingMapActivity, when it is running. The data is collected to local database.
The data can viewed after it is collected in separated activity, DataViewingActivity, that uses fragments: MapFragment,
MenuFragment, GraphFragment and NumericalFragment. Here one can compare visually effects of different algorithms to the correctness
of the route. MapFragment shows the map. In MenuFragment one can select, what is shown in the map. In GraphFragment data
of route is shown as graphs (distance-time, speed-time, altitude-distance). NumericalFragment shows collected data numerically.

There is still lot to adjust. This application is done for my phone and for example UI of Numerical Fragment needs to be
make more adjustable for different screen sizes.
