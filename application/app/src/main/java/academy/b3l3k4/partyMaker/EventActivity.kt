package academy.b3l3k4.partyMaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class EventActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.event_activity)

        //initialise buttons, textViews and editTexts
        val backwardArrow: ImageButton = findViewById(R.id.backwardArrow)
        val settings: ImageButton = findViewById(R.id.profileSettings)

        val partyName: TextView = findViewById(R.id.partyName)
        val venueName: TextView = findViewById(R.id.venueName)
        val venuePrice: TextView = findViewById(R.id.venuePrice)
        val venueCount: TextView = findViewById(R.id.venueCount)
        val venueMoreInfo: TextView = findViewById(R.id.venueMoreInfo)

        val editDate: EditText = findViewById(R.id.editDate)
        val editLocation: EditText = findViewById(R.id.editLocation)
        val editExpenses: EditText = findViewById(R.id.editExpenses)
        val editDescription: EditText = findViewById(R.id.editDescription)

        val addMember: ImageButton = findViewById(R.id.addMemberButton)

        //backward arrow on click
        backwardArrow.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                startActivity(Intent(this@EventActivity, MainScreen::class.java))
            }
        })



    }
}