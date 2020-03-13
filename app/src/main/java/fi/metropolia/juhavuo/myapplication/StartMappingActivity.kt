package fi.metropolia.juhavuo.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_start_mapping.*

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

            }
        }
    }
}
