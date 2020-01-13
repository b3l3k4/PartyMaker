package academy.b3l3k4.partyMaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlin.math.exp

class EventActivity: AppCompatActivity() {

    var memberList: MutableList<Member> = mutableListOf()

    var date: String? = null
    var expenses: String? = null
    var location: String? = null
    var des: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.event_activity)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        //initialise buttons, textViews and editTexts
        val backwardArrow: ImageButton = findViewById(R.id.backwardArrow)
        val settings: ImageButton = findViewById(R.id.profileSettings)

        val partyName: TextView = findViewById(R.id.partyName)
        val venueName: TextView = findViewById(R.id.venueName)
        val venuePrice: TextView = findViewById(R.id.venuePrice)
        val venueCount: TextView = findViewById(R.id.venueCount)
        val venueImage: ImageView = findViewById(R.id.venueImage)
        val venueMoreInfo: TextView = findViewById(R.id.venueMoreInfo)

        val editDate: EditText = findViewById(R.id.editDate)
        val editLocation: EditText = findViewById(R.id.editLocation)
        val editExpenses: EditText = findViewById(R.id.editExpenses)
        val editDescription: EditText = findViewById(R.id.editDescription)

        val addMember: Button = findViewById(R.id.addMemberButton)

        //backward arrow on click
        backwardArrow.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                startActivity(Intent(this@EventActivity, MainScreen::class.java))
            }
        })

        var dataArray: Array<String>? = null

        val partyNameIntent =  intent.getStringExtra("title")
        val venueNameIntent = intent.getStringExtra("pavadinimas")
        val venuePriceIntent  =intent.getStringExtra("kaina")
        val venueCountIntent = intent.getStringExtra("zmones")
        val photoIdIntent = intent.getStringExtra("id")
        if(intent.getStringExtra("title") != null) {
            dataArray = arrayOf(partyNameIntent!!, venueNameIntent!!, venuePriceIntent!!, venueCountIntent!!, photoIdIntent!!)
        } else {
            dataArray = intent.getStringArrayExtra("DATA")
        }

        if(intent.getStringArrayExtra("DATA") != null) {
            val intentArray: Array<String> = intent.getStringArrayExtra("DATA")!!
            print("VSIKAS LIT")
            print("LIT")
            partyName.text = intentArray[0]
            venueName.text = intentArray[1]
            venuePrice.text = intentArray[2]
            venueCount.text = intentArray[3]
        } else{
            partyName.text = partyNameIntent
            venueName.text = venueNameIntent
            venuePrice.text = venuePriceIntent
            venueCount.text = venueCountIntent
        }


        if(intent.getStringExtra("DATE") != null) editDate.setText(intent.getStringExtra("DATE"))
            else editDate.setText(intent.getStringExtra("data"))
        if(intent.getStringExtra("EXPENSES") != null) editExpenses.setText(intent.getStringExtra("EXPENSES"))
            else editExpenses.setText(intent.getStringExtra("expenses"))
        if(intent.getStringExtra("LOCATION") != null) editLocation.setText(intent.getStringExtra("LOCATION"))
            else editLocation.setText(intent.getStringExtra("location"))
        if(intent.getStringExtra("DESCRIPTION") != null) editDescription.setText(intent.getStringExtra("DESCRIPTION"))
            else editDescription.setText(intent.getStringExtra("des"))


        //settings and onClickListener
        editDate.keyListener = null
        editLocation.keyListener = null
        editExpenses.keyListener = null
        editDescription.keyListener = null

        val attendantsList: ListView = findViewById(R.id.attendantsView)

        val eventUid: String? = if(intent.getStringExtra("UID") != null){
            intent.getStringExtra("UID")
        } else{
            intent.getStringExtra("uid")
        }

        readData(object: FirebaseCallback{
            override fun onCalback(list: MutableList<Member>) {
                val adapter = CustomMemberListAdapter(this@EventActivity, R.layout.custom_member_list, list)
                attendantsList.adapter = adapter
            }
        })

        date = editDate.text.toString()
        expenses = editExpenses.text.toString()
        location = editLocation.text.toString()
        des = editDescription.text.toString()

        val intentSettings = Intent(this@EventActivity, EventSettings::class.java)
        intentSettings.putExtra("EVENT_UID", eventUid)
        intentSettings.putExtra("DATE", date)
        intentSettings.putExtra("EXPENSES", expenses)
        intentSettings.putExtra("LOCATION", location)
        intentSettings.putExtra("DESCRIPTION", des)
        intentSettings.putExtra("DATA", dataArray)


        settings.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                finish()
                startActivity(intentSettings)
            }
        })


        //download images from storage
        val photoID: String? = if(intent.getStringArrayExtra("DATA") != null){
            val intentArray = intent.getStringArrayExtra("DATA")
            intentArray!![4] + ".jpg"
        } else{
            intent.getStringExtra("id")!!.toString() + ".jpg"
        }

        val storageReference = FirebaseStorage.getInstance().reference.child("images/").child(photoID!!)

        Glide.with(this)
            .load(storageReference)
            .fitCenter()
            .into(venueImage)

        venueMoreInfo.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
//                val webIntent = Intent(this@EventActivity, WebView::class.java)
//                webIntent.putExtra("URL", intent.getStringExtra("psl_nuoroda"))
                val url = intent.getStringExtra("psl_nuoroda")
                val webIntent: Intent = Uri.parse(url).let{webpage ->
                    Intent(Intent.ACTION_VIEW, webpage)
                }
                startActivity(webIntent)
            }
        })

        addMember.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val newMemberIntent = Intent(this@EventActivity, AddNewMember::class.java)
                val eventTitle = intent.getStringExtra("title")
                newMemberIntent.putExtra("uid", eventUid)
                newMemberIntent.putExtra("title", eventTitle)
                newMemberIntent.putExtra("EVENT_UID", eventUid)
                newMemberIntent.putExtra("DATE", date)
                newMemberIntent.putExtra("EXPENSES", expenses)
                newMemberIntent.putExtra("LOCATION", location)
                newMemberIntent.putExtra("DESCRIPTION", des)
                newMemberIntent.putExtra("DATA", dataArray)

                startActivity(newMemberIntent)
            }
        })
    }


    private fun readData(firebaseCallback: FirebaseCallback){

        val eventUid: String? = if(intent.getStringExtra("UID") != null) {
            intent.getStringExtra("UID")
        } else{
            intent.getStringExtra("uid")
        }

        val eventMemberReference = FirebaseDatabase.getInstance().getReference("EventMembers").child(eventUid!!)

        val valueEventListener = object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val children = dataSnapshot.children
                memberList.clear()
                children.forEach {
                    val memberData = it.getValue<Member>(Member::class.java)
                    memberList.add(memberData!!)
                }
                firebaseCallback.onCalback(memberList)
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e("DB", p0.message)
            }
        }

        eventMemberReference.addListenerForSingleValueEvent(valueEventListener)
    }

    private interface FirebaseCallback{
        fun onCalback(list: MutableList<Member>)
    }


    override fun onBackPressed() {
        val intent = Intent(this, MainScreen::class.java)
        intent.flags  = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}