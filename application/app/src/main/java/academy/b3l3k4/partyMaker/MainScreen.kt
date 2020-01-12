package academy.b3l3k4.partyMaker


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.main_screen.*


private var party: MutableList<Party> = mutableListOf()

class MainScreen: AppCompatActivity(){

    private var drawer: DrawerLayout? = null
    private var mDatabaseReference: DatabaseReference? = null
    private var backPressedTime: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_screen)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        val toolbar: Toolbar? = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle(null)

        drawer = findViewById(R.id.drawer_layout)

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        val partyView: ListView = findViewById(R.id.partyView)

        readData(object : FirebaseCallback{
            override fun onCallback(list: MutableList<Party>) {
                val arrayAdapter = CustomListAdapter(this@MainScreen, R.layout.custom_list, list)
                partyView.adapter = arrayAdapter
            }
        })

        partyView.setOnItemClickListener { adapterView, _, position, _ ->

            val intentAdmin = Intent(this@MainScreen, EventActivity::class.java)
            val intentMember = Intent(this@MainScreen, EventActivityMember::class.java)
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
            intentAdmin.putExtra("location", data[10])
            intentAdmin.putExtra("expenses", data[11])
            intentAdmin.putExtra("position", position)

            val memberReference = FirebaseDatabase.getInstance().getReference("EventMembers").child(data[9]).child(userId)

            readDataMember(memberReference, object : FirebaseCallbackMember {
                override fun onCallback(member: Member) {
                    val eventReference = FirebaseDatabase.getInstance().getReference("Events").child(member.adminUID!!).child(data[9])
                    eventReference.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val party = dataSnapshot.getValue<Party>(Party::class.java)
                            intentMember.putExtra("pavadinimas", party!!.name)
                            intentMember.putExtra("kaina", party.price)
                            intentMember.putExtra("zmones", party.max_capacity)
                            intentMember.putExtra("psl_nuoroda", party.advert_url)
                            intentMember.putExtra("foto_nuoroda", party.image_url)
                            intentMember.putExtra("id", party.id)
                            intentMember.putExtra("data", party.date)
                            intentMember.putExtra("title", party.title)
                            intentMember.putExtra("des", party.description)
                            intentMember.putExtra("uid", party.uid)
                            intentMember.putExtra("location", party.location)
                            intentMember.putExtra("expenses", party.expenses)
                            intentMember.putExtra("position", position)

                            if(member.permission == "member"){
                                startActivity(intentMember)
                            } else{
                                startActivity(intentAdmin)
                            }
                        }

                        override fun onCancelled(p0: DatabaseError) {
                            Log.e("DB", p0.message)
                        }
                    })
                }
            })
        }

        val intent = Intent(this@MainScreen, UserProfile::class.java)

        mDatabaseReference!!.child(userId).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                intent.putExtra("name", user!!.firstName)
                intent.putExtra("secondName", user.lastName)
                intent.putExtra("description", user.description)
            }
            override fun onCancelled(p0: DatabaseError) {}
        })

        val notificationReference = FirebaseDatabase.getInstance().getReference("Notifications").child(userId)

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener{menuItem ->
            when(menuItem.itemId){
                R.id.nav_person -> {
                    if(drawer!!.isDrawerOpen(GravityCompat.START)){
                        drawer!!.closeDrawer(GravityCompat.START)
                    }
                    startActivity(intent)
                    true
                }
                R.id.nav_settings ->{
                    if(drawer!!.isDrawerOpen(GravityCompat.START)){
                        drawer!!.closeDrawer(GravityCompat.START)
                    }
                    startActivity(Intent(this@MainScreen, UserSettings::class.java))
                    true
                }
                R.id.nav_createEvent ->{
                    if(drawer!!.isDrawerOpen(GravityCompat.START)){
                        drawer!!.closeDrawer(GravityCompat.START)
                    }
                    startActivity(Intent(this@MainScreen, StartNewEvent::class.java))
                    true
                }
                R.id.logout_user ->{
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this@MainScreen, MainActivity::class.java))
                    true
                }
                R.id.nav_npotifications ->{
                    if(drawer!!.isDrawerOpen(GravityCompat.START)){
                        drawer!!.closeDrawer(GravityCompat.START)
                    }

                    notificationReference.addValueEventListener(object: ValueEventListener{
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val children = dataSnapshot.children
                            var count = 0
                            children.forEach {
                                count++
                            }
                            if(count == 0) startActivity(Intent(this@MainScreen, NotificationActivityEmpty::class.java))
                            else startActivity(Intent(this@MainScreen, NotificationActivity::class.java))
                        }

                        override fun onCancelled(p0: DatabaseError) {
                            Log.e("DB", p0.message)
                        }
                    })

                    true
                }
                else -> {
                    false
                }
            }
        }

        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer!!.addDrawerListener(toggle)
        toggle.syncState()

        if(drawer!!.isDrawerOpen(GravityCompat.START)){
            val userEmail: TextView = drawer_layout.findViewById(R.id.userEmail)
            val userReference = FirebaseDatabase.getInstance().getReference("Users").child(userId)

            userReference.addValueEventListener(object: ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue<User>(User::class.java)
                    userEmail.text = user!!.email
                }

                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })
        }

    }

    override fun onResume() {
        super.onResume()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        window.decorView.fitsSystemWindows
        actionBar?.hide()

    }

    private fun readData(firebaseCallback: FirebaseCallback){
        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        val partyReference = FirebaseDatabase.getInstance().getReference("Events").child(userID)

        val valueEventListener = object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val children = dataSnapshot.children
                party.clear()
                children.forEach {
                    val partyInfo = it.getValue<Party>(Party::class.java)
                    println(partyInfo!!.title)
                    party.add(partyInfo)
                }
                firebaseCallback.onCallback(party)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        }
        partyReference.addListenerForSingleValueEvent(valueEventListener)
    }

    private fun readDataMember(memberReference: DatabaseReference, firebaseCallbackMember: FirebaseCallbackMember){
        memberReference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.getValue<Member>(Member::class.java)
                firebaseCallbackMember.onCallback(data!!)
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e("DBcancel", p0.message)
            }
        })
    }


    private interface FirebaseCallback{
        fun onCallback(list: MutableList<Party>)
    }

    private interface FirebaseCallbackMember{
        fun onCallback(member: Member)
    }


    override fun onBackPressed() {

        if(drawer!!.isDrawerOpen(GravityCompat.START)){
            drawer!!.closeDrawer(GravityCompat.START)
        } else if(!drawer!!.isDrawerOpen(GravityCompat.START) && backPressedTime!!.plus(2000) > System.currentTimeMillis()){
            super.onBackPressed()
            return
        } else{
            Toast.makeText(this, "Please click back again to EXIT", Toast.LENGTH_SHORT).show()
        }
    }
}