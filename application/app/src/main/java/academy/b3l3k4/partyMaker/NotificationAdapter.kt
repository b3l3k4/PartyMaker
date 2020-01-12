package academy.b3l3k4.partyMaker

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.notification_list.view.*

class NotificationAdapter(private var list: List<Notifications>):RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NotificationListHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.legit_list, parent, false)
        )
    }

    private var listener: View.OnClickListener? = null

    fun setListener(listener: View.OnClickListener){
        this.listener = listener
    }

    interface OnClickListener{
        fun onAcceptButtonClick()
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is NotificationListHolder ->{
                holder.bind(list[position])

                val accept: TextView = holder.getAccept()
                val remove: TextView = holder.getRemove()

                val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
                val eventOwner = list[position].owner.toString()
                val eventId = list[position].eventId.toString()
                val eventReference = FirebaseDatabase.getInstance().getReference("Events").child(currentUser)
                val notificationReference = FirebaseDatabase.getInstance().getReference("Notifications").child(currentUser)
                val memberReference = FirebaseDatabase.getInstance().getReference("EventMembers").child(eventId)
                val partyReference = FirebaseDatabase.getInstance().getReference("Events").child(eventOwner).child(eventId)

//                val member = Member("member", currentUser)

                accept.setOnClickListener(object : View.OnClickListener{
                    override fun onClick(v: View?) {
//                        memberReference.child(currentUser).setValue(member)

                        partyReference.addValueEventListener(object: ValueEventListener{
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val party = dataSnapshot.getValue<Party>(Party::class.java)
                                eventReference.child(eventId).setValue(party)
                            }

                            override fun onCancelled(p0: DatabaseError) {
                                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                            }
                        })

                        notificationReference.child(eventId).removeValue()
                    }
                })


//                remove.setOnClickListener(object : View.OnClickListener{
//                    override fun onClick(v: View?) {
//                        notificationReference.child(eventId).removeValue()
//                    }
//                })
            }
        }

    }


    override fun getItemCount(): Int {
        return list.size
    }


    class NotificationListHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView){
        val message: TextView = itemView.message

        @SuppressLint("SetTextI18n")
        fun bind(notifications: Notifications){
            message.text = "You've received an invitation to ${notifications.eventTitle}"
        }

        fun getAccept(): TextView{
            return itemView.findViewById(R.id.accept)
        }

        fun getRemove(): TextView{
            return itemView.findViewById(R.id.delete)
        }

    }
}
