package com.example.co_stock

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_friend_details.*
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.fragment_sign_in.*

/**
 * A simple [Fragment] subclass.
 * Use the [SignInFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignInFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    val viewModel: UserViewModel by activityViewModels<UserViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        login_button.setOnClickListener {
            val username = login_username.text.toString()
            val password = login_password.text.toString()

            // Informs user if they are missing a username
            if (username == "") {
                Toast.makeText(activity, "Missing Username",
                    Toast.LENGTH_SHORT).show();
            }
            // Informs user if they are missing a password
            else if (password == "") {
                Toast.makeText(activity, "Missing Password",
                    Toast.LENGTH_SHORT).show();
            }
            else {
                auth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener() { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            val user = auth.currentUser!!
                            viewModel.updateAuth(user)

                            // Retrieving user data from firebase
                            lateinit var dbRef: DatabaseReference
                            viewModel.firebase.observe(viewLifecycleOwner, {
                                dbRef = it
                            })
                            // Find user reference by matching email
                            val userRef = dbRef?.child("users")?.orderByChild("email")?.equalTo(user.email!!)

                            // Defining event listener to override onDataChange
                            val valueEventListener = object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    for (ds in dataSnapshot.children) {
                                        // Getting user data from firebase
                                        val user = ds.getValue(User::class.java)
                                        viewModel.setUser(user!!)
                                        // Getting user picture from firebase storage
                                        viewModel.storage.observe(viewLifecycleOwner, {
                                            var imageRef = it.child(user?.profilePic!!)
                                            val FIVE_MEGABYTES: Long = 1024 * 1024 * 5
                                            imageRef?.getBytes(FIVE_MEGABYTES)?.addOnSuccessListener {
                                                viewModel.profilePic.postValue(BitmapFactory.decodeByteArray(it,0, it.size))
                                            }?.addOnFailureListener {
                                                Log.d("failure", it.toString())
                                            }
                                        })
                                        findNavController().navigate(R.id.action_signInFragment_to_homeFragment)
                                    }
                                }
                                override fun onCancelled(databaseError: DatabaseError) {
                                    Log.d("error", databaseError.getMessage())
                                }
                            }
                            userRef?.addListenerForSingleValueEvent(valueEventListener)

                        }
                        else {
                            // If sign in fails, display a message to the user.
                            Log.w("Failed", "signInWithEmailAndPassword:failure", task.exception)
                            Toast.makeText(activity, "Email or Password is incorrect.",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        signUp_text.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_registerFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }
}