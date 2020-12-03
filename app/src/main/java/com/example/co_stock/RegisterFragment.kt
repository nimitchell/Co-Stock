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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_register.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var auth: FirebaseAuth
    val viewModel: UserViewModel by viewModels<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

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
            val email = email_editText.text.toString()
            val username = username_editText.text.toString()
            val conflictUser = viewModel.checkUserExists(username)


            if (email == "") {
                Toast.makeText(activity, "Missing Email",
                    Toast.LENGTH_SHORT).show();
            }
            else if (password_editText.text.toString() == "") {
                Toast.makeText(activity, "Missing Password",
                    Toast.LENGTH_SHORT).show();
            }
            else if (password_editText.text.toString() != password_confirm_editText.text.toString()) {
                Toast.makeText(activity, "Passwords must match",
                    Toast.LENGTH_SHORT).show();
            }
            else if (username == "") {
                Toast.makeText(activity, "Missing Username",
                    Toast.LENGTH_SHORT).show();
            }

            else if (conflictUser) {
                Toast.makeText(activity, "Username Unavailable",
                    Toast.LENGTH_SHORT).show();
            }
            else {
                auth.createUserWithEmailAndPassword(email, password_editText.text.toString())
                    .addOnCompleteListener(){ task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Success", "createUserWithEmail:success")
                            val user = auth.currentUser
                            viewModel.updateAuth(user!!)
                            var date = "${reg_calenderView.year}-${reg_calenderView.month}-${reg_calenderView.dayOfMonth}"

                            viewModel.addUser(User(username,
                            email = user.email.toString(),
                            birthday = date,
                            profilePic = "${username}.png"
                            ))
                            viewModel.setImage("${username}.png", BitmapFactory.decodeResource(resources, R.drawable.baseline_person_outline_black_48dp))
                            findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Failed", "createUserWithEmail:failure", task.exception)
                            Toast.makeText(activity, "Email or Password is incorrect.",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        reg_login_text.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_signInFragment)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RegisterFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegisterFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}