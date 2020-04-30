package fi.metropolia.juhavuo.trackingaccuracy

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()

        checkLocationPermission()
        checkExternalStoragePermisson()

        if(LocationService.isServiceStarted){
            main_new_button.text = getText(R.string.main_button_straight_to_mapping)
        }

        main_new_button.setOnClickListener {
            if(!LocationService.isServiceStarted) {
                val intent = Intent(this, StartMappingActivity::class.java)
                startActivity(intent)
            }else{
                val intent = Intent(this,TrackingMapActivity::class.java)
                startActivity(intent)
            }
        }

        main_routes_button.setOnClickListener {
            val intent = Intent(this,RouteListingActivity::class.java)
            startActivity(intent)
        }


    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 0
            )
        }
    }

    private fun checkExternalStoragePermisson(){

        if(ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),0)
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
