package com.example.co_stock

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class UserViewModel : ViewModel(), ValueEventListener {

    var firebase = MutableLiveData<DatabaseReference>()

    init {
        firebase.value = Firebase.database.getReference("") // empty string means get root
        firebase.value?.addValueEventListener(this)
    }
    override fun onDataChange(snapshot: DataSnapshot) {
    }

    override fun onCancelled(error: DatabaseError) {
    }
}