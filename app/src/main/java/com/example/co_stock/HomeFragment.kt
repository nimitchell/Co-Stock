package com.example.co_stock

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
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

        viewModel.currentUser.observe(viewLifecycleOwner, {
            Log.d("homefrag obs", it.toString())
            userName_textView.text = it.name
            userBio_textView.text = it.bio
            userSign_textView.text = it.sign
            //profile_img.setImageBitmap(viewModel.getProfileImage(it.profilePic!!))
        })

        //Log.d("homefrag", viewModel.toString())
        //Log.d("homefrag", viewModel.currentUser.value.toString())

        // TODO need to set the text once getting signs is figured out
        userSign_tableText.text = viewModel.currentUser.value?.sign
        userRising_tableText.text = ""
        userRival_tableText.text = ""
        userMoon_tableText.text = ""

        // TODO add desciptions
        userSignText.text = viewModel.currentUser.value?.sign
        userSignDescription.text = ""
        userRisingText.text = ""
        userRisingDescription.text = ""
        userRivalText.text = ""
        userRivalDescription.text = ""
        userMoonText.text = ""
        userMoonDescription.text = ""

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