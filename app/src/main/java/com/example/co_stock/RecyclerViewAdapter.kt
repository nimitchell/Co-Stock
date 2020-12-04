package com.example.co_stock

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_sign_in.view.*

class RecyclerViewAdapter(var friendsList: ArrayList<User>, var storage: StorageReference):
    RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder{
        val viewItem =
            LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return RecyclerViewHolder(viewItem, deleteLambda)
    }

    override fun onBindViewHolder(holder: RecyclerViewAdapter.RecyclerViewHolder, position: Int) {
        holder.bind(friendsList[position], clickLambda, storage)
    }

    override fun getItemCount(): Int {
        return friendsList.size
    }

    lateinit var deleteLambda: (User) -> Unit

    lateinit var clickLambda: (User) -> Unit

    class RecyclerViewHolder(val view: View, val deleteLambda: (User) -> Unit):
        RecyclerView.ViewHolder(view){
        fun bind(friend: User, clickLambda: (User) -> Unit, storage: StorageReference){

                // load image
                var imageRef = storage.child(friend.profilePic)
                val FIVE_MEGABYTES: Long = 1024 * 1024 * 5
                imageRef?.getBytes(FIVE_MEGABYTES)?.addOnSuccessListener {
                    // Data for "images/island.jpg" is returned, use this as needed
                    view.findViewById<ImageView>(R.id.friendProfile_img).setImageBitmap(BitmapFactory.decodeByteArray(it,0, it.size))
                }?.addOnFailureListener {
                    // Handle any errors
                    throw it
                }
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