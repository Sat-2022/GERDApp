package com.example.gerdapp

data class UserData(
    var userId: String,
    var CaseNumber: String,
    var userName: String,
    var Gender: String
)

object Notification {
    var notificationOn: Boolean = true
}