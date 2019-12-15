@file:Suppress("DEPRECATION")

package academy.b3l3k4.partyMaker

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.Serializable

private var party: MutableList<Party> = mutableListOf()



private val TAG = "Log in process"
private val TAG1 = "DATA"

private var userEmail: EditText? = null
private var userPass: EditText? = null
private var logIn: Button? = null
private var forgotPass: TextView? = null
private var mProgressBar: ProgressDialog? = null

private var email: String? = null
private var password: String? =null

private var mAuth: FirebaseAuth? = null

class LogIn:AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.log_in)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        var backwardArrow: ImageButton = findViewById(R.id.backwardArrow)

        backwardArrow.setOnClickListener{
            val intent = Intent(this@LogIn, MainActivity::class.java)
            startActivity(intent)
        }

        initialise()
    }

    private fun initialise(){

        userEmail = findViewById(R.id.userEmail)
        userPass = findViewById(R.id.userPass)
        logIn = findViewById(R.id.logIn)
        forgotPass = findViewById(R.id.forgotPass)
        mProgressBar = ProgressDialog(this)

        mAuth = FirebaseAuth.getInstance()

        forgotPass!!
            .setOnClickListener { startActivity(Intent(this@LogIn, ForgotPasswordActivity::class.java)
            ) }

        logIn!!.setOnClickListener{logInUser()}
    }

    private fun logInUser(){

        email = userEmail!!.text.toString()
        password = userPass!!.text.toString()

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            mProgressBar!!.setMessage("Logging in")
            mProgressBar!!.show()

            mAuth!!
                .signInWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener(this){task ->

                    mProgressBar!!.hide()

                    if(task.isSuccessful){
                        Log.d(TAG,"Sign in with email successful")
                        updateUI()
                    } else{
                        Log.e(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(this@LogIn, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }else{
            Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(){

        val intent = Intent(this@LogIn, MainScreen::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

}