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
    var currentUser = MutableLiveData<User>()

    init {
        firebase.value = Firebase.database.getReference("") // empty string means get root
        firebase.value?.addValueEventListener(this)
    }

    fun updateUserData() {

    }
    fun compareImages(image: MarketImage){}


    fun addUser(user:User){
        currentUser.value = user
        firebase.value?.child("users")?.child(user.username)?.setValue(user)
    }
    fun determineSign(){}

    fun editUserInfo(){}
    fun addFriend(){}
    fun getUserByName(username:String){}
    fun getFriendCompatability(username:String){}
    fun getFriendCompatabilityMessage(score:Int){}
    fun calculateDailyScore(){}
    fun getDaileyReport(score:Int){}
    fun getStockScore(stock:String){}
    fun getStockReport(score:Int){}
    fun getRandomQuote(){}

    override fun onDataChange(snapshot: DataSnapshot) {
        snapshot.child("users").children.forEach {
            it.getValue(User::class.java)?.let {
                if(it.username == currentUser.value?.username){
                    currentUser.value  = it
                }
            }
        }
    }

    override fun onCancelled(error: DatabaseError) {
    }
}