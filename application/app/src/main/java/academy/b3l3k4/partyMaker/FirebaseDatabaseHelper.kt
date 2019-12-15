package academy.b3l3k4.partyMaker

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import java.util.ArrayList

class FirebaseDatabaseHelper {
    private val mDatabase: FirebaseDatabase
    private val mReferenceParty: DatabaseReference
    private val partyList = ArrayList<Party>()

    interface DataStatus {
        fun DataLoaded(party: List<Party>, keys: List<String>)
        fun DataIsInserted()
        fun DataIsUpdated()
        fun DataIsDeleted()
    }

    init {
        mDatabase = FirebaseDatabase.getInstance()
        mReferenceParty = mDatabase.reference.child("Skelbimai")
    }

    fun readParty(dataStatus: DataStatus) {
        mReferenceParty.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                partyList.clear()
                val keys = ArrayList<String>()
                for (keyNode in dataSnapshot.children) {
                    keys.add(keyNode.key.toString())
                    val party = keyNode.getValue(Party::class.java)
                    partyList.add(party!!)
                }
                dataStatus.DataLoaded(partyList, keys)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
}
