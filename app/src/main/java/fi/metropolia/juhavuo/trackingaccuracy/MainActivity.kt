package fi.metropolia.juhavuo.trackingaccuracy

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()

        main_new_button.setOnClickListener {
            val intent = Intent(this,StartMappingActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createNotificationChannel(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelID = LocationService.channelId
            val channelName = getString(R.string.notification_channel_name)
            val notificationDescription = getString(R.string.notification_content)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(channelID,channelName,importance)
            notificationChannel.description = notificationDescription
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)

            Log.i("test",notificationChannel.toString())
        }
    }
}
