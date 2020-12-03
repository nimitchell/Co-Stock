package com.example.co_stock

data class MarketImage(var FTSE: IndexImage = IndexImage(),
                       var DJI: IndexImage = IndexImage(),
                       var SNP: IndexImage = IndexImage(),
                       var NASDAQ: IndexImage = IndexImage()
){
}