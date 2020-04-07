package com.example.geekmessenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chatlog.*
import kotlinx.android.synthetic.main.chat_from.view.*
import kotlinx.android.synthetic.main.chat_to.view.*
import com.example.geekmessenger.Chatmessage as Chatmessage

class Chatlog : AppCompatActivity() {

    var name:String=""
    var toid:String=""
    var image:String=""
    lateinit var userlogin:datauser
    var adapter=GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatlog)
        userlogin= LatestMessage.currentuser!!





        name=intent.getStringExtra("name")
        toid=intent.getStringExtra("uid")
        image=intent.getStringExtra("photourl")



        supportActionBar?.title=name
        listensmsg()



       send_chatlog.setOnClickListener {
           sendmessage()
       }




        recyclerview_chatlog.adapter=adapter
        recyclerview_chatlog.layoutManager=LinearLayoutManager(this)

    }

    private fun listensmsg() {
        val uid=FirebaseAuth.getInstance().uid
        val ref=FirebaseDatabase.getInstance().getReference("/user-message/${uid}/${toid}")

        ref.addChildEventListener(object :ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatmsg=p0.getValue(Chatmessage::class.java)
                val msg=chatmsg?.msg
                val fromid=chatmsg?.fromid
                val toid=chatmsg?.toid
                if (fromid==uid){

                    if (msg != null) {
                        adapter.add(chatitemTo(msg,userlogin.path))
                    }


                }else{
                    if (msg != null) {
                        adapter.add(chatitemfrom(msg,image))
                    }

                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })

        recyclerview_chatlog.scrollToPosition(adapter.itemCount-1)
    }

    private fun sendmessage() {
        val uid=FirebaseAuth.getInstance().uid
        val reference=FirebaseDatabase.getInstance().getReference("/user-message/${uid}/${toid}").push()
        val referenceto=FirebaseDatabase.getInstance().getReference("/user-message/${toid}/${uid}").push()
        val chat= Chatmessage(reference.key!!,send_msgchatlog.text.toString(),uid!!,toid)


        reference.setValue(chat)
            .addOnSuccessListener {
                send_msgchatlog.text.clear()
                recyclerview_chatlog.scrollToPosition(adapter.itemCount-1)

            }
        referenceto.setValue(chat)



        val latestmsg=FirebaseDatabase.getInstance().getReference("/latest-message/${uid}/${toid}")
        latestmsg.setValue(chat)

        val latestMsgto=FirebaseDatabase.getInstance().getReference("/latest-message/${toid}/${uid}")
        latestMsgto.setValue(chat)




    }


}
class chatitemfrom(val text: String, val image: String): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {

        return R.layout.chat_from

    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.chatfrom_msg.text=text
        val dp=viewHolder.itemView.chatfrom_imageview
        Picasso.get().load(image).into(dp)



    }

}
class chatitemTo(val text: String,val path: String): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_to

    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.chatto_msg.text=text
        val dp=viewHolder.itemView.chatto_imageview
        Picasso.get().load(path).into(dp)

    }

}