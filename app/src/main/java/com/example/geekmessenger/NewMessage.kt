package com.example.geekmessenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_new_message.*

class NewMessage : AppCompatActivity() {
    var temp= arrayListOf<datauser>()
    var dapter=recyclerviewAdpter(temp)




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)


        recycler_new_msg.apply {
            adapter = dapter
            layoutManager = LinearLayoutManager(this@NewMessage)
        }




        fetchuser()
    }

    private fun fetchuser() {
        Log.i("newmsg","0")
        val ref=FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                p0.children.forEach(){
                    Log.i("newmsg",it.toString())

                    val tempuser=it.getValue(datauser::class.java)
                    if (tempuser != null) {
                        temp.add(tempuser)
                    }

                }
                Log.i("newmsg","${temp.size}")

              dapter.notifyDataSetChanged()


            }



            override fun onCancelled(p0: DatabaseError) {

            }

        })
    }
}
