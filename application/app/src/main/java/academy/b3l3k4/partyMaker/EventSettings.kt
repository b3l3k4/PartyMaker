package academy.b3l3k4.partyMaker

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class EventSettings: AppCompatActivity() {

    var dateEdit: EditText? = null
    var locationEdit: EditText? = null
    var expensesEdit: EditText? = null
    var desEdit: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.event_settings)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        val backwardButton: ImageButton = findViewById(R.id.backwardArrow)
        val saveButton: Button = findViewById(R.id.saveButton)
        val calendarButton: ImageButton = findViewById(R.id.calendar)

        dateEdit= findViewById(R.id.dateSettings)
        locationEdit = findViewById(R.id.locationSettings)
        expensesEdit = findViewById(R.id.expensesSettings)
        desEdit = findViewById(R.id.descriptionSettings)

        dateEdit!!.keyListener = null

        val date = intent.getStringExtra("DATE")
        val expenses  = intent.getStringExtra("EXPENSES")
        val location = intent.getStringExtra("LOCATION")
        val description = intent.getStringExtra("DESCRIPTION")

        if(date != null) dateEdit!!.hint = date
        if(expenses != null) expensesEdit!!.hint = expenses
        if(location != null) locationEdit!!.hint = location
        if(description!= null) desEdit!!.hint = description

        //calendar set up
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        calendarButton.setOnClickListener(object: View.OnClickListener{
            @SuppressLint("SetTextI18n")
            override fun onClick(v: View?) {
                val dpd = DatePickerDialog(this@EventSettings, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in TextView
                    dateEdit!!.setText("" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year)
                }, year, month, day)
                dpd.show()
            }
        })

        //save button on click
        saveButton.setOnClickListener { sendData()}

        //back button on click
        backwardButton.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val intentEvent = Intent(this@EventSettings, EventActivity::class.java)
                if(intent.getStringArrayExtra("DATA") != null){
                    val dataArray= intent.getStringArrayExtra("DATA")!!
                    intentEvent.putExtra("DATA", dataArray)
                }
                val eventUid = intent.getStringExtra("EVENT_UID")
                intentEvent.putExtra("UID", eventUid)
                intentEvent.putExtra("DATE", intent.getStringExtra("DATE"))
                intentEvent.putExtra("EXPENSES", intent.getStringExtra("EXPENSES"))
                intentEvent.putExtra("LOCATION", intent.getStringExtra("LOCATION"))
                intentEvent.putExtra("DESCRIPTION", intent.getStringExtra("DESCRIPTION"))

                startActivity(intentEvent)
            }
        })
    }

    private fun sendData(){
        val date = dateEdit!!.text.toString()
        val expenses = expensesEdit!!.text.toString()
        val location = locationEdit!!.text.toString()
        val description = desEdit!!.text.toString()

        val intentEvent = Intent(this, EventActivity::class.java)

        if(intent.getStringArrayExtra("DATA") != null){
            val dataArray= intent.getStringArrayExtra("DATA")!!
            intentEvent.putExtra("DATA", dataArray)
        }

        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val eventUid = intent.getStringExtra("EVENT_UID")
        val eventReference  = FirebaseDatabase.getInstance().getReference("Events").child(userId).child(eventUid!!)


        if(!TextUtils.isEmpty(date)) {
            eventReference.child("date").setValue(date)
        }
        if(!TextUtils.isEmpty(expenses)) {
            eventReference.child("expenses").setValue(expenses)
        }
        if(!TextUtils.isEmpty(location)) {
            eventReference.child("location").setValue(location)
        }
        if(!TextUtils.isEmpty(description)) {
            eventReference.child("description").setValue(description)
        }


        if(!TextUtils.isEmpty(date)) {
            intentEvent.putExtra("DATE", date)
        } else{
            intentEvent.putExtra("DATE", intent.getStringExtra("DATE"))
        }
        if(!TextUtils.isEmpty(expenses)) {
            intentEvent.putExtra("EXPENSES", expenses)
        } else{
            intentEvent.putExtra("EXPENSES", intent.getStringExtra("EXPENSES"))
        }
        if(!TextUtils.isEmpty(location)) {
            intentEvent.putExtra("LOCATION", location)
        } else{
            intentEvent.putExtra("LOCATION", intent.getStringExtra("LOCATION"))
        }
        if(!TextUtils.isEmpty(description)) {
            intentEvent.putExtra("DESCRIPTION", description)
        } else{
            intentEvent.putExtra("DESCRIPTION", intent.getStringExtra("DESCRIPTION"))
        }

        intentEvent.putExtra("UID", eventUid)

        startActivity(intentEvent)
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainScreen::class.java)
        intent.flags  = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}