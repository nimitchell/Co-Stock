package com.example.co_stock

data class User(val username :String = "",
                val birthday:String = "",
                val birthImage:MarketImage,
                var name:String = username,
                val sign:String = "",
                var bio:String = "",
                var friends:Array<User>) {
}