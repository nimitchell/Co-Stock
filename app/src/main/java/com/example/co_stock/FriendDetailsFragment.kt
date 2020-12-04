package com.example.co_stock

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import kotlinx.android.synthetic.main.fragment_friend_details.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlin.reflect.typeOf

/**
 * A simple [Fragment] subclass.
 * Use the [FriendDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FriendDetailsFragment : Fragment() {

    val viewModel: UserViewModel by activityViewModels<UserViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.currentFriend.observe(viewLifecycleOwner, {
            // Setting appropriate text with information from currentFriend in viewModel
            friendName_text.text = it?.name
            friendSign_text.text = it?.sign
            friendBio_text.text = it?.bio
            val friend = it

            // Getting and setting the currentFriend's profile image
            viewModel.storage.observe(viewLifecycleOwner, {
                val imageRef = it.child(friend?.profilePic!!)
                val FIVE_MEGABYTES: Long = 1024 * 1024 * 5
                imageRef?.getBytes(FIVE_MEGABYTES)?.addOnSuccessListener {
                    val bitm = BitmapFactory.decodeByteArray(it,0, it.size)
                    friendProfile_img.setImageBitmap(bitm)
                }?.addOnFailureListener {
                    // Handle any errors
                    throw it
                }
            })

            // Calculating and displaying compatibility score and report
            val compScore = viewModel.getFriendCompatibility(it!!)
            compatibility_textNumber.text = compScore.toString()
            compatibility_message.text = viewModel.getFriendCompatibilityMessage(it!!.name, compScore)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend_details, container, false)
    }
}