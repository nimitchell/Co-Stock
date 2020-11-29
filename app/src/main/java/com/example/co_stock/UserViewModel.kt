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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream

class UserViewModel : ViewModel(), ValueEventListener {

    var firebase = MutableLiveData<DatabaseReference>()
    var currentUser = MutableLiveData<User>()
    var currentFriend = MutableLiveData<User>()
    var currentIndex = MutableLiveData<String>()
    var userAuth = MutableLiveData<FirebaseUser>()
    var friends = MutableLiveData<ArrayList<User>>()
    var quotes = MutableLiveData<ArrayList<String>>()
    var dailyMessage = MutableLiveData<Map<Int, String>>()
    var indexMessage = MutableLiveData<Map<Int, String>>()
    var compatibility = MutableLiveData<Map<Int, Compatibility>>()
    var storage = MutableLiveData<StorageReference>()

    init {
        firebase.value = Firebase.database.getReference("") // empty string means get root
        firebase.value?.addValueEventListener(this)
        storage.value = FirebaseStorage.getInstance().getReference("images")
    }

    fun setImage(name: String, image:Bitmap){
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val ref = storage.value?.child(name)
        var uploadTask = ref?.putBytes(data)
        val urlTask = uploadTask?.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            ref?.downloadUrl
        }?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
            } else {
                Log.d("Failure", "Upload Failed")
            }
        }
    }

    fun getProfileImage(img_name: String):Bitmap{
        var imageRef = storage.value?.child(img_name)
        lateinit var imagebytes:ByteArray
        val ONE_MEGABYTE: Long = 1024 * 1024
        imageRef?.getBytes(ONE_MEGABYTE)?.addOnSuccessListener {
            // Data for "images/island.jpg" is returned, use this as needed
            imagebytes = it
        }?.addOnFailureListener {
            // Handle any errors
            throw it
        }
        return BitmapFactory.decodeByteArray(imagebytes,0, imagebytes.size)
    }

    fun updateAuth(user:FirebaseUser) {
        userAuth.value = user
    }

    fun updateUserData() {
        // TODO maybe uneeded?
    }

    fun compareImagesChange(image: MarketImage): Int{
        var count = 0
        var maxImageChange = maxOf(image.DJI.change, image.FTSE.change, image.NASDAQ.change, image.SNP.change)
        var selfImageChange = maxOf(currentUser.value?.birthImage?.DJI!!.change, currentUser.value?.birthImage?.FTSE!!.change, currentUser.value?.birthImage?.NASDAQ!!.change, currentUser.value?.birthImage?.SNP!!.change)
        if(maxImageChange == image.DJI.change && selfImageChange == currentUser.value?.birthImage?.DJI!!.change) {
            count++

        }
        else if(maxImageChange == image.FTSE.change && selfImageChange == currentUser.value?.birthImage?.FTSE!!.change)
            count++
        else if(maxImageChange == image.NASDAQ.change && selfImageChange == currentUser.value?.birthImage?.NASDAQ!!.change)
            count++
        else if(maxImageChange == image.SNP.change && selfImageChange == currentUser.value?.birthImage?.SNP!!.change)
            count++
        return count
    }

    fun compareImages(image: MarketImage): Int{
        var count = 0
        var maxImageChange = maxOf(image.DJI.change, image.FTSE.change, image.NASDAQ.change, image.SNP.change)
        var selfImageChange = maxOf(currentUser.value?.birthImage?.DJI!!.change, currentUser.value?.birthImage?.FTSE!!.change, currentUser.value?.birthImage?.NASDAQ!!.change, currentUser.value?.birthImage?.SNP!!.change)
        if(maxImageChange == image.DJI.change && selfImageChange == currentUser.value?.birthImage?.DJI!!.change)
            count++
        else if(maxImageChange == image.FTSE.change && selfImageChange == currentUser.value?.birthImage?.FTSE!!.change)
            count++
        else if(maxImageChange == image.NASDAQ.change && selfImageChange == currentUser.value?.birthImage?.NASDAQ!!.change)
            count++
        else if(maxImageChange == image.SNP.change && selfImageChange == currentUser.value?.birthImage?.SNP!!.change)
            count++
        var maxImageOpen = maxOf(image.DJI.open, image.FTSE.open, image.NASDAQ.open, image.SNP.open)
        var selfImageOpen = maxOf(currentUser.value?.birthImage?.DJI!!.open, currentUser.value?.birthImage?.FTSE!!.open, currentUser.value?.birthImage?.NASDAQ!!.open, currentUser.value?.birthImage?.SNP!!.open)
        if(maxImageOpen == image.DJI.open && selfImageOpen == currentUser.value?.birthImage?.DJI!!.open)
            count++
        else if(maxImageOpen == image.FTSE.open && selfImageOpen == currentUser.value?.birthImage?.FTSE!!.open)
            count++
        else if(maxImageOpen == image.NASDAQ.open && selfImageOpen == currentUser.value?.birthImage?.NASDAQ!!.open)
            count++
        else if(maxImageOpen == image.SNP.open && selfImageOpen == currentUser.value?.birthImage?.SNP!!.open)
            count++
        var maxImageClose = maxOf(image.DJI.close, image.FTSE.close, image.NASDAQ.close, image.SNP.close)
        var selfImageClose = maxOf(currentUser.value?.birthImage?.DJI!!.close, currentUser.value?.birthImage?.FTSE!!.close, currentUser.value?.birthImage?.NASDAQ!!.close, currentUser.value?.birthImage?.SNP!!.close)
        if(maxImageClose == image.DJI.close && selfImageClose == currentUser.value?.birthImage?.DJI!!.close)
            count++
        else if(maxImageClose == image.FTSE.close && selfImageClose == currentUser.value?.birthImage?.FTSE!!.close)
            count++
        else if(maxImageClose == image.NASDAQ.close && selfImageClose == currentUser.value?.birthImage?.NASDAQ!!.close)
            count++
        else if(maxImageClose == image.SNP.close && selfImageClose == currentUser.value?.birthImage?.SNP!!.close)
            count++
        var maxImageHigh = maxOf(image.DJI.high, image.FTSE.high, image.NASDAQ.high, image.SNP.high)
        var selfImageHigh = maxOf(currentUser.value?.birthImage?.DJI!!.high, currentUser.value?.birthImage?.FTSE!!.high, currentUser.value?.birthImage?.NASDAQ!!.high, currentUser.value?.birthImage?.SNP!!.high)
        if(maxImageHigh == image.DJI.high && selfImageHigh == currentUser.value?.birthImage?.DJI!!.high)
            count++
        else if(maxImageHigh == image.FTSE.high && selfImageHigh == currentUser.value?.birthImage?.FTSE!!.high)
            count++
        else if(maxImageHigh == image.NASDAQ.high && selfImageHigh == currentUser.value?.birthImage?.NASDAQ!!.high)
            count++
        else if(maxImageHigh == image.SNP.high && selfImageHigh == currentUser.value?.birthImage?.SNP!!.high)
            count++
        var minImageLow = minOf(image.DJI.low, image.FTSE.low, image.NASDAQ.low, image.SNP.low)
        var selfImageLow = minOf(currentUser.value?.birthImage?.DJI!!.low, currentUser.value?.birthImage?.FTSE!!.low, currentUser.value?.birthImage?.NASDAQ!!.low, currentUser.value?.birthImage?.SNP!!.low)
        if(minImageLow == image.DJI.low && selfImageLow == currentUser.value?.birthImage?.DJI!!.low)
            count++
        else if(minImageLow == image.FTSE.low && selfImageLow == currentUser.value?.birthImage?.FTSE!!.low)
            count++
        else if(minImageLow == image.NASDAQ.low && selfImageLow == currentUser.value?.birthImage?.NASDAQ!!.low)
            count++
        else if(minImageLow == image.SNP.low && selfImageLow == currentUser.value?.birthImage?.SNP!!.low)
            count++
        return count
    }

    fun addUser(user:User){
        currentUser.value = user
        Log.d("addUser", currentUser.value?.username!!)
        firebase.value?.child("users")?.child(user.username)?.setValue(user)
    }

    fun determineSign(date:String):String {
        // TODO Rachey! Pop off queen!
        return ""
    }

    fun editUserInfo(name: String?, bio: String?, image:Bitmap?){
        if (name!= null){
            firebase.value?.child("users")?.child(currentUser.value?.username!!)?.child("name")?.setValue(name)
        }
        if (bio != null){
            firebase.value?.child("users")?.child(currentUser.value?.username!!)?.child("bio")?.setValue(bio)
        }
        if (image != null) {
            firebase.value?.child("users")?.child(currentUser.value?.username!!)?.child("profilePic")?.setValue(image)
        }
    }

    fun addFriend(username:String){
        if (firebase.value?.child("users")?.child(username) != null) {
            var newFriends = currentUser.value?.friends as ArrayList<String>
            newFriends.add(username)
            currentUser.value?.friends = newFriends.toList()
            firebase.value?.child("users")?.child(currentUser.value?.username!!)?.child("friends")?.setValue(currentUser.value?.friends)
        }
    }

    fun removeFriend(username: String){
        if (currentUser.value?.friends?.contains(username)!!) {
            var newFriends = currentUser.value?.friends as ArrayList<String>
            newFriends.remove(username)
            currentUser.value?.friends = newFriends.toList()
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
                    currentUser.value = user
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

    fun getIndexScore(stock:String): Int{
        return 0
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
                if(it.email == userAuth.value?.email) {
                    Log.d("onDataChange", it.username)
                    currentUser.value = it
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