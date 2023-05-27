package com.example.reflex_projekt

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class MyAdapter(private val list: ArrayList<Player>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>(){


    private lateinit var dialoginflater: LayoutInflater
    private lateinit var databaseReference: DatabaseReference
   var level:Int =1
    var pos:Int=1
    private lateinit var builder: AlertDialog.Builder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {


        val inflater:LayoutInflater=LayoutInflater.from(parent.context)


        val view:View=inflater.inflate(R.layout.playerrow,parent,false)
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.playerrow, parent, false)

        return MyViewHolder(view)
    }
    public interface OnItemClickListener
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val player = list[holder.adapterPosition]
//        holder.name.text = game.getName1()
//        holder.year.text = game.getYear1()
//        holder.type.text = game.getType1()
//        holder.played.text = game.isPlayed1().toString()

        databaseReference = FirebaseDatabase

            .getInstance("https://top10-clickers-list-default-rtdb.firebaseio.com/")

            .getReference("Lists")

            .child(level.toString())


holder.rank.setText(pos.toString())
        holder.playername.text = player.name
        holder.time.setText(player.time.toString())
        holder.rank.text = pos.toString()
pos++

    }





    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        var playername: TextView
        var time: TextView
        var rank: TextView

        init {
            playername = itemView.findViewById(R.id.playername)
            time = itemView.findViewById(R.id.time)
            rank = itemView.findViewById(R.id.position)

        }
    }



}
