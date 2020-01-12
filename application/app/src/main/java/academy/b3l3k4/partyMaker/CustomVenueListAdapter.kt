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
import com.bumptech.glide.Registry
import com.bumptech.glide.module.AppGlideModule
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso


class CustomVenueListAdapter(context: Context, private val resource: Int, private val venueList: List<Party>): ArrayAdapter<Party>(context, resource, venueList) {
    val inflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return venueList.count()
    }

    @SuppressLint("SetTextI18n")
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
        val venueImage: ImageView = view.findViewById(R.id.venueImage)

        val currentVenue = venueList[position]

        venueName.text = currentVenue.name

        val imageID = currentVenue.id.toString() + ".jpg"

        val storageReference = FirebaseStorage.getInstance().reference.child("images/").child(imageID)

        Glide.with(context)
            .load(storageReference)
            .fitCenter()
            .into(venueImage)


        if(currentVenue.price!!.equals("Nenurodyta")){
            venuePrice.text = "Price unspecified"
        } else{
            venuePrice.text = currentVenue.price
        }

        if(currentVenue.max_capacity!!.equals("Nenurodyta")){
            venueCount.text = "Capacity unspecified"
        } else{
            venueCount.text = "${currentVenue.max_capacity} attendants"
        }

        return view
    }

}