package com.example.geekmessenger

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.userlist.view.*

lateinit var mcontext:Context

class recyclerviewAdpter (var list:ArrayList<datauser>) : RecyclerView.Adapter<viewholder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        mcontext=parent.context

        return viewholder(LayoutInflater.from(parent.context)
            .inflate(R.layout.userlist,parent,false))


    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {
        holder.bind(list[position])

    }
}
class viewholder(itemview: View) : RecyclerView.ViewHolder(itemview){

    lateinit var currentpos:datauser

    init {
        itemview.setOnClickListener {
           val intent=Intent(mcontext,Chatlog::class.java)
            intent.putExtra("name",currentpos.username)
            intent.putExtra("photourl",currentpos.path)
            intent.putExtra("uid",currentpos.uid)
            mcontext.startActivity(intent)

        }
    }
    fun bind(userr: datauser) {
        currentpos=userr
        with(itemView){
            listusername.text=userr.username;
            Picasso.get().load(userr.path).into(userdp)


        }


    }

}