package com.example.co_stock

import android.app.PendingIntent.getActivity
import android.content.res.Resources
import android.provider.Settings.Global.getString
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

class APIManager(val userViewModel: UserViewModel, API_KEY: String) {
    private val apiURL = "https://financialmodelingprep.com/"
    private val apiKey = API_KEY

    val retrofit = Retrofit.Builder()
        .baseUrl(apiURL)
        .build()

    val service = retrofit.create(IndexService::class.java)

    interface IndexService {
        // Different get functions for different stock indices
        @GET("api/v3/historical-price-full/%5EFTSE?")
        fun getFTSE(
            @Query("apikey") api_key: String
        ): Call<ResponseBody>

        @GET("api/v3/historical-price-full/%5EDJI?")
        fun getDJI(
            @Query("apikey") api_key: String
        ): Call<ResponseBody>

        @GET("api/v3/historical-price-full/%5EGSPC?")
        fun getSNP(
            @Query("apikey") api_key: String
        ): Call<ResponseBody>

        @GET("api/v3/historical-price-full/%5EIXIC?")
        fun getNASDAQ(
            @Query("apikey") api_key: String
        ): Call<ResponseBody>

        // Daily standing of each index
        @GET("api/v3/quote/%5EFTSE?")
        fun getDailyFTSE(
            @Query("apikey") api_key: String
        ): Call<ResponseBody>

        @GET("api/v3/quote/%5EDJI?")
        fun getDailyDJI(
            @Query("apikey") api_key: String
        ): Call<ResponseBody>

        @GET("api/v3/quote/%5EGSPC?")
        fun getDailySNP(
            @Query("apikey") api_key: String
        ): Call<ResponseBody>

        @GET("api/v3/quote/%5EIXIC?")
        fun getDailyNASDAQ(
            @Query("apikey") api_key: String
        ): Call<ResponseBody>
    }

    fun decodeJson(json: String, date: String) {
        val image = IndexImage()
        val data = JSONObject(json)
        val hist = data.getJSONArray("historical")
        // For each past index check if the index is the best on that day and update Market Image
        for (i in 0 until hist.length()){
            val cur = hist.getJSONObject(i)
            if(cur.getString("date").substring(5) == date.substring(5)) {
                image.symbol = data.getString("symbol")
                if (data.getString("symbol") == "^GSPTSE")
                    image.symbol = "^GSPC"
                image.date = cur.getString("date")
                image.open = cur.getDouble("open").toFloat()
                image.high = cur.getDouble("high").toFloat()
                image.low = cur.getDouble("low").toFloat()
                image.close = cur.getDouble("close").toFloat()
                image.change = cur.getDouble("change").toFloat()
                image.changePercent = cur.getDouble("changePercent").toFloat()
                image.changeOverTime = cur.getDouble("changeOverTime").toFloat()
                userViewModel.updateMI(image.symbol, image)
            }
        }
    }

    fun dailyDecodeJson(json: String, date: String) {
        val image = IndexImage()
        val data = JSONArray(json)
        // For each stock index update the image to it's current daily performance
        for (i in 0 until data.length()){
            val cur = data.getJSONObject(i)
            image.symbol = cur.getString("symbol")
            image.date = date
            image.open = cur.getDouble("open").toFloat()
            image.high = cur.getDouble("dayHigh").toFloat()
            image.low = cur.getDouble("dayLow").toFloat()
            image.close = cur.getDouble("price").toFloat()
            image.change = cur.getDouble("change").toFloat()
            image.changePercent = cur.getDouble("changesPercentage").toFloat()
            image.changeOverTime = cur.getDouble("changesPercentage").toFloat()/100
            userViewModel.setDailyImage(image.symbol, image)
        }
    }

    // Fetch for the user's stock image on their date of birth
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

    // Fetch for the current standing of the stock indices
    fun fetchDailyImage(date: String) {
        val call = service.getDailyFTSE(apiKey)
        call.enqueue(DailyCallback(date))
        val call2 = service.getDailyDJI(apiKey)
        call2.enqueue(DailyCallback(date))
        val call3 = service.getDailySNP(apiKey)
        call3.enqueue(DailyCallback(date))
        val call4 = service.getDailyNASDAQ(apiKey)
        call4.enqueue(DailyCallback(date))
    }

    // A callback for the historical indices to find the stock images for user's date of birth
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

    // A callback for the current daily stock indices
    inner class DailyCallback(date: String) :
        Callback<ResponseBody> {
        val date = date
        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            throw t
        }

        override fun onResponse(
            call: Call<ResponseBody>,
            response: Response<ResponseBody>
        ) {
            if (response.isSuccessful) {
                response.body()?.let {
                    val response = it.string()
                    dailyDecodeJson(response, date)
                }
            }
        }
    }
}