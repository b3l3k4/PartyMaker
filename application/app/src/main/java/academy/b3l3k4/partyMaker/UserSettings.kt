package academy.b3l3k4.partyMaker

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.user_settings.*
import java.io.IOException
import java.lang.Exception
import java.util.*

class UserSettings:AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    var userName: EditText? = null
    var oldPass: EditText? = null
    var newPass: EditText? = null
    var userDes: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_settings)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        val backwardArrow: ImageButton = findViewById(R.id.backwardArrow)

        print(intent.getStringExtra("nameUser"))

        backwardArrow.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                startActivity(Intent(this@UserSettings, MainScreen::class.java))
            }
        })

        userName = findViewById(R.id.userName)
        oldPass= findViewById(R.id.oldPass)
        newPass = findViewById(R.id.newPass)
        userDes = findViewById(R.id.userDes)
        val saveChangesButton: Button = findViewById(R.id.saveChanges)

        userName!!.hint = (intent.getStringExtra("nameUser"))
        userDes!!.hint = intent.getStringExtra("descriptionUser")

        val profilePicture: ImageView = findViewById(R.id.profilePicture)
        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        val userPictureId = FirebaseAuth.getInstance().currentUser!!.uid
        val storageReference = FirebaseStorage.getInstance().reference.child("profile_pictures/").child(userPictureId)


        storageReference.downloadUrl.addOnSuccessListener(object: OnSuccessListener<Uri>{
            override fun onSuccess(uri: Uri?) {
                Glide.with(this@UserSettings)
                    .load(uri)
                    .fitCenter()
                    .into(profilePicture)
            }
        }).addOnFailureListener(object: OnFailureListener {
            override fun onFailure(p0: Exception) {
                profilePicture.setImageResource(R.mipmap.ic_launcher)
            }
        })

        profilePicture.setOnClickListener { launchGallery() }



        saveChangesButton.setOnClickListener{sendData()}
    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }

            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                profilePicture.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImage(){
        if(filePath != null){
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            val ref = storageReference?.child("profile_pictures/$userId")
            ref!!.putFile(filePath!!)
        }
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

        val userName = intent.getStringExtra("nameUser")
        val userDes = intent.getStringExtra("descriptionUser")
        val splitUser = userName!!.split(" ").toList()

        val intent = Intent(this@UserSettings, UserProfile::class.java)
        if(!TextUtils.isEmpty(name)){
            intent.putExtra("userName", splitName[0])
            intent.putExtra("userSecondName", splitName[1])
        }else{
            intent.putExtra("userName", splitUser[0])
            intent.putExtra("userSecondName", splitUser[1])

        }
        if(!TextUtils.isEmpty(des)){
            intent.putExtra("userDescription", des)
        } else{
            intent.putExtra("userDescription", userDes!!)
        }

        uploadImage()

        startActivity(intent)

    }

    override fun onBackPressed() {
        val intent = Intent(this, MainScreen::class.java)
        intent.flags  = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}