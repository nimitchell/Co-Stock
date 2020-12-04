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
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_home.*


/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {

    val viewModel: UserViewModel by activityViewModels<UserViewModel>()

    override fun onResume() {
        super.onResume()
        var picName = ""
        viewModel.currentUser.observe(viewLifecycleOwner, {
            picName = it.profilePic!!
        })
        viewModel.storage.observe(viewLifecycleOwner, {
            var imageRef = it.child(picName)
            val ONE_MEGABYTE: Long = 1024 * 1024
            imageRef?.getBytes(ONE_MEGABYTE)?.addOnSuccessListener {
                // Data for "images/island.jpg" is returned, use this as needed
                profile_img.setImageBitmap(BitmapFactory.decodeByteArray(it,0, it.size))
            }?.addOnFailureListener {
                // Handle any errors
                throw it
            }
        })
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var picName = ""
        viewModel.currentUser.observe(viewLifecycleOwner, {
            userName_textView.text = it.name
            userBio_textView.text = it.bio
            picName = it.profilePic!!
            if (it.sign == "")
                viewModel.determineSign()
            userSign_textView.text = it.sign
            if(!it.signs.isEmpty()) {
                userSign_tableText.text = it.signs.get(0)
                userRising_tableText.text = it.signs.get(1)
                userRival_tableText.text = it.signs.get(2)
                userMoon_tableText.text = it.signs.get(3)

                userSignText.text = it.signs.get(0)
                userRisingText.text = it.signs.get(1)
                userRivalText.text = it.signs.get(2)
                userMoonText.text = it.signs.get(3)
            }
        })
        viewModel.storage.observe(viewLifecycleOwner, {
            var imageRef = it.child(picName)
            val ONE_MEGABYTE: Long = 1024 * 1024
            imageRef?.getBytes(ONE_MEGABYTE)?.addOnSuccessListener {
                // Data for "images/island.jpg" is returned, use this as needed
                profile_img.setImageBitmap(BitmapFactory.decodeByteArray(it,0, it.size))
            }?.addOnFailureListener {
                // Handle any errors
                throw it
            }
        })


        // TODO add desciptions
        userSignDescription.text = "This sign shows who you are at your core. This is your most stable and comfortable state."
        userRisingDescription.text = "This sign holds the most positive qualities you can aqure at your fullest potential."
        userRivalDescription.text = "This sign holds the most negative qualities you have to fight against in your most vulnerable states."
        userMoonDescription.text = "This is the sign you will benefit most form observing and understanding their perspectives on the world."

        edit_button.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_editFragment)
        }
        friends_button.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_friendsFragment)
        }
        dailyReport_button.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_dailyReportFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
}