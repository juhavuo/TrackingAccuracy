package fi.metropolia.juhavuo.trackingaccuracy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_about.*
import java.io.InputStream
import java.lang.Exception

class AboutActivity : AppCompatActivity() {

    private var about_text: String? = null
    private var apache_licence_2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        Thread{
            try{
                //https://stackoverflow.com/questions/39500045/in-kotlin-how-do-i-read-the-entire-contents-of-an-inputstream-into-a-string
                val inputStream: InputStream = resources.openRawResource(R.raw.about_application)
                about_text = inputStream.bufferedReader().use {
                    it.readText()
                }
                inputStream.close()
                if(!about_text.isNullOrBlank()) {
                    this@AboutActivity.runOnUiThread {
                        about_activity_edit_text.setText(about_text)
                    }
                }
            }catch(e: Exception){
                Log.e("filehandling", e.toString())
            }
        }.start()
    }
}