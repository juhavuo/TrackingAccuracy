package fi.metropolia.juhavuo.trackingaccuracy

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
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private val binder = LocationTrackingBinder()

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

        locationRequest = LocationRequest()
        locationRequest.interval = 20 * 1000 //20 seconds
        locationRequest.fastestInterval = 10 * 1000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        locationCallback = object: LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if(locationResult != null){
                    Log.i("test","altitude: ${locationResult.lastLocation}")
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("test","service started")
        isServiceStarted = true
        return START_NOT_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceStarted = false
        Log.i("test","service destroyed")

        //save location list to database
    }

    /*
     *Get the already gathered data of locations, so that mapping activity
     * can draw the route after reopening of that activity.
     */
    fun getLocationData(): ArrayList<Location>{
        return locationList
    }

    inner class LocationTrackingBinder: Binder(){
        fun getService(): LocationService{
            return this@LocationService
        }
    }
}