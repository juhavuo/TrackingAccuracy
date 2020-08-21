package fi.metropolia.juhavuo.trackingaccuracy

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.activity_about.*
import java.io.InputStream
import java.lang.Exception

class AboutActivity : AppCompatActivity() {

    private var about_map_text = ""
    private var about_graph_text = ""
    private var about_activity_text = ""
    private var about_rdb_algorithm = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val rdp_constraint_layout = about_activity_linear_layout.findViewById<ConstraintLayout>(R.id.about_activity_rdp_segment)
        val rdp_tv = rdp_constraint_layout.findViewById<TextView>(R.id.about_segment_title)
        rdp_tv.text = "test"

        Thread {
            about_map_text = readFromFile(R.raw.about_map_application)
            about_graph_text = readFromFile(R.raw.about_graph)
            about_activity_text = readFromFile(R.raw.explanation_for_about_page)
            about_rdb_algorithm = readFromFile(R.raw.about_rdp_algorithm)
            this@AboutActivity.runOnUiThread {
                about_activity_map_edit_text.setText(about_map_text)
                about_activity_graph_edit_text.setText(about_graph_text)
                about_activity_explanation_edit_text.setText(about_activity_text)
            }

        }.start()

        about_button_map_licence.setOnClickListener {
            openWebpage("https://creativecommons.org/licenses/by-sa/2.0/legalcode")
        }

        about_button_graph_licence.setOnClickListener {
            openWebpage("https://www.apache.org/licenses/LICENSE-2.0")
        }

        about_activity_close_button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    private fun openWebpage(address: String){
        val browsingIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(address)
        )
        startActivity(browsingIntent)
    }

    private fun readFromFile(id: Int): String {
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