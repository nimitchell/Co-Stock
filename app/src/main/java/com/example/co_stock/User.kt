package com.example.co_stock

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.security.AccessController.getContext

data class User(val username :String = "",
                val birthday:String = "",
                val birthImage:MarketImage,
                var name:String = username,
                val sign:String = "",
                var bio:String = "",
                var friends:ArrayList<String>,
                var profilePic: Bitmap){
}