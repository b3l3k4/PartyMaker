package academy.b3l3k4.partyMaker

import android.content.Intent
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
        val userId = FirebaseAuth.getInstance().currentUser!!.uid


        //backward arrow on click
        backwardArrow.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                startActivity(Intent(this@EventActivity, MainScreen::class.java))
            }
        })

        if(intent.getStringArrayExtra("VENUE") != null){
            val arrayTemp = intent.getStringArrayExtra("VENUE")
            partyName.text = arrayTemp!![0]
            venueName.text = arrayTemp[1]
            venuePrice.text = arrayTemp[2]
            venueCount.text = arrayTemp[3]
        } else{
            partyName.text = intent.getStringExtra("title")
            venueName.text = intent.getStringExtra("pavadinimas")
            venuePrice.text = intent.getStringExtra("kaina")
            venueCount.text = intent.getStringExtra("zmones")
        }


        if(intent.getStringArrayExtra("DATA") != null){
            val arrayTemp = intent.getStringArrayExtra("DATA")
            editDate.setText(arrayTemp!![0])
            editLocation.setText(arrayTemp[1])
            editExpenses.setText(arrayTemp[2])
            editDescription.setText(arrayTemp[3])
        } else{
            editDate.setText(intent.getStringExtra("data"))
            editDescription.setText(intent.getStringExtra("des"))
            editLocation.setText(intent.getStringExtra("location"))
            editExpenses.setText(intent.getStringExtra("expenses"))
        }

        //settings and onClickListener
        editDate.isEnabled = false
        editLocation.isEnabled = false
        editExpenses.isEnabled = false
        editDescription.isEnabled = false

        val attendantsList: ListView = findViewById(R.id.attendantsView)
        val eventUid = intent.getStringExtra("uid")

        print(eventUid)

        readData(eventUid!!, object: FirebaseCallback{
            override fun onCalback(list: MutableList<Member>) {
                val adapter = CustomMemberListAdapter(this@EventActivity, R.layout.custom_member_list, list)
                attendantsList.adapter = adapter
            }
        })


        settings.setOnClickListener(object: View.OnClickListener{
            var clickCounter = 0

            override fun onClick(v: View?) {

                when {
                    clickCounter % 2 == 0 -> {
                        editDate.isEnabled = true
                        editDate.isFocusable
                        editLocation.isEnabled = true
                        editLocation.isFocusable
                        editExpenses.isEnabled = true
                        editExpenses.isFocusable
                        editDescription.isEnabled = true
                        editDescription.isFocusable
                    }
                    else -> {
                        editDate.isEnabled = false
                        editLocation.isEnabled = false
                        editExpenses.isEnabled = false
                        editDescription.isEnabled = false

                        val date = editDate.text.toString()
                        val location = editLocation.text.toString()
                        val expenses = editExpenses.text.toString()
                        val des = editDescription.text.toString()

                        editDate.setText(date)
                        editLocation.setText(location)
                        editExpenses.setText(expenses)
                        editDescription.setText(des)

                        val eventReference = FirebaseDatabase.getInstance().getReference("Events").child(userId).child(eventUid!!)

                        if(!TextUtils.isEmpty(date)) eventReference.child("date").setValue(date)
                        if(!TextUtils.isEmpty(location)) eventReference.child("location").setValue(location)
                        if(!TextUtils.isEmpty(expenses)) eventReference.child("expenses").setValue(expenses)
                        if(!TextUtils.isEmpty(des)) eventReference.child("description").setValue(des)

                        val array = arrayListOf(date, location, expenses, des)

                        val title: String? = intent.getStringExtra("title")
                        val name: String? = intent.getStringExtra("pavadinimas")
                        val price: String? = intent.getStringExtra("kaina")
                        val count: String? = intent.getStringExtra("zmones")

                        val venueArray = arrayListOf(title, name, price, count)
                        val intentSettings = Intent(this@EventActivity, EventActivity::class.java)
                        intentSettings.putExtra("DATA", array)
                        intentSettings.putExtra("VENUE", venueArray)
                        startActivity(intentSettings)

                    }
                }

                clickCounter++

            }
        })

        //download images from storage
        val photoID = intent.getStringExtra("id")!!.toString() + ".jpg"

        val storageReference = FirebaseStorage.getInstance().reference.child("images/").child(photoID)

        Glide.with(this)
            .load(storageReference)
            .fitCenter()
            .into(venueImage)

        venueMoreInfo.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val webIntent = Intent(this@EventActivity, WebView::class.java)
                webIntent.putExtra("URL", intent.getStringExtra("psl_nuoroda"))
                startActivity(webIntent)
            }
        })

        addMember.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                    val newMemberIntent = Intent(this@EventActivity, AddNewMember::class.java)
                    val eventTitle = intent.getStringExtra("title")
                    newMemberIntent.putExtra("uid", eventUid)
                    newMemberIntent.putExtra("title", eventTitle)
                    startActivity(newMemberIntent)
            }
        })

    }

    private fun readData(eventUid: String?, firebaseCallback: FirebaseCallback){
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

        eventMemberReference.addValueEventListener(valueEventListener)
    }

    private interface FirebaseCallback{
        fun onCalback(list: MutableList<Member>)
    }
}