package academy.b3l3k4.partyMaker

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.custom_user_list.view.*

class CustomUserRecyclerView(private val user: List<User>): RecyclerView.Adapter<CustomUserRecyclerView.UserListHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_user_list, parent, false)
        return  UserListHolder(view)
    }

    override fun getItemCount(): Int {
        return user.size
    }

    private var listener: OnClickListener? = null

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UserListHolder, position: Int) {
        val userData: User = user[position]

        holder.itemView.userName.text = "${userData.firstName} ${userData.lastName}"

        holder.itemView.addVenue.setOnClickListener {
            listener?.onAddButtonClicked(userData.uid, userData.firstName)
        }
    }

    fun setListener(listener: OnClickListener){
        this.listener = listener
    }

    interface OnClickListener{
        fun onAddButtonClicked(uid: String?, firstName: String?)
    }

    class UserListHolder(view: View) : RecyclerView.ViewHolder(view)
}