package academy.b3l3k4.partyMaker

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class CustomUserListAdapter(context: Context, private var resource: Int, private val userList: List<User>): ArrayAdapter<User>(context, resource, userList) {
    val inflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return userList.count()
    }

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View

        if(convertView == null){
            view = inflater.inflate(resource, parent,false)
        } else{
            view = convertView
        }

        val userName: TextView = view.findViewById(R.id.userName)

        val currentUser = userList[position]

        userName.text = "${currentUser.firstName} ${currentUser.lastName}"



        return view
    }
}