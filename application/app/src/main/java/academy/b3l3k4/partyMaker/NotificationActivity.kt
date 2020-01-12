package academy.b3l3k4.partyMaker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.notification_activity.*

val notifications: MutableList<Notifications> = mutableListOf()

class NotificationActivity: AppCompatActivity() {
    private lateinit var notificationAdapter: NotificationAdapterTest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notification_activity)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        val backButton: ImageButton = findViewById(R.id.backwardArrow)

        backButton.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                startActivity(Intent(this@NotificationActivity,MainScreen::class.java))
            }
        })

        val notificationList: RecyclerView = findViewById(R.id.notification_list_view)

        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        val notificationReference = FirebaseDatabase.getInstance().getReference("Notifications").child(currentUser)

        readData(object: FirebaseCallback{
            override fun onCallback(list: MutableList<Notifications>) {
                notificationList.apply {
                    layoutManager = LinearLayoutManager(this@NotificationActivity)
                    val topSpacingItemDecoration = TopSpacingItemDecoration(30)
                    addItemDecoration(topSpacingItemDecoration)
                    notificationAdapter = NotificationAdapterTest(list)
                    notificationList.adapter = notificationAdapter
                }

                notificationAdapter.setListener(object : NotificationAdapterTest.OnClickListener{
                    override fun onAcceptButtonClick(member: Member, eventId: String?, partyReference: DatabaseReference, eventReference: DatabaseReference, memberReference: DatabaseReference) {
                        memberReference.child(currentUser).setValue(member)

                        partyReference.addValueEventListener(object: ValueEventListener{
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val party = dataSnapshot.getValue<Party>(Party::class.java)
                                eventReference.child(eventId!!).setValue(party)
                            }

                            override fun onCancelled(p0: DatabaseError) {
                                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                            }
                        })

                        notificationReference.child(eventId!!).removeValue()

                        Toast.makeText(this@NotificationActivity, "Invitation has been accepted", Toast.LENGTH_SHORT).show()

                        notificationReference.addValueEventListener(object: ValueEventListener{
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val children = dataSnapshot.children
                                var count = 0
                                children.forEach {
                                    count++
                                }
                                if(count == 0){
                                    finish()
                                    startActivity(Intent(this@NotificationActivity, NotificationActivityEmpty::class.java))
                                } else{
                                    finish()
                                    startActivity(Intent(this@NotificationActivity, NotificationActivity::class.java))
                                }
                            }

                            override fun onCancelled(p0: DatabaseError) {
                                Log.e("DB", p0.message)
                            }
                        })

//                        finish()
//                        startActivity(Intent(this@NotificationActivity, NotificationActivity::class.java))
                    }

                    override fun onRemoveButtonClick(eventId: String?) {
                        notificationReference.child(eventId!!).removeValue()

                        Toast.makeText(this@NotificationActivity, "Invitation has been removed", Toast.LENGTH_SHORT).show()

                        notificationReference.addValueEventListener(object: ValueEventListener{
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val children = dataSnapshot.children
                                var count = 0
                                children.forEach {
                                    count++
                                }
                                if(count == 0){
                                    finish()
                                    startActivity(Intent(this@NotificationActivity, NotificationActivityEmpty::class.java))
                                } else{
                                    finish()
                                    startActivity(Intent(this@NotificationActivity, NotificationActivity::class.java))
                                }
                            }

                            override fun onCancelled(p0: DatabaseError) {
                                Log.e("DB", p0.message)
                            }
                        })

//                        finish()
//                        startActivity(Intent(this@NotificationActivity, NotificationActivity::class.java))

                    }
                })
            }
        })
    }


    private fun readData(firebaseCallback: FirebaseCallback){
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        val notificationReference = FirebaseDatabase.getInstance().getReference("Notifications").child(currentUser)

        val valueEventListener = object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                notifications.clear()
                val children = dataSnapshot.children
                children.forEach {
                    val data = it.getValue<Notifications>(Notifications::class.java)
                    notifications.add(data!!)
                }
                firebaseCallback.onCallback(notifications)
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
        notificationReference.addListenerForSingleValueEvent(valueEventListener)
    }


    private interface FirebaseCallback{
        fun onCallback(list: MutableList<Notifications>)
    }
}