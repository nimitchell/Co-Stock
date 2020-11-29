package com.example.co_stock

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.fragment_friend_details.*

/**
 * A simple [Fragment] subclass.
 * Use the [FriendDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FriendDetailsFragment : Fragment() {

    val viewModel: UserViewModel by activityViewModels<UserViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val friendUser = viewModel.currentFriend.value
        friendName_text.text = friendUser?.name
        friendSign_text.text = friendUser?.sign
        friendBio_text.text = friendUser?.bio

        val compScore = viewModel.getFriendCompatibility(friendUser!!)
        compatibility_textNumber.text = compScore.toString()
        compatibility_message.text = viewModel.getFriendCompatibilityMessage(friendUser!!.name, compScore)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend_details, container, false)
    }
}