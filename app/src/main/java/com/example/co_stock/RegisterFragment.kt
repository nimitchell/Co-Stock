package com.example.co_stock

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_register.*


/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    val viewModel: UserViewModel by activityViewModels<UserViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        reg_signUp_button.setOnClickListener {
            // Making sure a user with the same username/password doesn't exist
            val email = email_editText.text.toString()
            val username = username_editText.text.toString()
            val conflictUser = viewModel.checkUserExists(username)

            // Informs user if they are missing an email
            if (email == "") {
                Toast.makeText(activity, "Missing Email",
                    Toast.LENGTH_SHORT).show();
            }
            // Informs user if they are missing a password
            else if (password_editText.text.toString() == "") {
                Toast.makeText(activity, "Missing Password",
                    Toast.LENGTH_SHORT).show();
            }
            // Informs user if they need a matching password
            else if (password_editText.text.toString() != password_confirm_editText.text.toString()) {
                Toast.makeText(activity, "Passwords must match",
                    Toast.LENGTH_SHORT).show();
            }
            // Informs user if they are missing a username
            else if (username == "") {
                Toast.makeText(activity, "Missing Username",
                    Toast.LENGTH_SHORT).show();
            }
            // Informs user if their username is already taken
            else if (conflictUser) {
                Toast.makeText(activity, "Username Unavailable",
                    Toast.LENGTH_SHORT).show();
            }
            // Creates a user
            else {
                auth.createUserWithEmailAndPassword(email, password_editText.text.toString())
                    .addOnCompleteListener(){ task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            val user = auth.currentUser
                            viewModel.updateAuth(user!!)

                            // Add leading zeros to day and/or month
                            var day = reg_calenderView.month.toString()
                            if(reg_calenderView.month < 10)
                                day = "0" + reg_calenderView.month.toString()
                            var month = reg_calenderView.month.toString()
                            if(reg_calenderView.month < 10)
                                month = "0" + reg_calenderView.month.toString()

                            val date = "${reg_calenderView.year}-${month}-${day}"

                            // Saves user information in viewModel
                            viewModel.addUser(User(username,
                            email = user.email.toString(),
                            birthday = date,
                            profilePic = "${username}.png"
                            ))
                            // Defaults user's profile picture with white picture
                            viewModel.setImage("${username}.png", BitmapFactory.decodeResource(resources, R.drawable.round_portrait_white_48dp))
                            viewModel.profilePic.postValue(BitmapFactory.decodeResource(resources, R.drawable.round_portrait_white_48dp))
                            // Navigates to home fragment
                            findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
                        } else {
                            // Informs user if sign in fails
                            Toast.makeText(activity, task.exception?.message,
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        reg_login_text.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_signInFragment)
        }
    }

}