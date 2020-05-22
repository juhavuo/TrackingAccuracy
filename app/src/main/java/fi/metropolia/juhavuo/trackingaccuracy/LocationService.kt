package fi.metropolia.juhavuo.trackingaccuracy

import android.app.Activity
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.google.android.gms.location.*

class LocationService: Service(){

    companion object{
        var isServiceStarted = false
        val channelId = "NotificationChannelForLocationService"
        val notificationId = 3001
        var isBinded = false
    }

    private val locationList: ArrayList<Location> = ArrayList()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var routeid = -1
    private var activity: CallbackForService? = null
    private val binder = LocationTrackingBinder()
    private lateinit var database: RouteDB

    override fun onCreate() {
        super.onCreate()
        val pendingIntent = Intent(this,LocationService::class.java)
            .let{notificationIntent->PendingIntent.getActivity(this,0,notificationIntent,0)}
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationBuilder = Notification.Builder(this, channelId)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getText(R.string.notification_content))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
            var notification = notificationBuilder.build()

            startForeground(notificationId,notification)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //get initial location
        try {
            fusedLocationClient.lastLocation.addOnCompleteListener{ task ->
                Log.i("test","task succesful: ${task.isSuccessful} result: ${task.result}")
               if(task.isSuccessful && task.result !=null){
                   locationList.add(task.result!!)
               }
            }
        }catch (secExp: SecurityException){
            Log.i("test",secExp.toString())
        }

        locationRequest = LocationRequest()
        locationRequest.interval = 20 * 1000 //20 seconds
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        locationCallback = object: LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if(locationResult != null){
                    if(isBinded && activity!=null){
                        activity!!.drawLocation(locationResult.locations[0])
                        locationList.addAll(locationResult.locations)
                    }
                }
            }
        }

        database = RouteDB.get(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("test","service started")
        if(intent!=null){
            routeid=intent!!.getIntExtra("routeid",-1)
        }
        startLocationUpdates()
        isServiceStarted = true
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onDestroy() {

        stopLocationUpdates()

        isServiceStarted = false
        Log.i("test","service destroyed")

    }

    /*
     *Get the already gathered data of locations, so that mapping activity
     * can draw the route after reopening of that activity.
     */
    fun getLocationData(): ArrayList<Location>{
        return locationList
    }

    fun hasLocations(): Boolean = locationList.isNotEmpty()

    fun registerClient(activity: Activity){
        this.activity = activity as CallbackForService
    }



    fun unregisterClient(){
        this.activity = null
    }

    private fun startLocationUpdates(){
        try{
            fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback,null)
        }catch (secExp: SecurityException){
            Log.e("test",secExp.toString())
        }
    }

    private fun stopLocationUpdates(){
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    fun saveLocationDataToDatabase(){
        Log.i("test","save the database")
        Log.i("test","location list size ${locationList.size}")
        Log.i("test","route id: $routeid")
        Thread{
            //add proper stopping time
            if(routeid!=-1) {
                database.routeDao().updateStoppingtime(routeid, System.currentTimeMillis())

                //add collected locations to the database
                var indexbase = 0
                if(database.measuredLocationDao().getAmountOfLocations()>0) {
                    indexbase = database.measuredLocationDao().getLargestLocationId() + 1
                }
                Log.i("index","start indexbase $indexbase")
                Log.i("index","$routeid")

                for ((index, location) in locationList.withIndex()) {
                    var bearingAccuracy = -1f
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        bearingAccuracy = location.bearingAccuracyDegrees
                    }
                    val measuredLocation = MeasuredLocation(indexbase+index,routeid,location.latitude,location.longitude,
                        location.altitude,location.speed,location.accuracy,location.bearing,bearingAccuracy,location.time)
                    Log.i("index","index raising  ${indexbase+index}")
                    database.measuredLocationDao().insert(measuredLocation)
                    Log.i("test","location inserted")
                }
            }
        }.start()
    }

    inner class LocationTrackingBinder: Binder(){
        fun getService(): LocationService{
            return this@LocationService
        }
    }

    //from answer to https://stackoverflow.com/questions/20594936/communication-between-activity-and-service
    interface CallbackForService{
        fun drawLocation(location: Location)
    }
}