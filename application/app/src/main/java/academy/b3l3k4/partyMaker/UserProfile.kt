package academy.b3l3k4.partyMaker

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class UserProfile:AppCompatActivity() {

    private var userPartyData: MutableList<Party> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_profile)

        //set buttons and views
        val username: TextView = findViewById(R.id.username)
        val userDescription: TextView = findViewById(R.id.userDescription)
        val activityList: ListView = findViewById(R.id.activityList)
        val userSettings: ImageButton = findViewById(R.id.profileSettings)

        //set TextViews
        if(intent.getStringExtra("userName") == null){
            val name = "${intent.getStringExtra("name")} ${intent.getStringExtra("secondName")}"
            username.setText(name)
            userDescription.setText(intent.getStringExtra("description"))
        } else{
            val name = "${intent.getStringExtra("userName")} ${intent.getStringExtra("userSecondName")}"
            username.setText(name)
            userDescription.setText((intent.getStringExtra("userDescription")))
        }

        val backwardArrow: ImageButton = findViewById(R.id.backwardArrow)

        //program backward arrow button
        backwardArrow.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                startActivity(Intent(this@UserProfile, MainScreen::class.java))
            }
        })

        //read data from data base, create array adapter and custom list view
        readData(object: FirebaseCallback{
            override fun onCallback(list: MutableList<Party>) {
                val arrayAdapter = CustomListAdapter(this@UserProfile, R.layout.custom_list, list)
                activityList.adapter = arrayAdapter
            }
        })

        //goto user profile settings
        userSettings.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                startActivity(Intent(this@UserProfile, UserSettings::class.java))
            }
        })

    }

    //read function to get user's events data from FireBase
    private fun readData(firebaseCallback: FirebaseCallback){
        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        val partyReference = FirebaseDatabase.getInstance().getReference("Events").child(userID)

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val children = dataSnapshot.children
                userPartyData.clear()
                children.forEach {
                    val party = it.getValue<Party>(Party::class.java)
                    userPartyData.add(party!!)
                }
                firebaseCallback.onCallback(userPartyData)
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e("DB download", p0.message)
            }
        }
        partyReference.addValueEventListener(valueEventListener)

    }

    //interface for obtaining data outside onDataChange method
    private interface FirebaseCallback{
        fun onCallback(list: MutableList<Party>)
    }
}