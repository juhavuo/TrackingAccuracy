package fi.metropolia.juhavuo.trackingaccuracy

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_tracking_map.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory

/**
 * This activity is for live view of walking route while tracking that route.
 * This started a service, that is binded to this activity, while this activity is visible.
 * When this activity stops, that service unbinds and continues to track users location to be presented in
 * map in this view.
 *
 * Author: Juha Vuokko
 */
class TrackingMapActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ctx = applicationContext
        Configuration.getInstance().load(ctx,
            PreferenceManager.getDefaultSharedPreferences(ctx))
        setContentView(R.layout.activity_tracking_map)

        val route_id = intent.getIntExtra("route_id",-1)
        val route_name = intent.getStringExtra("route_name")

        if(!route_name.isNullOrBlank()){
            tracking_map_title.text = route_name
        }
        tracking_map.setTileSource(TileSourceFactory.MAPNIK)

       checkPermissions()

        val serviceClass = LocationService::class.java
        val serviceIntent = Intent(applicationContext,serviceClass)

        //if service is not already running, start service
        if(!LocationService.isServiceStarted){
            startService(serviceIntent)
            Log.i("test","service started")
        }

        mapping_stop_button.setOnClickListener {
            if(LocationService.isServiceStarted){
                stopService(serviceIntent)
            }
        }


    }

    override fun onStart() {
        super.onStart()

        //bind service
    }

    override fun onStop(){
        super.onStop()

        //unbind service
    }

    private fun checkPermissions(){
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 0)
        }

        if(ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),0)
        }
    }


}
