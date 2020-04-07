package com.example.geekmessenger

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    var emailid=""
    var pass=""
    val auth=FirebaseAuth.getInstance()





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




        alredadyacct.setOnClickListener{
            var intent=Intent(this,Login::class.java)
            startActivity(intent)

        }
        btnregister.setOnClickListener{
            registeruser()
        }
        btnaddimage.setOnClickListener{
            var intent=Intent(Intent.ACTION_PICK)
            intent.type="image/*"
            startActivityForResult(intent,1)

        }

    }
    lateinit var selectedphotouri :Uri

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==1&&resultCode== Activity.RESULT_OK&&data!=null){
            selectedphotouri= data.data!!
            var bitmap=MediaStore.Images.Media.getBitmap(contentResolver,selectedphotouri)
            btnaddimage.setImageBitmap(bitmap)
//            val bitmapDrawable=BitmapDrawable(bitmap)
//            btnaddimage.setBackgroundDrawable(bitmapDrawable)

        }
    }

    private fun registeruser() {
        emailid=edtusername.text.toString();
        pass=edtpass.text.toString()

        if (emailid==""||pass==""){
            return
        }

        auth.createUserWithEmailAndPassword(emailid,pass)
            .addOnSuccessListener {
                uploadimagefirebase()

                Toast.makeText(this,"Signup Succesfull",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this,it.localizedMessage,Toast.LENGTH_SHORT).show()
            }


    }

    private fun uploadimagefirebase() {
        if (selectedphotouri==null){
            return
        }

        var filename=UUID.randomUUID().toString()

        var ref=FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedphotouri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    saveuseronfirebasedatabase(it.toString())


                }
            }

    }

    private fun saveuseronfirebasedatabase( imageurl:String) {
        var uid=FirebaseAuth.getInstance().uid
        var ref=FirebaseDatabase.getInstance().getReference("/users/$uid")

        var user= uid?.let { datauser(it,edtfullname.text.toString(),imageurl) }
        ref.setValue(user)
            .addOnSuccessListener {

                Log.i("register","done")
                val intent=Intent(this,LatestMessage::class.java)
                intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
    }
}

data class datauser(val uid:String,val username:String,val path:String){
    constructor() : this("","","")
}
