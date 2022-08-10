package com.example.gerdapp

object UserData {
    var userId: String? = null
    var userNo: String? = null
    var userName: String? = null
    var gender: String? = null
}

fun setUserData(userId: String, userNo: String, userName: String, gender: String) {
    UserData.userId = userId
    UserData.userNo = userNo
    UserData.userName = userName
    UserData.gender = gender
}

object Notification {
    var notificationOn: Boolean = true
}