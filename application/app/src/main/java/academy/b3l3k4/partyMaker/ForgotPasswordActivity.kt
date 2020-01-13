package academy.b3l3k4.partyMaker

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

private var userEmail: EditText? = null
private var resetButton: Button? = null

private var mAuth: FirebaseAuth? = null


class ForgotPasswordActivity: AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgot_password)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        val backwardArrow: ImageButton = findViewById(R.id.backwardArrow)

        backwardArrow.setOnClickListener {
            startActivity(Intent(this@ForgotPasswordActivity, LogIn::class.java))
        }

        val textMessage: TextView = findViewById(R.id.textMessage)

        textMessage.setText("We just need your registered e-mail to sent you a reset link")

        initialise()
    }

    private fun initialise(){
        userEmail = findViewById(R.id.userEmail)
        resetButton = findViewById(R.id.resetButton)

        mAuth = FirebaseAuth.getInstance()

        resetButton!!.setOnClickListener{sendNewPassword()}
    }

    private fun sendNewPassword(){
        val email = userEmail!!.text.toString()

        if(!TextUtils.isEmpty(email)){
            mAuth!!
                .sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        val message = "Email sent"
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        updateUI()
                    } else{
                        Toast.makeText(this, "No user found with this email.", Toast.LENGTH_SHORT).show()
                    }
                }
        } else{
            Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(){
        val intent = Intent(this@ForgotPasswordActivity, LogIn::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainScreen::class.java)
        intent.flags  = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}