package com.example.co_stock

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_sign_in.view.*

class RecyclerViewAdapter(var friendsList: ArrayList<User>):
    RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder{
        val viewItem =
            LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return RecyclerViewHolder(viewItem, deleteLambda)
    }

    override fun onBindViewHolder(holder: RecyclerViewAdapter.RecyclerViewHolder, position: Int) {
        holder.bind(friendsList[position], clickLambda)
    }

    override fun getItemCount(): Int {
        return friendsList.size
    }

    lateinit var deleteLambda: (User) -> Unit

    lateinit var clickLambda: (User) -> Unit

    class RecyclerViewHolder(val view: View, val deleteLambda: (User) -> Unit):
        RecyclerView.ViewHolder(view){
        fun bind(friend: User, clickLambda: (User) -> Unit){
            view.findViewById<ImageView>(R.id.friendProfile_img).setImageBitmap(friend.profilePic)
            view.findViewById<TextView>(R.id.friendName_text).text = friend.name
            view.findViewById<TextView>(R.id.friendSign_text).text = friend.sign

            view.findViewById<Button>(R.id.delete_button).setOnClickListener {
                deleteLambda(friend)
            }

            view.setOnClickListener {
                clickLambda(friend)
            }
        }
    }
}