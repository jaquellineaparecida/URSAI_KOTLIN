package my.projects.ursai

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class InfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        setContentView(R.layout.info)

        val backgroundImage = findViewById<ConstraintLayout>(R.id.backgroundImage)
        val text1 = findViewById<TextView>(R.id.text1)
        val text2 = findViewById<TextView>(R.id.text2)
        val buttonContainer = findViewById<ConstraintLayout>(R.id.buttonContainer)
        val buttonText = findViewById<TextView>(R.id.buttonText)
        val icon = findViewById<ImageView>(R.id.icon)


        buttonContainer.setOnClickListener {
            val intent = Intent(this, KnowActivity::class.java)
            startActivity(intent)
        }
    }
}
