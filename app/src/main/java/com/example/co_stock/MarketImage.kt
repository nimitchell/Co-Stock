package com.example.co_stock

data class MarketImage(val FTSE: IndexImage = IndexImage(),
                       val DJI: IndexImage = IndexImage(),
                       val SNP: IndexImage = IndexImage(),
                       val NASDAQ: IndexImage = IndexImage()) {
}