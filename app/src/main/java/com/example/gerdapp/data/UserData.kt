package com.example.gerdapp.data

/**********************************************
 * Data structure of data of the app user
 * parameters:
 *  CaseNumber: String - The case number of the user
 *  Nickname: String - The name of the user
 *  Gender: String - The gender of the user (1 -> male; 2 -> female; 0 -> not specified)
 **********************************************/
data class UserData(
    var CaseNumber: String,
    var Nickname: String,
    var Gender: String
)