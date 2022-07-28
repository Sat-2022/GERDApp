package com.example.gerdapp

import android.graphics.BitmapFactory
import okhttp3.Response
import org.json.JSONObject
import java.net.URL

class ResponseToData(val response: Response?) {
    fun getData(): User {
        val responseStr = response!!.body!!.string()
        val itemList = JSONObject(responseStr)
        val name = if("${itemList.get("date")}"=="null") "" else "${itemList.get("date")}"
        val bitmap = BitmapFactory.decodeStream(URL("${itemList.get("avatar_url")}").openConnection().getInputStream())
        val user = User(
                name,
                itemList.getInt("temperatureC"),
                itemList.getInt("temperatureF"),
                "${itemList.get("followers")}",
        )
        return user
    }
}