package com.example.geekmessenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnlogin.setOnClickListener{
            login()
        }

    }

    private fun login() {
        val email=edtloginusername.text.toString();
        val pass=edtloginpass.text.toString()
        if (email==""||pass==""){
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,pass)
            .addOnSuccessListener {

                Toast.makeText(this,"login successfull",Toast.LENGTH_SHORT).show()
                val intent= Intent(this,LatestMessage::class.java)
                intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this,it.localizedMessage,Toast.LENGTH_SHORT).show()
            }

    }
}
