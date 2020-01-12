package academy.b3l3k4.partyMaker

import android.content.Intent
import android.os.Bundle
import android.renderscript.Sampler
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

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        //set buttons and views
        val username: TextView = findViewById(R.id.username)
        val userDescription: TextView = findViewById(R.id.userDescription)
        val activityList: ListView = findViewById(R.id.activityList)
        val userSettings: ImageButton = findViewById(R.id.profileSettings)

        //set TextViews
        if(intent.getStringExtra("userName") == null){
            val name = "${intent.getStringExtra("name")} ${intent.getStringExtra("secondName")}"
            username.setText(name)
//            userDescription.setText(intent.getStringExtra("description"))
        } else{
            val name = "${intent.getStringExtra("userName")} ${intent.getStringExtra("userSecondName")}"
            username.setText(name)
//            userDescription.setText((intent.getStringExtra("userDescription")))
        }

        if(intent.getStringExtra("userDescription") == null){
            userDescription.setText(intent.getStringExtra("description"))
        } else{
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

        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        activityList.setOnItemClickListener { adapterView, _, position, _ ->

            val intentAdmin = Intent(this@UserProfile, EventActivity::class.java)
            val intentMember = Intent(this@UserProfile, EventActivityMember::class.java)
            val venue = adapterView.adapter.getItem(position).toString()
            val data = venue.split(",")

            intentAdmin.putExtra("pavadinimas", data[0])
            intentAdmin.putExtra("kaina", data[1])
            intentAdmin.putExtra("zmones", data[2])
            intentAdmin.putExtra("psl_nuoroda", data[3])
            intentAdmin.putExtra("foto_nuoroda", data[4])
            intentAdmin.putExtra("id", data[5])
            intentAdmin.putExtra("data", data[6])
            intentAdmin.putExtra("title", data[7])
            intentAdmin.putExtra("des", data[8])
            intentAdmin.putExtra("uid", data[9])
            intentAdmin.putExtra("position", position)

            intentMember.putExtra("pavadinimas", data[0])
            intentMember.putExtra("kaina", data[1])
            intentMember.putExtra("zmones", data[2])
            intentMember.putExtra("psl_nuoroda", data[3])
            intentMember.putExtra("foto_nuoroda", data[4])
            intentMember.putExtra("id", data[5])
            intentMember.putExtra("data", data[6])
            intentMember.putExtra("title", data[7])
            intentMember.putExtra("des", data[8])
            intentMember.putExtra("uid", data[9])
            intentMember.putExtra("position", position)

            val memberReference = FirebaseDatabase.getInstance().getReference("EventMembers").child(data[9]).child(userId)

            readDataMember(memberReference, object: FirebaseCallbackMember{
                override fun onCallBack(member: Member) {
                    if(member.permission == "member"){
                        startActivity(intentMember)
                    } else{
                        startActivity(intentAdmin)
                    }
                }
            })
        }

        val intentSettings = Intent(this, UserSettings::class.java)
        val nameUser = username.text.toString()
        val descriptionUser = userDescription.text.toString()
        intentSettings.putExtra("nameUser", nameUser)
        intentSettings.putExtra("descriptionUser", descriptionUser)

        userSettings.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                startActivity(intentSettings)
            }
        })

        //goto user profile settings
//        userSettings.setOnClickListener(object : View.OnClickListener {
//            override fun onClick(v: View?) {
//                readDataUser(object : FirebaseCallbackUser{
//                    override fun onCallBack(user: User) {
//                        val intentUser = Intent(this@UserProfile, UserSettings::class.java)
//                        val fullName = "${user.firstName} ${user.lastName}"
//                        val description = user.description.toString()
//                        intentUser.putExtra("nameUser", fullName)
//                        intentUser.putExtra("descriptionUser", description)
//                        startActivity(Intent(this@UserProfile, UserSettings::class.java))
//                    }
//                })
//            }
//        })

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


    private fun readDataMember(databaseReference: DatabaseReference, firebaseCallbackMember: FirebaseCallbackMember){
        databaseReference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.getValue<Member>(Member::class.java)
                firebaseCallbackMember.onCallBack(data!!)
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e("DB", p0.message)
            }
        })
    }


    private fun readDataUser(firebaseCallbackUser: FirebaseCallbackUser){
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val userReference = FirebaseDatabase.getInstance().getReference("Users").child(userId)

        userReference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.getValue(User::class.java)
                firebaseCallbackUser.onCallBack(data!!)
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e("DB", p0.message)
            }
        })

    }

    //interface for obtaining data outside onDataChange method
    private interface FirebaseCallback{
        fun onCallback(list: MutableList<Party>)
    }


    private interface FirebaseCallbackMember{
        fun onCallBack(member: Member)
    }


    private interface FirebaseCallbackUser{
        fun onCallBack(user: User)
    }
}