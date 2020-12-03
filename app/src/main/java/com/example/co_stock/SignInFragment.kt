package com.example.co_stock

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.fragment_sign_in.*

/**
 * A simple [Fragment] subclass.
 * Use the [SignInFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignInFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    val viewModel: UserViewModel by viewModels<UserViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        login_button.setOnClickListener {
            val username = login_username.text.toString()
            val password = login_password.text.toString()

            if (username == "") {
                Toast.makeText(activity, "Missing Username",
                    Toast.LENGTH_SHORT).show();
            }
            else if (password == "") {
                Toast.makeText(activity, "Missing Password",
                    Toast.LENGTH_SHORT).show();
            }
            else {
                auth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener() { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Success", "signInWithEmailAndPassword:success")
                            val user = auth.currentUser!!
                            viewModel.updateAuth(user)
                            //viewModel.getUserByName()
                            lateinit var dbRef: DatabaseReference
                            viewModel.firebase.observe(viewLifecycleOwner, {
                                dbRef = it
                            })
                            val userRef = dbRef?.child("users")?.orderByChild("email")?.equalTo(user.email!!)
                            val valueEventListener = object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    for (ds in dataSnapshot.children) {
                                        val user = ds.getValue(User::class.java)
                                        viewModel.setUser(user!!)
                                        Log.d("getUserByName", viewModel.currentUser.value?.username!!)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SignInFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignInFragment().apply {
                arguments = Bundle().apply {
                    putString("", param1)
                    putString("", param2)
                }
            }
    }
}