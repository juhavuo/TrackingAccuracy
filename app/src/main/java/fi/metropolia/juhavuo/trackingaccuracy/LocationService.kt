package fi.metropolia.juhavuo.trackingaccuracy

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log

class LocationService: Service(){

    companion object{
        var isServiceStarted = false
        val channelId = "NotificationChannelForLocationService"
        val notificationId = 3001
    }

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
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("test","service started")
        isServiceStarted = true
        return START_NOT_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceStarted = false
        Log.i("test","service destroyed")
    }


}