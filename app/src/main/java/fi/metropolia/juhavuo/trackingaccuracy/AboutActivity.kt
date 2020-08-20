package fi.metropolia.juhavuo.trackingaccuracy

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_about.*
import java.io.InputStream
import java.lang.Exception

class AboutActivity : AppCompatActivity() {

    private var about_text: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        Thread {
            about_text = readFromFile(R.raw.about_application)
            if (!about_text.isNullOrBlank()) {
                this@AboutActivity.runOnUiThread {
                    about_activity_edit_text.setText(about_text)
                }
            }
        }.start()

        about_button_apache.setOnClickListener {
            val browsingIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.apache.org/licenses/LICENSE-2.0")
            )
            startActivity(browsingIntent)
        }

        about_activity_close_button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    private fun readFromFile(id: Int): String? {
        var textAsString = ""
        try {
            //https://stackoverflow.com/questions/39500045/in-kotlin-how-do-i-read-the-entire-contents-of-an-inputstream-into-a-string
            val inputStream: InputStream = resources.openRawResource(id)
            textAsString = inputStream.bufferedReader().use {
                it.readText()
            }
            inputStream.close()

        } catch (e: Exception) {
            Log.e("filehandling", e.toString())
        }
        return textAsString

    }
}