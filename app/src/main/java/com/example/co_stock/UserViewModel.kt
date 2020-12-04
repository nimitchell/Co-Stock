package com.example.co_stock

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import kotlin.collections.ArrayList


class UserViewModel : ViewModel(), ValueEventListener {

    val apiManager = MutableLiveData<APIManager>()          // apiManager
    var firebase = MutableLiveData<DatabaseReference>()
    var currentUser = MutableLiveData<User>()
    var currentFriend = MutableLiveData<User>()
    var currentIndex = MutableLiveData<String>()
    var dailyImage = MutableLiveData<MarketImage>()
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
        currentUser.value = User()
        dailyImage.value = MarketImage()
        apiManager.value = APIManager(this)
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

    fun setDailyImage(symbol:String, image: IndexImage) {
        when(symbol) {
            "^FTSE" -> dailyImage.value?.FTSE = image
            "^DJI" -> dailyImage.value?.DJI = image
            "^GSPTSE" -> dailyImage.value?.SNP = image
            else     -> dailyImage.value?.NASDAQ = image
        }
        dailyImage.postValue(dailyImage.value)
    }

    //fun getProfileImage(img_name: String):Bitmap{
    //}

    fun updateAuth(user:FirebaseUser) {
        userAuth.value = user
    }


    fun sortedChange(image: MarketImage): List<IndexImage>{
        var selfChangeSort = listOf(image.FTSE, image.DJI, image.SNP, image.NASDAQ)
        var selfChangeGlobal = selfChangeSort.sortedBy { it.change }
        return selfChangeGlobal
    }

    fun compareImagesChange(image: MarketImage): Int {
        var count = 1
        var moveOn = false
        var imageChange =
            listOf(image.DJI.change, image.FTSE.change, image.NASDAQ.change, image.SNP.change)
        var selfChange = listOf(
            currentUser.value?.birthImage?.DJI!!.change,
            currentUser.value?.birthImage?.FTSE!!.change,
            currentUser.value?.birthImage?.NASDAQ!!.change,
            currentUser.value?.birthImage?.SNP!!.change
        )
        var imageChangeSorted = imageChange.sorted()
        var selfChangeSorted = selfChange.sorted()
        for(k in 0..3){
            if(imageChange.get(k) == imageChangeSorted.get(0) && selfChange.get(k) == selfChangeSorted.get(0))
                moveOn = true
        }
        if(moveOn) {
            for (i in 1..3) {
                for (j in 0..3) {
                    if (imageChange.get(j) == imageChangeSorted.get(i) && selfChange.get(j) == selfChangeSorted.get(i))
                        count++
                }
            }
        }
        else{
            count = 0
        }
        return count
    }

    fun compareImagesOpen(image: MarketImage): Int {
        var count = 1
        var moveOn = false
        var imageOpen =
            listOf(image.DJI.open, image.FTSE.open, image.NASDAQ.open, image.SNP.open)
        var selfOpen = listOf(
            currentUser.value?.birthImage?.DJI!!.open,
            currentUser.value?.birthImage?.FTSE!!.open,
            currentUser.value?.birthImage?.NASDAQ!!.open,
            currentUser.value?.birthImage?.SNP!!.open
        )
        var imageOpenSorted = imageOpen.sorted()
        var selfOpenSorted = selfOpen.sorted()
        for(k in 0..3){
            if(imageOpen.get(k) == imageOpenSorted.get(0) && selfOpen.get(k) == selfOpenSorted.get(0))
                moveOn = true
        }
        if(moveOn) {
            for (i in 1..3) {
                for (j in 0..3) {
                    if (imageOpen.get(j) == imageOpenSorted.get(i) && selfOpen.get(j) == selfOpenSorted.get(i))
                        count++
                }
            }
        }
        else{
            count = 0
        }
        return count
    }

    fun compareImagesClose(image: MarketImage): Int {
        var count = 1
        var moveOn = false
        var imageClose =
            listOf(image.DJI.close, image.FTSE.close, image.NASDAQ.close, image.SNP.close)
        var selfClose = listOf(
                    currentUser.value?.birthImage?.DJI!!.close,
            currentUser.value?.birthImage?.FTSE!!.close,
            currentUser.value?.birthImage?.NASDAQ!!.close,
            currentUser.value?.birthImage?.SNP!!.close
        )
        var imageCloseSorted = imageClose.sorted()
        var selfCloseSorted = selfClose.sorted()
        for(k in 0..3){
            if(imageClose.get(k) == imageCloseSorted.get(0) && selfClose.get(k) == selfCloseSorted.get(0))
                moveOn = true
        }
        if(moveOn) {
            for (i in 1..3) {
                for (j in 0..3) {
                    if (imageClose.get(j) == imageCloseSorted.get(i) && selfClose.get(j) == selfCloseSorted.get(i))
                        count++
                }
            }
        }
        else{
            count = 0
        }
        return count
    }

    fun compareImagesHigh(image: MarketImage): Int {
        var count = 1
        var moveOn = false
        var imageHigh =
            listOf(image.DJI.high, image.FTSE.high, image.NASDAQ.high, image.SNP.high)
        var selfHigh = listOf(
            currentUser.value?.birthImage?.DJI!!.high,
            currentUser.value?.birthImage?.FTSE!!.high,
            currentUser.value?.birthImage?.NASDAQ!!.high,
            currentUser.value?.birthImage?.SNP!!.high
        )
        var imageHighSorted = imageHigh.sorted()
        var selfHighSorted = selfHigh.sorted()
        for(k in 0..3){
            if(imageHigh.get(k) == imageHighSorted.get(0) && selfHigh.get(k) == selfHighSorted.get(0))
                moveOn = true
        }
        if(moveOn) {
            for (i in 1..3) {
                for (j in 0..3) {
                    if (imageHigh.get(j) == imageHighSorted.get(i) && selfHigh.get(j) == selfHighSorted.get(i))
                        count++
                }
            }
        }
        else{
            count = 0
        }
        return count
    }

    fun compareImagesLow(image: MarketImage): Int {
        var count = 0
        var moveOn = false
        var imageLow =
            listOf(image.DJI.low, image.FTSE.low, image.NASDAQ.low, image.SNP.low)
        var selfLow = listOf(
            currentUser.value?.birthImage?.DJI!!.low,
            currentUser.value?.birthImage?.FTSE!!.low,
            currentUser.value?.birthImage?.NASDAQ!!.low,
            currentUser.value?.birthImage?.SNP!!.low
        )
        var imageLowSorted = imageLow.sorted()
        var selfLowSorted = selfLow.sorted()
        for(k in 0..3){
            if(imageLow.get(k) == imageLowSorted.get(3) && selfLow.get(k) == selfLowSorted.get(3))
                moveOn = true
        }
        if(moveOn) {
            for (i in 0..2) {
                for (j in 0..3) {
                    if (imageLow.get(j) == imageLowSorted.get(i) && selfLow.get(j) == selfLowSorted.get(i))
                        count++
                }
            }
        }
        else{
            count = 0
        }
        return count
    }

    fun compareImages(image: MarketImage): Int{
        var count = 0
        var change = compareImagesChange(image)
        if (change > 0)
            count++
        var open = compareImagesOpen(image)
        if (open > 0)
            count++
            var close = compareImagesClose(image)
        if (close > 0)
            count++
            var high = compareImagesHigh(image)
        if (high > 0)
            count++
        var low = compareImagesLow(image)
        if (low > 0)
            count++
        return count
    }

    fun addUser(user:User){
        currentUser.value = user
        apiManager.value?.fetchImage(user.birthday)
        firebase.value?.child("users")?.child(user.username)?.setValue(currentUser.value)
    }

    fun updateMI(symbol:String, image: IndexImage) {
        Log.d("hello", "working")
        when(symbol) {
            "^FTSE" -> firebase.value?.child("users")?.child(currentUser.value?.username!!)?.child("birthImage")?.child("ftse")?.setValue(image)
            "^DJI" -> firebase.value?.child("users")?.child(currentUser.value?.username!!)?.child("birthImage")?.child("dji")?.setValue(image)
            "^GSPTSE" -> firebase.value?.child("users")?.child(currentUser.value?.username!!)?.child("birthImage")?.child("snp")?.setValue(image)
            else     -> firebase.value?.child("users")?.child(currentUser.value?.username!!)?.child("birthImage")?.child("nasdaq")?.setValue(image)
            }
        }

    fun determineSign(){
        val image = currentUser.value?.birthImage!!
        var selfChangeGlobal = sortedChange(image)
        var symbol = selfChangeGlobal.get(0).symbol
        var sign = ""
        when(symbol) {
            "^FTSE" -> sign = "FTSE-o"
            "^DJI" -> sign = "Dow Jones-ces"
            "^GSPTSE" -> sign = "SNP-isces"
            "^IXIC" -> sign = "NASDAQ-rius"
            else     -> sign = ""
        }
        firebase.value?.child("users")?.child(currentUser.value?.username!!)?.child("sign")?.setValue(sign)
        var signs = arrayListOf<String>()
        selfChangeGlobal.forEach {
            var cur = ""
            when(it.symbol) {
                "^FTSE" -> cur = "FTSE"
                "^DJI" -> cur = "Dow Jones"
                "^GSPTSE" -> cur = "SNP"
                "^IXIC" -> cur = "NASDAQ"
                else     -> cur = ""
            }
            signs.add(cur)}
        firebase.value?.child("users")?.child(currentUser.value?.username!!)?.child("signs")?.setValue(signs)




    }

    fun editUserInfo(name: String?, bio: String?){
        if (name!= null && name != ""){
            firebase.value?.child("users")?.child(currentUser.value?.username!!)?.child("name")?.setValue(name)
        }
        if (bio != null){
            firebase.value?.child("users")?.child(currentUser.value?.username!!)?.child("bio")?.setValue(bio)
        }
    }

    fun addFriend(username:String){
        if (firebase.value?.child("users")?.child(username) != null) {
            var newFriends = arrayListOf<String>()
            if (currentUser.value?.friends?.size!! > 0)
                newFriends = currentUser.value?.friends as ArrayList<String>
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
    fun setUser(user:User) {
        currentUser.postValue(user)
        val valueEventListener = object : ValueEventListener {
            var tmpFriends = ArrayList<User>()
            var tmpUser = currentUser.value
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("onDataCHange", snapshot.toString())
                snapshot.child("users").children.forEach {
                    it.getValue(User::class.java)?.let {
                        // update current user
                        if (it.email == userAuth.value?.email) {
                            Log.d("onDataChange", it.username)
                            tmpUser = it
                        }
                        // update friends list
                        else if (currentUser.value?.friends?.contains(it.username) != null && currentUser.value?.friends?.contains(
                                it.username
                            )!!
                        ) {
                            tmpFriends.add(it)
                        }
                    }
                }
                currentUser.postValue(tmpUser)
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
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("error", databaseError.getMessage())
            }
        }
        firebase.value?.addListenerForSingleValueEvent(valueEventListener)

    }
    fun setFriend(user:User) {
        currentFriend.value = user
        currentFriend.postValue(user)
    }

    // TODO remove later
    fun getUserByName(email:String) {
        val dbRef = firebase.value
        val userRef = dbRef?.child("users")?.orderByChild("email")?.equalTo(email)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val user = ds.getValue(User::class.java)
                    currentUser.value = user
                    currentUser.postValue(currentUser.value)
                    Log.d("getUserByName", currentUser.value?.username!!)
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
        return compareImages(friend.birthImage)
    }

    fun getFriendCompatibilityMessage(friend: String, score:Int): String {
        var comp = compatibility.value?.get(score)!!
        var output = "For you, ${friend} is a ${comp.personality}:\n${comp.message}"
        return output
    }

    fun calculateDailyScore(): Int {
        return compareImages(dailyImage.value!!)
    }

    fun getDailyReport(score:Int) : String{
        Log.d("dailyMessage", dailyMessage.value.toString())
        Log.d("score", score.toString())
        return dailyMessage.value?.get(score)!!
    }

    fun getIndexScore(index:String): Int {
        // TODO Rachael: write code that determins if the daily image for this stock is good or bad (might just check if change is positive or negative turn it into 1-5

        return 1
    }

    fun getIndexReport(score:Int) : String{
        Log.d("getIndexReport", indexMessage.value.toString())
        Log.d("score", score.toString())
        return indexMessage.value?.get(score)!!
    }

    fun getRandomQuote(): String{
        var tmpList = quotes.value
        return tmpList?.shuffled()?.take(1)!![0]
    }

    override fun onDataChange(snapshot: DataSnapshot) {
        var tmpFriends = ArrayList<User>()
        var tmpUser = currentUser.value
        // get users
        snapshot.child("users").children.forEach {
            it.getValue(User::class.java)?.let {
                // update current user
                if(it.email == userAuth.value?.email) {
                    tmpUser = it
                }
                // update friends list
                else if (currentUser.value?.friends?.contains(it.username) != null && currentUser.value?.friends?.contains(it.username)!!) {
                    tmpFriends.add(it)
                }
            }
        }
        currentUser.postValue(tmpUser)
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