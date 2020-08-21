package fi.metropolia.juhavuo.trackingaccuracy

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.activity_about.*
import java.io.InputStream
import java.lang.Exception

class AboutActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val map_constraint_layout = about_activity_linear_layout.findViewById<ConstraintLayout>(R.id.about_activity_map_segment)
        val map_tv = map_constraint_layout.findViewById<TextView>(R.id.about_segment_title)
        val map_et = map_constraint_layout.findViewById<EditText>(R.id.about_segment_text_edit)
        val map_button = map_constraint_layout.findViewById<Button>(R.id.about_segment_button)

        val graph_constraint_layout = about_activity_linear_layout.findViewById<ConstraintLayout>(R.id.about_activity_graph_segment)
        val graph_tv = graph_constraint_layout.findViewById<TextView>(R.id.about_segment_title)
        val graph_et = graph_constraint_layout.findViewById<EditText>(R.id.about_segment_text_edit)
        val graph_button = graph_constraint_layout.findViewById<Button>(R.id.about_segment_button)

        val rdp_constraint_layout = about_activity_linear_layout.findViewById<ConstraintLayout>(R.id.about_activity_rdp_segment)
        val rdp_tv = rdp_constraint_layout.findViewById<TextView>(R.id.about_segment_title)
        val rdp_et = rdp_constraint_layout.findViewById<EditText>(R.id.about_segment_text_edit)
        val rdp_button = rdp_constraint_layout.findViewById<Button>(R.id.about_segment_button)

        map_button.text = getString(R.string.about_activity_button_map_licence)
        graph_button.text = getString(R.string.about_activity_button_graph_licence)
        rdp_button.text = getString(R.string.about_activity_button_rdp_licence)

        map_tv.text = getString(R.string.about_map_title)
        graph_tv.text = getString(R.string.about_graph_title)
        rdp_tv.text = getString(R.string.about_rdp_title)



        Thread {
            val about_map_text = readFromFile(R.raw.about_map_application)
            val about_graph_text = readFromFile(R.raw.about_graph)
            val about_activity_text = readFromFile(R.raw.explanation_for_about_page)
            val about_rdb_algorithm = readFromFile(R.raw.about_rdp_algorithm)
            this@AboutActivity.runOnUiThread {
                map_et.setText(about_map_text)
                graph_et.setText(about_graph_text)
                rdp_et.setText(about_rdb_algorithm)
                about_activity_explanation_edit_text.setText(about_activity_text)
            }

        }.start()

        map_button.setOnClickListener {
            openWebpage("https://creativecommons.org/licenses/by-sa/2.0/legalcode")
        }

        graph_button.setOnClickListener {
            openWebpage("https://www.apache.org/licenses/LICENSE-2.0")
        }

        rdp_button.setOnClickListener {
            openWebpage("https://www.gnu.org/licenses/old-licenses/fdl-1.2.html")
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