package academy.b3l3k4.partyMaker

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserSettings:AppCompatActivity() {

    var userName: EditText? = null
    var oldPass: EditText? = null
    var newPass: EditText? = null
    var userDes: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_settings)

        userName = findViewById(R.id.userName)
        oldPass= findViewById(R.id.oldPass)
        newPass = findViewById(R.id.newPass)
        userDes = findViewById(R.id.userDes)
        val saveChangesButton: Button = findViewById(R.id.saveChanges)

        saveChangesButton.setOnClickListener{sendData()}
    }

    private fun sendData(){
        val name = userName!!.text.toString()
        val old = oldPass!!.text.toString()
        val new = newPass!!.text.toString()
        val des = userDes!!.text.toString()

        val splitName = name.split(" ").toList()

        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        val userReference = FirebaseDatabase.getInstance().getReference("Users").child(userID)
        val mAuth = FirebaseAuth.getInstance()

        if(!TextUtils.isEmpty(name)){
            userReference.child("firstName").setValue(splitName[0])
            userReference.child("lastName").setValue(splitName[1])
        }
        if(!TextUtils.isEmpty(des)){
            userReference.child("description").setValue(des)
        }

        if(!TextUtils.isEmpty(old) && !TextUtils.isEmpty(new)){
            userReference.addValueEventListener(object: ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val userInfo = dataSnapshot.getValue(User::class.java)
                    if(userInfo!!.password == old){
                        mAuth.currentUser!!.updatePassword(new)
                        userReference.child("password").setValue(new)
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.e("DB failed download", p0.message)
                }
            })
        }

        val intent = Intent(this@UserSettings, UserProfile::class.java)
        intent.putExtra("userName", splitName[0])
        intent.putExtra("userSecondName", splitName[1])
        intent.putExtra("userDescription", des)
        startActivity(intent)

    }
}