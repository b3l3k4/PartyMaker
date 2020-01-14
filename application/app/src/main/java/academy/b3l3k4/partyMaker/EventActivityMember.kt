package academy.b3l3k4.partyMaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class EventActivityMember: AppCompatActivity() {

    var memberList: MutableList<Member> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.event_activity_member)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        //initialise buttons, textViews and editTexts
        val backwardArrow: ImageButton = findViewById(R.id.backwardArrow)

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

        //backward arrow on click
        backwardArrow.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                startActivity(Intent(this@EventActivityMember, MainScreen::class.java))
            }
        })

        val attendantsList: ListView = findViewById(R.id.attendantsView)
        val eventUid = intent.getStringExtra("uid")
        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        readData(eventUid, object: FirebaseCallback {
            override fun onCalback(list: MutableList<Member>) {
                val adapter = CustomMemberListAdapter(this@EventActivityMember, R.layout.custom_member_list, list)
                attendantsList.adapter = adapter
            }
        })

        //leave party
        val leaveParty: TextView = findViewById(R.id.leaveParty)

        leaveParty.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val eventReference = FirebaseDatabase.getInstance().getReference("Events").child(userId).child(eventUid!!)
                val memberReference = FirebaseDatabase.getInstance().getReference("EventMembers").child(eventUid).child(userId)
                eventReference.removeValue()
                memberReference.removeValue()
                startActivity(Intent(this@EventActivityMember, MainScreen::class.java))
            }
        })

        partyName.text = intent.getStringExtra("title")
        venueName.text = intent.getStringExtra("pavadinimas")
        venuePrice.text = intent.getStringExtra("kaina")
        venueCount.text = intent.getStringExtra("zmones")

        //settings and onClickListener
        editDate.keyListener = null
        editLocation.keyListener = null
        editExpenses.keyListener = null
        editDescription.keyListener = null

        editDate.setText(intent.getStringExtra("data"))
        editDescription.setText(intent.getStringExtra("des"))
        editLocation.setText(intent.getStringExtra("location"))
        editExpenses.setText(intent.getStringExtra("expenses"))

        //download images from storage
        val photoID = intent.getStringExtra("id")!!.toString() + ".jpg"

        val storageReference = FirebaseStorage.getInstance().reference.child("images/").child(photoID)

        Glide.with(this)
            .load(storageReference)
            .fitCenter()
            .into(venueImage)

        venueMoreInfo.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val url = intent.getStringExtra("psl_nuoroda")
                val webIntent: Intent = Uri.parse(url).let{ webpage ->
                    Intent(Intent.ACTION_VIEW, webpage)
                }
                startActivity(webIntent)
            }
        })

    }

    private fun readData(eventUid: String?, firebaseCallback: FirebaseCallback){
        val eventMemberReference = FirebaseDatabase.getInstance().getReference("EventMembers").child(eventUid!!)

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val children = dataSnapshot.children
                children.forEach {
                    val member = it.getValue<Member>(Member::class.java)
                    memberList.add(member!!)
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

    override fun onBackPressed() {
        val intent = Intent(this, MainScreen::class.java)
        intent.flags  = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

}