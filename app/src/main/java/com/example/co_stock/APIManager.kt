package com.example.co_stock

import android.util.Log
import androidx.core.content.res.TypedArrayUtils.getString
import com.example.co_stock.UserViewModel
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

class APIManager(val userViewModel: UserViewModel) {
    private val apiURL = "https://financialmodelingprep.com/"
    private val apiKey = "abe86a7e029d4e80007fb927cc8ca733"

    val retrofit = Retrofit.Builder()
        .baseUrl(apiURL)
        .build()

    val service = retrofit.create(IndexService::class.java)

    interface IndexService {
        //different functions for different indices
        @GET("api/v3/historical-price-full/%5EFTSE?")
        fun getFTSE(
            @Query("api_key") api_key: String
        ): Call<ResponseBody>

        @GET("api/v3/historical-price-full/%5EDJI?")
        fun getDJI(
            @Query("api_key") api_key: String
        ): Call<ResponseBody>

        @GET("api/v3/historical-price-full/%5EGSPTSE?")
        fun getSNP(
            @Query("api_key") api_key: String
        ): Call<ResponseBody>

        @GET("api/v3/historical-price-full/%5EIXIC?")
        fun getNASDAQ(
            @Query("api_key") api_key: String
        ): Call<ResponseBody>
    }

    fun decodeJson(json: String, date: String) {
        val image = IndexImage()
        val data = JSONObject(json)
        val hist = data.getJSONArray("historical")
        for (i in 0 until hist.length()){
            val cur = hist.getJSONObject(i)
            if(cur.getString("date").substring(5) == date.substring(5))
            image.symbol = cur.getString("symbol")
            image.date = cur.getString("date")
            image.open = cur.getDouble("open").toFloat()
            image.high = cur.getDouble("high").toFloat()
            image.low = cur.getDouble("low").toFloat()
            image.close = cur.getDouble("close").toFloat()
            image.change = cur.getDouble("change").toFloat()
            image.changePercent = cur.getDouble("change_percent").toFloat()
            image.changeOverTime = cur.getDouble("change_over_time").toFloat()
            userViewModel.updateMI(image.symbol, image)
        }
    }

    fun fetchImage(date: String) {
        val call = service.getFTSE(apiKey)
        call.enqueue(ImageCallback(date))
        val call2 = service.getDJI(apiKey)
        call2.enqueue(ImageCallback(date))
        val call3 = service.getSNP(apiKey)
        call3.enqueue(ImageCallback(date))
        val call4 = service.getNASDAQ(apiKey)
        call4.enqueue(ImageCallback(date))
    }

    inner class ImageCallback(date: String) :
        Callback<ResponseBody> {
        val date = date
        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        }

        override fun onResponse(
            call: Call<ResponseBody>,
            response: Response<ResponseBody>
        ) {
            if (response.isSuccessful) {
                response.body()?.let {
                    decodeJson(it.string(), date)
                }
            }
        }
    }
}