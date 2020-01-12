package academy.b3l3k4.partyMaker

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private var venueLs: MutableList<Party> = mutableListOf()

class FindVenue: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.venue_list)

        val venueView: ListView = findViewById(R.id.venueView)

        readData(object : FireBaseCallback{
            override fun onCallback(list: MutableList<Party>) {
                val arrayAdapter = CustomVenueListAdapter(this@FindVenue, R.layout.custom_venue_list, list)
                venueView.adapter = arrayAdapter
            }
        })

        venueView.setOnItemClickListener { adapterView, view, position, id ->
            val venueMoreInfo: TextView = view.findViewById(R.id.venueMoreInfo)
            val addVenue: TextView = view.findViewById(R.id.addVenue)

            val venue = adapterView.adapter.getItem(position).toString()
            val data = venue.split(",").toList()

            venueMoreInfo.setOnClickListener(object: View.OnClickListener{
                override fun onClick(v: View?) {
                    val webIntent = Intent(this@FindVenue, WebView::class.java)
                    webIntent.putExtra("URL", data[3])
                    startActivity(webIntent)
                }
            })

            addVenue.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    val intentVenue = Intent(this@FindVenue, StartNewEvent::class.java)
                    intentVenue.putExtra("pavadinimas", data[0])
                    intentVenue.putExtra("kaina", data[1])
                    intentVenue.putExtra("zmones", data[2])
                    intentVenue.putExtra("psl_nuoroda", data[3])
                    intentVenue.putExtra("foto_nuoroda", data[4])
                    intentVenue.putExtra("id", data[5])

                    if(intent.getStringArrayExtra("insertedData") != null){
                        val dataArray: Array<String> = intent.getStringArrayExtra("insertedData")!!
                        intentVenue.putExtra("insertedData", dataArray)
                    }
                    print(intentVenue.getStringExtra("pavadinimas"))
                    startActivity(intentVenue)
                }
            })
        }
    }

    private fun readData(firebaseCallback: FireBaseCallback){
        val venueReference = FirebaseDatabase.getInstance().getReference("Skelbimai")

        val eventListener = object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val children = dataSnapshot.children
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
