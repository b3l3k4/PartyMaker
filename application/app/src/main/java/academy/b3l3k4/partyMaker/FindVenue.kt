package academy.b3l3k4.partyMaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ListView
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private var venueLs: MutableList<Party> = mutableListOf()

class FindVenue: AppCompatActivity() {

    private lateinit var venueAdapter: CustomVenueRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.venue_list)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        val backwardButton: ImageButton = findViewById(R.id.backwardArrow)

        backwardButton.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                startActivity(Intent(this@FindVenue, StartNewEvent::class.java))
            }
        })

        val venueList: RecyclerView = findViewById(R.id.venueListRecycler)

        readData(object: FireBaseCallback{
            override fun onCallback(list: MutableList<Party>) {
                venueList.apply {
                    layoutManager = LinearLayoutManager(this@FindVenue)
                    val topSpacingItemDecoration = TopSpacingItemDecoration(30)
                    addItemDecoration(topSpacingItemDecoration)
                    venueAdapter = CustomVenueRecyclerAdapter(list)
                    adapter = venueAdapter
                }

                venueAdapter.setListener(object : CustomVenueRecyclerAdapter.OnClickListener{

                    override fun onAddButtonClicked(venue: Party) {
                        val intentVenue = Intent(this@FindVenue, StartNewEvent::class.java)
                        intentVenue.putExtra("pavadinimas", venue.name)
                        intentVenue.putExtra("kaina", venue.price)
                        intentVenue.putExtra("zmones", venue.max_capacity)
                        intentVenue.putExtra("psl_nuoroda", venue.advert_url)
                        intentVenue.putExtra("foto_nuoroda", venue.image_url)
                        intentVenue.putExtra("id", venue.id)

                        if(intent.getStringArrayExtra("insertedData") != null){
                            val dataArray: Array<String> = intent.getStringArrayExtra("insertedData")!!
                            intentVenue.putExtra("insertedData", dataArray)
                        }

                        startActivity(intentVenue)
                    }

                    override fun onMoreInfoButtonClicked(url: String?) {
                        val webIntent: Intent = Uri.parse(url).let { webpage ->
                            Intent(Intent.ACTION_VIEW, webpage)
                        }
                        startActivity(webIntent)
                    }
                })
            }
        })

    }

    private fun readData(firebaseCallback: FireBaseCallback){
        val venueReference = FirebaseDatabase.getInstance().getReference("Skelbimai")

        val eventListener = object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val children = dataSnapshot.children
                venueLs.clear()
                children.forEach {
                    val venueInfo = it.getValue<Party>(Party::class.java)
                    venueLs.add(venueInfo!!)
                }
                firebaseCallback.onCallback(venueLs)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        }
        venueReference.addListenerForSingleValueEvent(eventListener)
    }

    private interface FireBaseCallback{
        fun onCallback(list: MutableList<Party>)
    }
}
