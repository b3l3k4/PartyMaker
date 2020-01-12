package academy.b3l3k4.partyMaker

import android.text.Editable
import android.os.Bundle
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mancj.materialsearchbar.MaterialSearchBar

private var userList: MutableList<User> = mutableListOf()

class AddNewMember: AppCompatActivity() {
    private lateinit var userAdapter: CustomUserRecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_new_member)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        val searchBar: MaterialSearchBar = findViewById(R.id.searchBar)

        searchBar.setHint("Search..")
        searchBar.setSpeechMode(true)

        val userList: RecyclerView = findViewById(R.id.userList)

        //read data from DB and add adapter

        val notificationReference = FirebaseDatabase.getInstance().reference.child("Notifications")

        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        val currentEvent = intent.getStringExtra("uid")!!.toString()
        val currentEventTitle = intent.getStringExtra("title")!!.toString()

        readData(object : FirebaseCallback{
            override fun onCallback(list: List<User>) {
                val sortedList = list.sortedBy { it.firstName }
                userList.apply {
                    layoutManager = LinearLayoutManager(this@AddNewMember)
                    userAdapter = CustomUserRecyclerView(sortedList)
                    adapter = userAdapter
                }

                userAdapter.setListener(object : CustomUserRecyclerView.OnClickListener{
                    override fun onAddButtonClicked(uid: String?, firstName: String?) {
                        Toast.makeText(this@AddNewMember, "${firstName} has been invited to join your party", Toast.LENGTH_SHORT).show()
                        val notification = Notifications(currentUser, "member", "pending", currentEvent, currentEventTitle)
                        notificationReference.child(uid!!).child(currentEvent).setValue(notification)
                    }
                })
            }
        })

        //search bar
        searchBar.addTextChangeListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                readData(object: FirebaseCallback{
                    override fun onCallback(list: List<User>) {
                        val filteredList: MutableList<User> = mutableListOf()
                        val sortedList = list.sortedBy { it.firstName }
                        var size  = sortedList.size - 1

                        while(size >= 0){
                            val fullName = "${sortedList[size].firstName} ${sortedList[size].lastName}"
                            if(fullName.startsWith(charSequence!!,ignoreCase = true)){
                                filteredList.add(sortedList[size])
                            }
                            size--
                        }

                        filteredList.sortBy { it.firstName }

                        userList.apply {
                            layoutManager = LinearLayoutManager(this@AddNewMember)
                            userAdapter = CustomUserRecyclerView(filteredList)
                            adapter = userAdapter
                        }

                        userAdapter.setListener(object : CustomUserRecyclerView.OnClickListener{
                            override fun onAddButtonClicked(uid: String?, firstName: String?) {
                                Toast.makeText(this@AddNewMember, "${firstName} has been invited to join your party", Toast.LENGTH_SHORT).show()
                                val notification = Notifications(currentUser, "member", "pending", currentEvent, currentEventTitle)
                                notificationReference.child(uid!!).child(currentEvent).setValue(notification)
                            }
                        })

                    }
                })
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }


    private fun readData(firebaseCallback: FirebaseCallback){
        val userReference = FirebaseDatabase.getInstance().getReference("Users")
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid

        val eventListener = object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot){
                val children = dataSnapshot.children
                userList.clear()
                children.forEach {
                    val userData = it.getValue<User>(User::class.java)
                    if(userData!!.uid != currentUser){
                        userList.add(userData)
                    }
                }
                firebaseCallback.onCallback(userList)
            }

            override fun onCancelled(p0: DatabaseError) {
                print(p0.message)
            }
        }
        userReference.addListenerForSingleValueEvent(eventListener)
    }


    private interface FirebaseCallback{
        fun onCallback(list: List<User>)
    }
}