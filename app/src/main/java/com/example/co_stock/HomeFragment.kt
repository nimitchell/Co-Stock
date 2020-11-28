package com.example.co_stock

import android.os.Bundle
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

        edit_button.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_editFragment)
        }
        friends_button.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_friendsFragment)
        }

        viewModel.currentUser.observe(viewLifecycleOwner, {
            profile_img.setImageBitmap(viewModel.currentUser.value?.profilePic)
            userName_textView.text = viewModel.currentUser.value?.name
            userSign_textView.text = viewModel.currentUser.value?.sign
            userBio_textView.text = viewModel.currentUser.value?.bio

            userSign_tableText.text = viewModel.currentUser.value?.sign

            userSignText.text = viewModel.currentUser.value?.sign
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
}