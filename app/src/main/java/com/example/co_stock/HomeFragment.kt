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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var picName = ""
        viewModel.currentUser.observe(viewLifecycleOwner, {
            // Setting textViews with user information
            userName_textView.text = it.name
            userBio_textView.text = it.bio
            picName = it.profilePic!!

            // Determines user's sign if one isn't assigned yet and displays it
            if (it.sign == "")
                viewModel.determineSign()
            userSign_textView.text = it.sign

            // Sets table and following listings with user's appropriate signs
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

        // Sets profile image
        viewModel.profilePic.observe(viewLifecycleOwner, {
            profile_img.setImageBitmap(it)
        })

        // Navigations
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