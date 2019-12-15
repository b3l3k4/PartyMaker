package academy.b3l3k4.partyMaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class EventCreated: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.event_created)

        val backButton: Button = findViewById(R.id.backButton)

        backButton.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                startActivity(Intent(this@EventCreated, MainScreen::class.java))
            }
        })
    }
}