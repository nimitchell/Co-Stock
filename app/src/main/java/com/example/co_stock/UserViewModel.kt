package com.example.co_stock

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
    var currentFriend = MutableLiveData<User>()
    var friends = MutableLiveData<ArrayList<User>>()
    var quotes = MutableLiveData<ArrayList<String>>()
    var dailyMessage = MutableLiveData<Map<Int, String>>()
    var indexMessage = MutableLiveData<Map<Int, String>>()
    var compatibility = MutableLiveData<Map<Int, Compatibility>>()

    init {
        firebase.value = Firebase.database.getReference("") // empty string means get root
        firebase.value?.addValueEventListener(this)
    }

    fun updateUserData() {
        // TODO maybe uneeded?
    }
    fun compareImages(image: MarketImage){
        // TODO Rachey?
        // compare to self.marketImage
        //return integer between 1 and 5
    }


    fun addUser(user:User){
        // add line below where addUser is being called to init profile pic
        //var bm = BitmapFactory.decodeResource(getResources(), R.drawable.baseline_person_outline_black_48dp);
        currentUser.value = user
        firebase.value?.child("users")?.child(user.username)?.setValue(user)
    }

    fun determineSign() {
        // TODO Rachey! Pop off queen!
    }

    fun editUserInfo(name: String, bio: String, image:Bitmap){
        firebase.value?.child("users")?.child(currentUser.value?.username!!)?.child("name")?.setValue(name)
        firebase.value?.child("users")?.child(currentUser.value?.username!!)?.child("bio")?.setValue(bio)
        firebase.value?.child("users")?.child(currentUser.value?.username!!)?.child("profilePic")?.setValue(image)
    }

    fun addFriend(username:String){
        if (firebase.value?.child("users")?.child(username) != null) {
            currentUser.value?.friends?.add(username)
            firebase.value?.child("users")?.child(currentUser.value?.username!!)?.child("friends")?.setValue(currentUser.value?.friends)
        }
    }

    fun getUserByName(username:String) {
        // TODO remove? might be uneeded
    }

    fun getFriendCompatibility(friend:User): Int {
        //TODO Rachael's work feeds into here
        //compare the friend's market image to yours using compareImages
        return 0
    }
    fun getFriendCompatibilityMessage(score:Int): Int {
        //TODO store in cloud?
        return 0
    }
    fun calculateDailyScore(): Int {
        //TODO makes API calls
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
                else if (currentUser.value?.friends?.contains(it.username)!!) {
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
    }
}