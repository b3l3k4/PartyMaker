package academy.b3l3k4.partyMaker


import android.content.Intent
import android.os.Bundle
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


private var party: MutableList<Party> = mutableListOf()

class MainScreen: AppCompatActivity(){

    private var drawer: DrawerLayout? = null
    private var TAG = "PARTYINFO"
    private var mDatabaseReference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_screen)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        window.decorView.fitsSystemWindows
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
//                val arrayAdapter = CustomVenueListAdapter(this@MainScreen, R.layout.custom_venue_list,list)
                partyView.adapter = arrayAdapter
            }
        })

        partyView.setOnItemClickListener { adapterView, view, position, id ->
            val itemId = adapterView.getItemIdAtPosition(position)
            Toast.makeText(this, itemId.toString(), Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, EventActivity::class.java))
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
                else -> {
                    false
                }
            }
        }

        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer!!.addDrawerListener(toggle)
        toggle.syncState()
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
                    party.add(partyInfo!!)
                }
                firebaseCallback.onCallback(party)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        }
        partyReference.addListenerForSingleValueEvent(valueEventListener)
    }


    private interface FirebaseCallback{
        fun onCallback(list: MutableList<Party>)
    }


    override fun onBackPressed() {
        if(drawer!!.isDrawerOpen(GravityCompat.START)){
            drawer!!.closeDrawer(GravityCompat.START)
        } else{
            val intent = Intent(this, MainScreen::class.java)
            startActivity(intent)
        }
    }
}