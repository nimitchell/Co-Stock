package com.example.co_stock

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
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
import kotlinx.android.synthetic.main.fragment_edit.*
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * A simple [Fragment] subclass.
 * Use the [EditFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditFragment : Fragment() {

    val viewModel: UserViewModel by activityViewModels<UserViewModel>()
    var nameEdit: String? = null
    var bioEdit: String? = null
    var picEdit: Bitmap? = null

    override fun onPause() {
        super.onPause()
        // TODO maybe don't need bitMap in editUserInfo
        if (name_editText.text.isNotEmpty()) {
            nameEdit = name_editText.text.toString()
        }
        if (editHome_textView.text.isNotEmpty()) {
            bioEdit = bio_editText.text.toString()
        }
        viewModel.editUserInfo(nameEdit, bioEdit)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var picName = ""
        viewModel.currentUser.observe(viewLifecycleOwner, {
            name_editText.setText(it.name)
            bio_editText.setText(it.bio)
            picName = it.profilePic!!
        })
        viewModel.storage.observe(viewLifecycleOwner, {
            var imageRef = it.child(picName)
            val ONE_MEGABYTE: Long = 1024 * 1024
            imageRef?.getBytes(ONE_MEGABYTE)?.addOnSuccessListener {
                // Data for "images/island.jpg" is returned, use this as needed
                edit_profile_img.setImageBitmap(BitmapFactory.decodeByteArray(it,0, it.size))
            }?.addOnFailureListener {
                // Handle any errors
                throw it
            }
        })


        edit_profile_img.setOnClickListener {
            val intent= Intent(Intent.ACTION_PICK)
            intent.type="image/*"
            startActivityForResult(intent, 0)
        }

        save_button.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit, container, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK && requestCode==0){
            val inputURI=data?.data
            inputURI?.let {
                val imageStream = activity?.contentResolver?.openInputStream(it)
                val selectBitmap = BitmapFactory.decodeStream(imageStream)
                viewModel.setImage(viewModel.currentUser.value!!.profilePic, selectBitmap)
                edit_profile_img.setImageBitmap(selectBitmap)

            }
        }
    }
}