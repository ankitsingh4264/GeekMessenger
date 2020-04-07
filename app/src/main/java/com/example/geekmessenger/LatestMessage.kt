package com.example.geekmessenger

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Messenger
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_latest_message.*
import kotlinx.android.synthetic.main.latestmsg_chat.view.*
import kotlin.math.log

class LatestMessage : AppCompatActivity() {

    companion object{
        var currentuser: datauser? =null
    }

    var dapter= GroupAdapter<GroupieViewHolder>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_message)
        supportActionBar?.title="Messenger"
        fetchcurrentuser()
        verifyuserloggedin()
        listenforlatestmsg()

        latest_recyclerview.adapter=dapter
        latest_recyclerview.layoutManager=LinearLayoutManager(this)
        latest_recyclerview.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))




        dapter.setOnItemClickListener { item, view ->

            val user=item as latestmsg

            val intent=Intent(this,Chatlog::class.java)
            intent.putExtra("name",user.chatpartneruser?.username)
            intent.putExtra("photourl",user.chatpartneruser?.path)
            intent.putExtra("uid",user.chatpartneruser?.uid)
            startActivity(intent)

        }






    }

    var hashmap=HashMap<String,Chatmessage>()

    private fun refereshrecyclerview(){

        dapter.clear()

        hashmap.values.forEach {
            dapter.add(latestmsg(it))
        }
    }

    private fun listenforlatestmsg(){
        val uid=FirebaseAuth.getInstance().uid

        val ref=FirebaseDatabase.getInstance().getReference("/latest-message/${uid}")
        ref.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                val chatrecent=p0.getValue(Chatmessage::class.java)

                Log.i("latest",p0.key)


                if(hashmap.containsKey(p0.key)) {

                    if (chatrecent != null) {
                        hashmap.put(p0.key!!, chatrecent)
                    }
                    refereshrecyclerview()

                }else{
                    if (chatrecent != null) {

                        dapter.add(latestmsg(chatrecent))
                    }
                }
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatrecent=p0.getValue(Chatmessage::class.java)
                if(hashmap.containsKey(p0.key)) {

                    if (chatrecent != null) {
                        hashmap.put(p0.key!!, chatrecent)
                    }
                    refereshrecyclerview()

                }else{


                    if (chatrecent != null) {

                        dapter.add(latestmsg(chatrecent))
                    }
                }



            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }






    private fun fetchcurrentuser() {
        val uid=FirebaseAuth.getInstance().uid;
        val ref=FirebaseDatabase.getInstance().getReference("/users/${uid}")
        ref.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {

                currentuser=p0.getValue(datauser::class.java)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun verifyuserloggedin() {
        val uid=FirebaseAuth.getInstance().uid;
        if (uid==null){
            val intent=Intent(this,MainActivity::class.java)
            intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        when(item.itemId) {
            R.id.menunewmsg->{
                val intent=Intent(this,NewMessage::class.java)
                startActivity(intent)
            }
            R.id.menu_signout->{
                FirebaseAuth.getInstance().signOut()
                val intent=Intent(this,MainActivity::class.java)
                intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }


        }
        return super.onOptionsItemSelected(item)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mainmenu,menu)
        return super.onCreateOptionsMenu(menu)

    }
}
class latestmsg(val chat: Chatmessage) : Item<GroupieViewHolder>(){
    override fun getLayout(): Int {

        return R.layout.latestmsg_chat

    }
     lateinit var chatpartneruser :datauser
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.latest_chat_msg.text=chat.msg

        var partnerid:String
        if (chat.fromid== FirebaseAuth.getInstance().uid){
            partnerid=chat.toid
        }else{
            partnerid=chat.fromid
        }
        Log.i("latestpartner",partnerid)

        val ref= FirebaseDatabase.getInstance().getReference("/users/${partnerid}")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                 chatpartneruser= p0.getValue(datauser::class.java)!!
                viewHolder.itemView.latest_chatname.text=chatpartneruser?.username
                val partnerdp=viewHolder.itemView.latest_chat_imageview
                Picasso.get().load(chatpartneruser?.path).into(partnerdp)


            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })



    }

}

