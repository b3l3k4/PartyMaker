package academy.b3l3k4.partyMaker

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.venue_list_recycler.view.*

class CustomVenueRecyclerAdapter(private val venueList: List<Party>): RecyclerView.Adapter<CustomVenueRecyclerAdapter.VenueListHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VenueListHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.venue_list_recycler, parent, false)
        return VenueListHolder(view)
    }

    private var listener: OnClickListener? = null

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VenueListHolder, position: Int) {
        val venue: Party = venueList[position]

        holder.itemView.venueName.text = venue.name

        if(venue.price!!.equals("Nenurodyta")){
            holder.itemView.venuePrice.text = "Price unspecified"
        } else{
            holder.itemView.venuePrice.text = venue.price
        }

        if(venue.max_capacity!!.equals("Nenurodyta")){
            holder.itemView.venueCount.text = "Capacity unspecified"
        } else{
            holder.itemView.venueCount.text = "${venue.max_capacity} attendants"
        }

        val imageID = venue.id.toString() + ".jpg"
        val storageReference = FirebaseStorage.getInstance().reference.child("images/").child(imageID)


        Glide.with(holder.itemView.venueImage.context)
            .load(storageReference)
            .fitCenter()
            .into(holder.itemView.venueImage)


        holder.itemView.venueMoreInfo.setOnClickListener {
            listener?.onMoreInfoButtonClicked(venue.advert_url)
        }

        holder.itemView.addVenue.setOnClickListener {
            listener?.onAddButtonClicked(venue)
        }

    }

    fun setListener(listener: OnClickListener){
        this.listener = listener
    }

    interface OnClickListener{
        fun onAddButtonClicked(venue: Party)
        fun onMoreInfoButtonClicked(url: String?)
    }

    override fun getItemCount(): Int {
        return venueList.size
    }

    class VenueListHolder(view: View): RecyclerView.ViewHolder(view)
}