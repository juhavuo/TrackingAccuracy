package fi.metropolia.juhavuo.trackingaccuracy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_start_mapping.*
import java.util.*

class StartMappingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_mapping)

        starting_dialog_cancel_button.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        starting_dialog_start_button.setOnClickListener {
            if(starting_dialog_name_edittext.text.isNotEmpty() && starting_dialog_details_edittext.text.isNotEmpty()){
                val intent = Intent(this, TrackingMapActivity::class.java)

                val startingTime: Long = System.currentTimeMillis()
                Log.i("testing","${Date(startingTime)}")

                val database = RouteDB.get(this)

                var id = 0
                val route_name = starting_dialog_name_edittext.text.toString()

                Thread{

                    Log.i("testing","${database.routeDao().getAmountOfRoutes()}")

                    if(database.routeDao().getAmountOfRoutes()>0){
                        id = database.routeDao().getBiggestRouteId()+1
                    }
                    val route = Route(id,route_name,starting_dialog_details_edittext.text.toString(),startingTime,-1L)
                    database.routeDao().insert(route)
                    intent.putExtra("route_id",id)
                    intent.putExtra("route_name",route_name)
                    startActivity(intent)
                }.start()
            }else{
                var toastText = ""
                if(starting_dialog_name_edittext.text.isEmpty()){
                    toastText += "Name missing"
                }
                if(starting_dialog_details_edittext.text.isEmpty()){
                    if(toastText.isNotEmpty()){
                        toastText +=", details text missing"
                    }else{
                        toastText += "Details text missing"
                    }
                }

                Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
