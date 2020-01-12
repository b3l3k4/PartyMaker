package academy.b3l3k4.partyMaker

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import org.w3c.dom.Text
import java.util.*


class StartNewEvent:AppCompatActivity() {

    private var textTitle: EditText? = null
    private var textDate: EditText? = null
    private var textDescription: EditText? = null
    private var textPrice: EditText? = null
    private var textAttendants: EditText? = null

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null

    private val TAG = "activity manager"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_new_event)

        Log.d(TAG,"On create started")

        textTitle = findViewById(R.id.textTitle)
        textDate = findViewById(R.id.textDate)
        textDescription = findViewById(R.id.textDescription)
        textPrice = findViewById(R.id.textPrice)
        textAttendants = findViewById(R.id.textAttendants)

        val backwardArrow: ImageButton = findViewById(R.id.backwardArrow)

        val buttonVenue: Button = findViewById(R.id.buttonVenue)
        val buttonCreate: Button = findViewById(R.id.buttonCreate)

        if(intent.getStringArrayExtra("insertedData") != null){
            val dataArray: Array<String> = intent.getStringArrayExtra("insertedData")!!
            textTitle!!.setText(dataArray[0])
            textDate!!.setText(dataArray[1])
            textDescription!!.setText(dataArray[2])
            textPrice!!.setText(dataArray[3])
            textAttendants!!.setText(dataArray[4])
        }

        backwardArrow.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                startActivity(Intent(this@StartNewEvent, MainScreen::class.java))
            }
        })

        buttonVenue.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val title = textTitle!!.text.toString()
                val date = textDate!!.text.toString()
                val description = textDescription!!.text.toString()
                val approximatePrice = textPrice!!.text.toString()
                val attendants = textAttendants!!.text.toString()

                val intentInsertedData = Intent(this@StartNewEvent, FindVenue::class.java)

                if(!TextUtils.isEmpty(title) || !TextUtils.isEmpty(date) || !TextUtils.isEmpty(description) || !TextUtils.isEmpty(approximatePrice) || !TextUtils.isEmpty(attendants)){
                    val dataArray: Array<String> = arrayOf(title,date,description,approximatePrice,attendants)
                    intentInsertedData.putExtra("insertedData", dataArray)
                }

                startActivity(intentInsertedData)
            }
        })

        buttonCreate.setOnClickListener { createNewEvent() }

    }

    private fun createNewEvent(){
        val title = textTitle!!.text.toString()
        val date = textDate!!.text.toString()
        val description = textDescription!!.text.toString()
        val approximatePrice = textPrice!!.text.toString()
        val attendants = textAttendants!!.text.toString()

        var party1 = Party(title, date, description, approximatePrice, attendants,"","","","","","","")

        if(intent.getStringExtra("pavadinimas") != null) {
            party1.name = intent.getStringExtra("pavadinimas")
            party1.price = intent.getStringExtra("kaina")
            party1.max_capacity = intent.getStringExtra("zmones")
            party1.advert_url = intent.getStringExtra("psl_nuoroda")
            party1.image_url = intent.getStringExtra("foto_nuoroda")
            party1.id = intent.getStringExtra("id")
        }

        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(date)){
            passData(party1)
            updateLayout()
        } else{
            Toast.makeText(this, "Enter at least title and date", Toast.LENGTH_SHORT).show()
        }
    }

    private fun passData(party: Party){
        mDatabaseReference = FirebaseDatabase.getInstance().reference.child("Events")
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val eventID = FirebaseDatabase.getInstance().getReference().push().key.toString()
        mDatabaseReference!!.child(userId).child(eventID).setValue(party)
    }

    private fun updateLayout(){
        val intent = Intent(this@StartNewEvent, EventCreated::class.java)
        startActivity(intent)
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "activity stopped")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "activity resumed")
    }
}