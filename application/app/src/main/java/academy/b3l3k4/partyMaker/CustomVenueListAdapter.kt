package academy.b3l3k4.partyMaker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class CustomVenueListAdapter(context: Context, private val resource: Int, private val venueList: List<Party>): ArrayAdapter<Party>(context, resource, venueList) {
    val inflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return venueList.count()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View

        if(convertView == null){
            view = inflater.inflate(resource, parent, false)

        } else{
            view = convertView
        }

        val venueName: TextView = view.findViewById(R.id.venueName)
        val venuePrice: TextView = view.findViewById(R.id.venuePrice)
        val venueCount: TextView = view.findViewById(R.id.venueCount)

        val currentVenue = venueList[position]

        venueName.text = currentVenue.name
        venuePrice.text = currentVenue.price
        venueCount.text = "${currentVenue.max_capacity} attendants"


        return view
    }

}