package com.example.gerdapp

import android.content.Context
import android.util.Log
import okhttp3.*
import java.io.IOException

class OkHttp(val context: Context) {
    private val client = OkHttpClient().newBuilder().build()
    private lateinit var responseToData: ResponseToData
    var user: User? = null

    private var alreadyGetUser = 0

    fun getData(): String {
        val request = Request.Builder()
            .url("http://120.126.40.203/EDMTAPI/weatherforecast")
            .build()
        val call = client.newCall(request)
        var text = ""
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("API", "fail")
                text = "API connection failed"
            }

            override fun onResponse(call: Call, response: Response) {
                responseToData = ResponseToData(response)
                user = responseToData.getData()
                alreadyGetUser = 1
                intent()
                text = user!!.summary
            }
        })

        return text
    }

    fun intent() {
        if(alreadyGetUser == 1) {
            alreadyGetUser = 0
            println("Get User data")
        }
    }
}