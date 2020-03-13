package fi.metropolia.juhavuo.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        main_new_button.setOnClickListener {
            val intent = Intent(this,StartMappingActivity::class.java)
            startActivity(intent)
        }
    }
}
