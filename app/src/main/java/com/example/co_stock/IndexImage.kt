package com.example.co_stock

import androidx.room.Entity
import androidx.room.PrimaryKey

class IndexImage {
    var symbol = "symbol"
    var date = ""
    var open = 0.0f
    var high = 0.0f
    var low = 0.0f
    var close = 0.0f
    var change = 0.0f
    var changePercent = 0.0f
    var changeOverTime = 0.0f
}