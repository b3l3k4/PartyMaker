package academy.b3l3k4.partyMaker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class CustomMemberListAdapter(context: Context, private var resource: Int, private var member: List<Member>): ArrayAdapter<Member>(context, resource, member) {
    val inflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return member.count()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View

        if(convertView == null){
            view = inflater.inflate(resource, parent, false)
        } else{
            view = convertView
        }

        val memberName: TextView = view.findViewById(R.id.memberName)

        val currentMember = member[position]

        memberName.text = currentMember.userFullName

        return view
    }
}