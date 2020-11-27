package com.example.co_stock

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_sign_in.view.*

class RecyclerViewAdapter(var userList: Array<User>):
    RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder{
        val viewItem =
            LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return RecyclerViewHolder(viewItem, deleteLambda)
    }

    override fun onBindViewHolder(holder: RecyclerViewAdapter.RecyclerViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    lateinit var deleteLambda: (User) -> Unit

    class RecyclerViewHolder(val view: View, val deleteLambda: (User) -> Unit):
        RecyclerView.ViewHolder(view){
        fun bind(user: User){
            view.findViewById<TextView>(R.id.friendName_text).text = user.name
            view.findViewById<TextView>(R.id.friendSign_text).text = user.sign

            view.findViewById<Button>(R.id.delete_button).setOnClickListener {
                deleteLambda(user)
            }
        }
    }
}