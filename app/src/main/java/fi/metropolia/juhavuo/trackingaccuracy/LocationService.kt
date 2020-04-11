package fi.metropolia.juhavuo.trackingaccuracy

import android.app.Service
import android.content.Intent
import android.os.IBinder

class LocationService: Service(){

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }


}