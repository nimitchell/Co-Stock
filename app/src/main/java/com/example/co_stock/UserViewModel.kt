package com.example.co_stock

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class UserViewModel : ViewModel(), ValueEventListener {

    var firebase = MutableLiveData<DatabaseReference>()
    var currentUser = MutableLiveData<User>()
    var currentFriend = MutableLiveData<User>()
    var userAuth = MutableLiveData<FirebaseUser>()
    var friends = MutableLiveData<ArrayList<User>>()
    var quotes = MutableLiveData<ArrayList<String>>()
    var dailyMessage = MutableLiveData<Map<Int, String>>()
    var indexMessage = MutableLiveData<Map<Int, String>>()
    var compatibility = MutableLiveData<Map<Int, Compatibility>>()

    init {
        firebase.value = Firebase.database.getReference("") // empty string means get root
        firebase.value?.addValueEventListener(this)
    }

    fun updateAuth(user:FirebaseUser) {
        userAuth.value = user
    }

    fun updateUserData() {
        // TODO maybe uneeded?
    }

    fun compareImages(image: MarketImage){
        // TODO Rachey?
        // compare to self.marketImage
        //return integer between 1 and 5
    }

    // TODO do we need a create user? From the username/dob
    fun addUser(user:User){
        currentUser.postValue(user)
        firebase.value?.child("users")?.child(user.username)?.setValue(user)
    }

    fun determineSign(date:String):String {
        // TODO Rachey! Pop off queen!
        return ""
    }

    fun editUserInfo(name: String, bio: String, image:Bitmap){
        firebase.value?.child("users")?.child(currentUser.value?.username!!)?.child("name")?.setValue(name)
        firebase.value?.child("users")?.child(currentUser.value?.username!!)?.child("bio")?.setValue(bio)
        firebase.value?.child("users")?.child(currentUser.value?.username!!)?.child("profilePic")?.setValue(image)
    }

    fun addFriend(username:String){
        if (firebase.value?.child("users")?.child(username) != null) {
            var newFriends = currentUser.value?.friends as ArrayList<String>
            newFriends.add(username)
            currentUser.value?.friends = newFriends.toList()
            currentUser.postValue(currentUser.value)
            firebase.value?.child("users")?.child(currentUser.value?.username!!)?.child("friends")?.setValue(currentUser.value?.friends)
        }
    }

    fun removeFriend(username: String){
        if (currentUser.value?.friends?.contains(username)!!) {
            var newFriends = currentUser.value?.friends as ArrayList<String>
            newFriends.remove(username)
            currentUser.value?.friends = newFriends.toList()
            currentUser.postValue(currentFriend.value)
            firebase.value?.child("users")?.child(currentUser.value?.username!!)?.child("friends")
                ?.setValue(currentUser.value?.friends)
        }
    }

    fun getUserByName(email:String) {
        val dbRef = firebase.value
        val userRef = dbRef?.child("users")?.orderByChild("email")?.equalTo(email)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val user = ds.getValue(User::class.java)
                    currentUser.postValue(user)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("error", databaseError.getMessage())
            }
        }
        userRef?.addListenerForSingleValueEvent(valueEventListener)
    }

    fun checkUserExists(username:String):Boolean {
        val dbRef = firebase.value
        val userRef = dbRef?.child("users")?.orderByChild("username")?.equalTo(username)
        var exists = false
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    exists = true
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("error", databaseError.getMessage())
            }
        }
        userRef?.addListenerForSingleValueEvent(valueEventListener)
        return exists
    }

    fun getFriendCompatibility(friend:User): Int {
        //TODO Rachael's work feeds into here
        //compare the friend's market image to yours using compareImages
        return 0
    }
    fun getFriendCompatibilityMessage(friend: String, score:Int): String {
        var comp = compatibility.value?.get(score)!!
        var output = "For you, ${friend} is a ${comp.personality}:\n${comp.message}"
        return output
    }
    fun calculateDailyScore(): Int {
        // TODO Rachey
        return 0

    }
    fun getDailyReport(score:Int) : String{
        return dailyMessage.value?.get(score)!!
    }
    fun getIndexScore(stock:String){

    }
    fun getIndexReport(score:Int) : String{
        return indexMessage.value?.get(score)!!
    }
    fun getRandomQuote(): String{
        var tmpList = quotes.value
        return tmpList?.shuffled()?.take(1)!![0]
    }

    override fun onDataChange(snapshot: DataSnapshot) {
        var tmpFriends = ArrayList<User>()
        // get users
        snapshot.child("users").children.forEach {
            it.getValue(User::class.java)?.let {
                // update current user
                if(it.username == currentUser.value?.username){
                    currentUser.value  = it
                }
                // update friends list
                else if (currentUser.value?.friends?.contains(it.username) != null && currentUser.value?.friends?.contains(it.username)!!) {
                    tmpFriends.add(it)
                }
            }
        }
        friends.postValue(tmpFriends)

        // get quotes
        var tmpQuotes = ArrayList<String>()
        snapshot.child("quotes").children.forEach {
            it.getValue(String::class.java)?.let {
                tmpQuotes.add(it)
            }
        }
        quotes.postValue(tmpQuotes)

        // get daily messages
        var tmpDaily = mutableMapOf<Int, String>()
        var counter = 0
        snapshot.child("daily_message").children.forEach {
            it.getValue(String::class.java)?.let {
                counter++
                tmpDaily[counter] = it
            }
        }
        dailyMessage.postValue(tmpDaily)

        // get index messages
        var tmpIndex = mutableMapOf<Int, String>()
        counter = 0
        snapshot.child("index_message").children.forEach {
            it.getValue(String::class.java)?.let {
                counter++
                tmpIndex[counter] = it
            }
        }
        indexMessage.postValue(tmpIndex)

        // get index messages
        var tmpCompatability = mutableMapOf<Int, Compatibility>()
        snapshot.child("compatibility").children.forEach {
            it.getValue(Compatibility::class.java)?.let {
                tmpCompatability[it.rating] = it
            }
        }
        compatibility.postValue(tmpCompatability)
    }


    override fun onCancelled(error: DatabaseError) {
        Log.e("db error", error.message)
    }
}