package academy.b3l3k4.partyMaker

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.notification_list.view.*

class NotificationAdapterTest(private var list: List<Notifications>):RecyclerView.Adapter<NotificationAdapterTest.NotificationListHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationListHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.legit_list, parent, false)
        return NotificationListHolder(view)
    }


    override fun getItemCount(): Int {
        return list.size
    }

    private var listener: OnClickListener? = null


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NotificationListHolder, position: Int) {
        val notification: Notifications = list[position]

        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        val eventId = list[position].eventId.toString()
        val eventOwner = list[position].owner.toString()

        val userReference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser)

        val partyReference = FirebaseDatabase.getInstance().getReference("Events").child(eventOwner).child(eventId)
        val eventReference = FirebaseDatabase.getInstance().getReference("Events").child(currentUser)
        val memberReference = FirebaseDatabase.getInstance().getReference("EventMembers").child(eventId)

        holder.itemView.message.text = "You've received an invitation to ${notification.eventTitle}"

        holder.itemView.accept.setOnClickListener{
            userReference.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val userInfo = dataSnapshot.getValue<User>(User::class.java)
                    val fullName = userInfo!!.firstName + " " + userInfo.lastName
                    val member = Member( "member", currentUser, fullName, notification.owner)
                    listener?.onAcceptButtonClick(member, eventId, partyReference, eventReference, memberReference)
                }

                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })
        }

        holder.itemView.delete.setOnClickListener {
            listener?.onRemoveButtonClick(eventId)
        }

    }


    fun setListener(listener: OnClickListener){
        this.listener = listener
    }

    interface OnClickListener{
        fun onAcceptButtonClick(member: Member, eventId: String?, partyReference: DatabaseReference, eventReference: DatabaseReference, memberReference: DatabaseReference)
        fun onRemoveButtonClick(eventId: String?)
    }


    class NotificationListHolder(view: View): RecyclerView.ViewHolder(view)
}
