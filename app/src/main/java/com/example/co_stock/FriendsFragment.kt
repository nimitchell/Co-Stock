package com.example.co_stock

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_friends.*
import java.util.ArrayList

/**
 * A simple [Fragment] subclass.
 * Use the [FriendsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FriendsFragment : Fragment() {

    lateinit var viewManger: RecyclerView.LayoutManager
    lateinit var viewAdapter: RecyclerViewAdapter

    val viewModel: UserViewModel by activityViewModels<UserViewModel>()

    val friendClickLambda: (User) -> Unit ={
        viewModel.currentFriend.value = it
        findNavController().navigate(R.id.action_friendsFragment_to_friendDetailsFragment)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewManger = LinearLayoutManager(activity)
        viewAdapter = RecyclerViewAdapter(ArrayList(), viewModel::getProfileImage)

        friends_recyclerView.layoutManager = viewManger
        friends_recyclerView.adapter = viewAdapter

        viewModel.friends.observe(viewLifecycleOwner, {
            viewAdapter.friendsList = it
            viewAdapter.notifyDataSetChanged()
        })

        val deleteLambda: (User) -> Unit = {
            viewModel.removeFriend(it.username)
        }

        viewAdapter.deleteLambda = deleteLambda
        viewAdapter.clickLambda = friendClickLambda

        search_button.setOnClickListener {
            if (searchFriend_text.text.isNotEmpty()){
                viewModel.addFriend(searchFriend_text.text.toString())
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false)
    }
}