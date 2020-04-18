package fi.metropolia.juhavuo.trackingaccuracy

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
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

    private lateinit var locationService: LocationService
    private lateinit var locations: ArrayList<Location>

    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(p0: ComponentName?) {
            LocationService.isBinded=false
        }

        override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
            val binder = service as LocationService.LocationTrackingBinder
            locationService = binder.getService()
            LocationService.isBinded=true
        }

    }

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
        }

        mapping_stop_button.setOnClickListener {

            //must unbind first
            unbindLocationService()
            stopService(serviceIntent)
            backToMain()
        }


    }

    override fun onStart() {
        super.onStart()

        //bind service
        Intent(this, LocationService::class.java).also { intent->
            bindService(intent,connection, Context.BIND_AUTO_CREATE)
        }
        /*
        locations = locationService.getLocationData()
        for (location in locations){
            Log.i("test","${location.latitude}")
        }*/

    }

    override fun onStop(){
        super.onStop()
        Log.i("test","Tracking map activity onStop")
        //unbind service
        if(LocationService.isBinded) {
            unbindLocationService()
            Log.i("test","unbind at stop")
        }
    }

    override fun onBackPressed() {
        backToMain()
    }

    private fun backToMain(){
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
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

    private fun unbindLocationService(){
        unbindService(connection)
        LocationService.isBinded = false
    }

}
