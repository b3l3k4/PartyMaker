@file:Suppress("DEPRECATION")

package academy.b3l3k4.partyMaker

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

private var userName: EditText? = null
private var userSurname: EditText? = null
private var userMail: EditText? = null
private var userPass: EditText? = null
private var signUp: Button? = null
private var mProgressBar: ProgressDialog? = null

private var mDatabaseReference: DatabaseReference? = null
private var mDatabase: FirebaseDatabase? = null
private var mAuth: FirebaseAuth? = null

private val TAG = "CreateAccountActivity"

private var firstName: String? = null
private var lastName: String? = null
private var email: String? = null
private var password: String? = null

class SignUp : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        userName= findViewById(R.id.textTitle)
        userSurname = findViewById(R.id.userSurname)
        userMail= findViewById(R.id.userMail)
        userPass = findViewById(R.id.userPass)
        signUp = findViewById(R.id.signUp)
        mProgressBar = ProgressDialog(this)

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")
        mAuth = FirebaseAuth.getInstance()

        val backwardArrow: ImageButton = findViewById(R.id.backwardArrow)

        backwardArrow.setOnClickListener{
            startActivity(Intent(this@SignUp, MainActivity::class.java))
        }

        signUp!!.setOnClickListener{ createNewAccount() }

    }

    private fun createNewAccount(){

        firstName = userName!!.text.toString()
        lastName = userSurname!!.text.toString()
        email = userMail!!.text.toString()
        password = userPass!!.text.toString()

        if(!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            mProgressBar!!.setMessage("Registering User!")
            mProgressBar!!.show()

            mAuth!!
                .createUserWithEmailAndPassword(email!!,password!!)
                .addOnCompleteListener(this){task ->
                    mProgressBar!!.hide()

                    if(task.isSuccessful){
                        Log.d(TAG, "createUser: successful")

                        val userID = mAuth!!.currentUser!!.uid

                        verifyEmail()

                        val user = User(email, firstName, lastName, password, "")

                        mDatabaseReference!!.child(userID).setValue(user)

                        updateUserInfoAndUI()
                    } else{
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }


        } else{
            Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUserInfoAndUI(){
        val intent = Intent(this, LogIn::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun verifyEmail() {

        val mUser = mAuth!!.currentUser
        mUser!!.sendEmailVerification()
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {
                    Toast.makeText(this,
                        "Verification email sent to " + mUser.getEmail(),
                        Toast.LENGTH_SHORT).show()
                } else {
                    Log.e(TAG, "sendEmailVerification", task.exception)
                    Toast.makeText(this,
                        "Failed to send verification email.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

}