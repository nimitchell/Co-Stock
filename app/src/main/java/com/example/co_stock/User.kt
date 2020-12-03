package com.example.co_stock

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.security.AccessController.getContext

data class User(val username :String = "",
                val email :String = "",
                val birthday:String = "",
                val birthImage:MarketImage = MarketImage(),
                var name:String = username,
                var sign:String = "",
                var bio:String = "",
                var friends:List<String> = emptyList(),
                var profilePic: String = username) {
}