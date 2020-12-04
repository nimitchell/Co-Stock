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

    val apiManager = MutableLiveData<APIManager>()
    var firebase = MutableLiveData<DatabaseReference>()
    var currentUser = MutableLiveData<User>()
    var currentFriend = MutableLiveData<User>()
    var currentIndex = MutableLiveData<IndexImage>()
    var dailyImage = MutableLiveData<MarketImage>()
    var userAuth = MutableLiveData<FirebaseUser>()
    var friends = MutableLiveData<ArrayList<User>>()
    var quotes = MutableLiveData<ArrayList<String>>()
    var dailyMessage = MutableLiveData<Map<Int, String>>()
    var indexMessage = MutableLiveData<Map<Int, String>>()
    var compatibility = MutableLiveData<Map<Int, Compatibility>>()
    var storage = MutableLiveData<StorageReference>()
    var profilePic = MutableLiveData<Bitmap>()

    init {
        // Empty string means get root
        firebase.value = Firebase.database.getReference("")
        firebase.value?.addValueEventListener(this)
        storage.value = FirebaseStorage.getInstance().getReference("images")
        currentUser.value = User()
        dailyImage.value = MarketImage()
    }

    // Retrieving profile image from firebase storage with user's username
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

    // Setting dailyImage with with specified stock index and IndexImage
    fun setDailyImage(symbol:String, image: IndexImage) {
        when(symbol) {
            "^FTSE" -> dailyImage.value?.FTSE = image
            "^DJI" -> dailyImage.value?.DJI = image
            "^GSPC" -> dailyImage.value?.SNP = image
            else     -> dailyImage.value?.NASDAQ = image
        }
        dailyImage.postValue(dailyImage.value)
    }

    // Update firebase user authentication
    fun updateAuth(user:FirebaseUser) {
        userAuth.value = user
    }

    // Sorts the market image indices for a specific user's results
    fun sortedChange(image: MarketImage): List<IndexImage>{
        var selfChangeSort = listOf(image.FTSE, image.DJI, image.SNP, image.NASDAQ)
        var selfChangeGlobal = selfChangeSort.sortedBy { it.change }
        return selfChangeGlobal
    }

    // Compares current inputted MarketImage change with MarketImage on user's date of birth change
    fun compareImagesChange(image: MarketImage): Int {
        var count = 1
        var moveOn = false
        // Listing current marketImage change
        var imageChange =
            listOf(image.DJI.change, image.FTSE.change, image.NASDAQ.change, image.SNP.change)
        // Listing user's birth MarketImage change
        var selfChange = listOf(
            currentUser.value?.birthImage?.DJI!!.change,
            currentUser.value?.birthImage?.FTSE!!.change,
            currentUser.value?.birthImage?.NASDAQ!!.change,
            currentUser.value?.birthImage?.SNP!!.change
        )
        // Sorting them
        var imageChangeSorted = imageChange.sorted()
        var selfChangeSorted = selfChange.sorted()
        // Checking to see is the top values (the largest change) is equal in both sorted lists
        for(k in 0..3){
            if(imageChange.get(k) == imageChangeSorted.get(0) && selfChange.get(k) == selfChangeSorted.get(0))
                moveOn = true
        }
        // If the top values are equal, increments how many other values are equal in sorted lists
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
        // Returning the number of equal values in the sorted list
        return count
    }

    // Compares current inputted MarketImage open with MarketImage on user's date of birth open
    fun compareImagesOpen(image: MarketImage): Int {
        var count = 1
        var moveOn = false
        // Listing current marketImage open
        var imageOpen =
            listOf(image.DJI.open, image.FTSE.open, image.NASDAQ.open, image.SNP.open)
        // Listing user's birth MarketImage open
        var selfOpen = listOf(
            currentUser.value?.birthImage?.DJI!!.open,
            currentUser.value?.birthImage?.FTSE!!.open,
            currentUser.value?.birthImage?.NASDAQ!!.open,
            currentUser.value?.birthImage?.SNP!!.open
        )
        // Sorting them
        var imageOpenSorted = imageOpen.sorted()
        var selfOpenSorted = selfOpen.sorted()
        // Checking to see is the top values (the largest open) is equal in both sorted lists
        for(k in 0..3){
            if(imageOpen.get(k) == imageOpenSorted.get(0) && selfOpen.get(k) == selfOpenSorted.get(0))
                moveOn = true
        }
        // If the top values are equal, increments how many other values are equal in sorted lists
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
        // Returning the number of equal values in the sorted list
        return count
    }

    // Compares current inputted MarketImage close with MarketImage on user's date of birth close
    fun compareImagesClose(image: MarketImage): Int {
        var count = 1
        var moveOn = false
        // Listing current marketImage close
        var imageClose =
            listOf(image.DJI.close, image.FTSE.close, image.NASDAQ.close, image.SNP.close)
        // Listing user's birth MarketImage close
        var selfClose = listOf(
                    currentUser.value?.birthImage?.DJI!!.close,
            currentUser.value?.birthImage?.FTSE!!.close,
            currentUser.value?.birthImage?.NASDAQ!!.close,
            currentUser.value?.birthImage?.SNP!!.close
        )
        // Sorting them
        var imageCloseSorted = imageClose.sorted()
        var selfCloseSorted = selfClose.sorted()
        // Checking to see is the top values (the largest close) is equal in both sorted lists
        for(k in 0..3){
            if(imageClose.get(k) == imageCloseSorted.get(0) && selfClose.get(k) == selfCloseSorted.get(0))
                moveOn = true
        }
        // If the top values are equal, increments how many other values are equal in sorted lists
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
        // Returning the number of equal values in the sorted list
        return count
    }

    // Compares current inputted MarketImage high with MarketImage on user's date of birth high
    fun compareImagesHigh(image: MarketImage): Int {
        var count = 1
        var moveOn = false
        // Listing current marketImage high
        var imageHigh =
            listOf(image.DJI.high, image.FTSE.high, image.NASDAQ.high, image.SNP.high)
        // Listing user's birth MarketImage high
        var selfHigh = listOf(
            currentUser.value?.birthImage?.DJI!!.high,
            currentUser.value?.birthImage?.FTSE!!.high,
            currentUser.value?.birthImage?.NASDAQ!!.high,
            currentUser.value?.birthImage?.SNP!!.high
        )
        // Sorting them
        var imageHighSorted = imageHigh.sorted()
        var selfHighSorted = selfHigh.sorted()
        // Checking to see is the top values (the largest high) is equal in both sorted lists
        for(k in 0..3){
            if(imageHigh.get(k) == imageHighSorted.get(0) && selfHigh.get(k) == selfHighSorted.get(0))
                moveOn = true
        }
        // If the top values are equal, increments how many other values are equal in sorted lists
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
        // Returning the number of equal values in the sorted list
        return count
    }

    // Compares current inputted MarketImage low with MarketImage on user's date of birth low
    fun compareImagesLow(image: MarketImage): Int {
        var count = 0
        var moveOn = false
        // Listing current marketImage low
        var imageLow =
            listOf(image.DJI.low, image.FTSE.low, image.NASDAQ.low, image.SNP.low)
        // Listing user's birth MarketImage low
        var selfLow = listOf(
            currentUser.value?.birthImage?.DJI!!.low,
            currentUser.value?.birthImage?.FTSE!!.low,
            currentUser.value?.birthImage?.NASDAQ!!.low,
            currentUser.value?.birthImage?.SNP!!.low
        )
        // Sorting them
        var imageLowSorted = imageLow.sorted()
        var selfLowSorted = selfLow.sorted()
        // Checking to see is the top values (the largest low) is equal in both sorted lists
        for(k in 0..3){
            if(imageLow.get(k) == imageLowSorted.get(3) && selfLow.get(k) == selfLowSorted.get(3))
                moveOn = true
        }
        // If the top values are equal, increments how many other values are equal in sorted lists
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
        // Returning the number of equal values in the sorted list
        return count
    }

    // Increments user's compatibility score with inputted MarketImage based on their differences
    fun compareImages(image: MarketImage): Int{
        var count = 0
        var change = compareImagesChange(image)
        if (change == 0)
            count++
        var open = compareImagesOpen(image)
        if (open == 0)
            count++
        var close = compareImagesClose(image)
        if (close == 0)
            count++
        var high = compareImagesHigh(image)
        if (high == 0)
            count++
        var low = compareImagesLow(image)
        if (low == 0)
            count++
        if (count == 0)
            return 1
        return count
    }

    // Saves the user and fetches their birthImage and adds them to the firebase
    fun addUser(user:User){
        currentUser.value = user
        apiManager.value?.fetchImage(user.birthday)
        firebase.value?.child("users")?.child(user.username)?.setValue(currentUser.value)
    }

    // Updates the user's birthImage in firebase with specified index and IndexImage
    fun updateMI(symbol:String, image: IndexImage) {
        when(symbol) {
            "^FTSE" -> firebase.value?.child("users")?.child(currentUser.value?.username!!)?.child("birthImage")?.child("ftse")?.setValue(image)
            "^DJI" -> firebase.value?.child("users")?.child(currentUser.value?.username!!)?.child("birthImage")?.child("dji")?.setValue(image)
            "^GSPC" -> firebase.value?.child("users")?.child(currentUser.value?.username!!)?.child("birthImage")?.child("snp")?.setValue(image)
            else     -> firebase.value?.child("users")?.child(currentUser.value?.username!!)?.child("birthImage")?.child("nasdaq")?.setValue(image)
        }
    }

    // Determines a user's primary sign and their other list/order of signs
    fun determineSign(){
        val image = currentUser.value?.birthImage!!
        var selfChangeGlobal = sortedChange(image)
        var symbol = selfChangeGlobal.get(0).symbol
        var sign = ""
        // Gives user special sign name based on their top stock index
        when(symbol) {
            "^FTSE" -> sign = "FTSE-o"
            "^DJI" -> sign = "Dow Jones-ces"
            "^GSPC" -> sign = "SNP-isces"
            "^IXIC" -> sign = "NASDAQ-rius"
            else     -> sign = ""
        }
        // Inputs this sign into firebase
        firebase.value?.child("users")?.child(currentUser.value?.username!!)?.child("sign")?.setValue(sign)
        // Lists user's signs in order highest value to lowest on their birthday
        var signs = arrayListOf<String>()
        selfChangeGlobal.forEach {
            var cur = ""
            when(it.symbol) {
                "^FTSE" -> cur = "FTSE"
                "^DJI" -> cur = "Dow Jones"
                "^GSPC" -> cur = "SNP"
                "^IXIC" -> cur = "NASDAQ"
                else     -> cur = ""
            }
            signs.add(cur)
        }
        // Saves user sign list in firebase
        firebase.value?.child("users")?.child(currentUser.value?.username!!)?.child("signs")?.setValue(signs)
    }

    // If inputs are not null, updates user information in firebase
    fun editUserInfo(name: String?, bio: String?){
        if (name!= null && name != ""){
            firebase.value?.child("users")?.child(currentUser.value?.username!!)?.child("name")?.setValue(name)
        }
        if (bio != null){
            firebase.value?.child("users")?.child(currentUser.value?.username!!)?.child("bio")?.setValue(bio)
        }
    }

    // Adds friend to user's friend list with inputted username
    fun addFriend(username:String){
        // Checks is user exists in firebase
        if (firebase.value?.child("users")?.child(username) != null) {
            var newFriends = arrayListOf<String>()
            // Copies existing friend into friend list
            if (currentUser.value?.friends?.size!! > 0)
                newFriends = currentUser.value?.friends as ArrayList<String>
            // Adds new friend to list and firebase
            newFriends.add(username)
            currentUser.value?.friends = newFriends.toList()
            firebase.value?.child("users")?.child(currentUser.value?.username!!)?.child("friends")?.setValue(currentUser.value?.friends)
        }
    }

    // Removes friend from user's friend list with inputted username
    fun removeFriend(username: String){
        // Checks is user exists in friend list
        if (currentUser.value?.friends?.contains(username)!!) {
            // Removes friend from users friend list and updates currentUser and firebase
            var newFriends = currentUser.value?.friends as ArrayList<String>
            newFriends.remove(username)
            currentUser.value?.friends = newFriends.toList()
            firebase.value?.child("users")?.child(currentUser.value?.username!!)?.child("friends")
                ?.setValue(currentUser.value?.friends)
        }
    }

    // Sets the currentUser as inputted user
    fun setUser(user:User) {
        currentUser.postValue(user)

        // Defining event listener to override onDataChange
        val valueEventListener = object : ValueEventListener {
            var tmpFriends = ArrayList<User>()
            var tmpUser = currentUser.value
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.child("users").children.forEach {
                    it.getValue(User::class.java)?.let {
                        // Update current user information with any previously stored information
                        if (it.email == userAuth.value?.email) {
                            tmpUser = it
                        }
                        // Update friends list with any previously stored friends
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

                // Get list quotes
                var tmpQuotes = ArrayList<String>()
                snapshot.child("quotes").children.forEach {
                    it.getValue(String::class.java)?.let {
                        tmpQuotes.add(it)
                    }
                }
                quotes.postValue(tmpQuotes)

                // Get map of daily messages
                var tmpDaily = mutableMapOf<Int, String>()
                var counter = 0
                snapshot.child("daily_message").children.forEach {
                    it.getValue(String::class.java)?.let {
                        counter++
                        tmpDaily[counter] = it
                    }
                }
                dailyMessage.postValue(tmpDaily)

                // Get map of index messages
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
        // Sets firebase to update with valueEventListener
        firebase.value?.addListenerForSingleValueEvent(valueEventListener)
    }

    // Sets currentFriend to inputted user
    fun setFriend(user:User) {
        currentFriend.value = user
        currentFriend.postValue(user)
    }

    // Checks if username already exists in firebase
    fun checkUserExists(username:String):Boolean {
        val dbRef = firebase.value
        // Gets equalTo query from firebase
        val userRef = dbRef?.child("users")?.orderByChild("username")?.equalTo(username)
        var exists = false
        // Defining event listener to override onDataChange
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Changes exists to true if it is in firebase
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

    // Compares the inputted friend's MarketImage to currentUser's
    fun getFriendCompatibility(friend:User): Int {
        return compareImages(friend.birthImage)
    }

    // Returns message based on inputted friend and compatibility score
    fun getFriendCompatibilityMessage(friend: String, score:Int): String {
        var comp = compatibility.value?.get(score)!!
        var output = "For you, ${friend} is a ${comp.personality}:\n${comp.message}"
        return output
    }

    // Compares the current day's MarketImage to currentUser's
    fun calculateDailyScore(): Int {
        return compareImages(dailyImage.value!!)
    }

    // Returns message based on inputted daily score
    fun getDailyReport(score:Int) : String{
        return dailyMessage.value?.get(score)!!
    }

    // Returns integer based on how much inputted IndexImage has changed that day
    fun getIndexScore(index:IndexImage): Int {
        var percentChange: Int
        if (index.changePercent!! <= -1)
            percentChange = 1
        else if (index.changePercent!! < -0.1)
            percentChange = 2
        else if (index.changePercent!! >= 1)
            percentChange = 5
        else if (index.changePercent!! > 0.1)
            percentChange = 4
         else
            percentChange = 3

        return percentChange
    }

    // Returns user friendly index name with inputted encoded index name
    fun getIndexName(index:String): String {
        when(index) {
            "^FTSE" -> return "FTSE"
            "^DJI" -> return "Dow Jones"
            "^GSPC" -> return "SNP"
            "^IXIC" -> return "NASDAQ"
            else     -> return ""
        }
    }

    // Returns the inputted index's description
    fun getIndexDescription(index:String): String {
        when(index) {
            "^FTSE" -> return "The people of this sign are very loyal to others. Thay also usually find great passion in their trade. But their focus of these attributes usually also leads to them not prioritizing their own health or wellbeing."
            "^DJI" -> return "This sign is populated by natural leaders. Their independent and influential nature leads many people to look to them for advice and guidance. However, this sign is vulnerable to being overconfident and letting their influence on others get to their heads."
            "^GSPC" -> return "This sign is full of very dependable and trustworthy people. But because they value their reliability it could lead them to be over-cautious in life and unhealthy perfectionists."
            "^IXIC" -> return "The people of this sign are uniquely intelligent. They love taking in new information from anything they come across. However, this sign is also very exclusive with the people they let into their inner circle and they also have a vulnerability of co-dependence."
            else     -> return ""
        }
    }

    // Returns message based on inputted index score
    fun getIndexReport(score:Int) : String{
        return indexMessage.value?.get(score)!!
    }

    // Returns random quote
    fun getRandomQuote(): String{
        var tmpList = quotes.value
        return tmpList?.shuffled()?.take(1)!![0]
    }

    // Overrides onDataChange for viewModel
    override fun onDataChange(snapshot: DataSnapshot) {
        var tmpFriends = ArrayList<User>()
        var tmpUser = currentUser.value
        // Get user information from firebase
        snapshot.child("users").children.forEach {
            it.getValue(User::class.java)?.let {
                // Update current user
                if(it.email == userAuth.value?.email) {
                    tmpUser = it
                }
                // Update friends list
                else if (currentUser.value?.friends?.contains(it.username) != null && currentUser.value?.friends?.contains(it.username)!!) {
                    tmpFriends.add(it)
                }
            }
        }
        currentUser.postValue(tmpUser)
        friends.postValue(tmpFriends)

        // Get list of quotes
        var tmpQuotes = ArrayList<String>()
        snapshot.child("quotes").children.forEach {
            it.getValue(String::class.java)?.let {
                tmpQuotes.add(it)
            }
        }
        quotes.postValue(tmpQuotes)

        // Gets map of daily messages
        var tmpDaily = mutableMapOf<Int, String>()
        var counter = 0
        snapshot.child("daily_message").children.forEach {
            it.getValue(String::class.java)?.let {
                counter++
                tmpDaily[counter] = it
            }
        }
        dailyMessage.postValue(tmpDaily)

        // Gets map index messages
        var tmpIndex = mutableMapOf<Int, String>()
        counter = 0
        snapshot.child("index_message").children.forEach {
            it.getValue(String::class.java)?.let {
                counter++
                tmpIndex[counter] = it
            }
        }
        indexMessage.postValue(tmpIndex)

        // Gets map of compatibility messages
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