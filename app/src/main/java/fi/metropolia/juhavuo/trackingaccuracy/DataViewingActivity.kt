package fi.metropolia.juhavuo.trackingaccuracy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class DataViewingActivity : AppCompatActivity() {

    private var routeid = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_viewing)

        routeid = intent.getIntExtra("routeid",-1)
        val routeName = intent.getStringExtra("routename")
    }

}
