package academy.b3l3k4.partyMaker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class CustomListAdapter(context: Context, private val resource: Int, private val partyInfo: List<Party>): ArrayAdapter<Party>(context, resource, partyInfo) {
    val inflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return partyInfo.count()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View

        if(convertView == null) {
            view = inflater.inflate(resource, parent, false)
        } else{
            view = convertView
        }

        val partyName: TextView = view.findViewById(R.id.venueName)
        val venuePrice: TextView = view.findViewById(R.id.venuePrice)
        val venueCount: TextView = view.findViewById(R.id.venueCount)

        val currentParty = partyInfo[position]

        partyName.text = currentParty.title
        venuePrice.text = "${currentParty.approximatePrice} $"
        venueCount.text = currentParty.max_capacity

        return view
    }
}