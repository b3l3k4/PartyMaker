package academy.b3l3k4.partyMaker

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage

class CustomListAdapter(context: Context, private val resource: Int, private val partyInfo: List<Party>): ArrayAdapter<Party>(context, resource, partyInfo) {
    val inflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return partyInfo.count()
    }

    @SuppressLint("SetTextI18n")
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
        val venueImage: ImageView = view.findViewById(R.id.venueImage)

        val currentParty = partyInfo[position]

        val imageID = currentParty.id.toString() + ".jpg"

        val storageReference = FirebaseStorage.getInstance().reference.child("images/").child(imageID)

        Glide.with(context)
            .load(storageReference)
            .fitCenter()
            .into(venueImage)

        partyName.text = currentParty.title

        if(currentParty.approximatePrice!!.equals("") && currentParty.price!!.equals("Nenurodyta")) {
            venuePrice.text = "Price unspecified"
        } else if(currentParty.approximatePrice!!.equals("")){
            venuePrice.text = "${currentParty.price}"
        } else{
            venuePrice.text = "${currentParty.approximatePrice} €"
        }

        if(currentParty.max_capacity!!.equals("Nenurodyta")){
            venueCount.text = "Capacity unspecified"
        } else {
            venueCount.text = currentParty.max_capacity + " attendants"
        }

        return view
    }
}