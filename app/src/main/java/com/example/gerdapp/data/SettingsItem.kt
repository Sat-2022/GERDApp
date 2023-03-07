package com.example.gerdapp.data

import androidx.annotation.StringRes

/**********************************************
 * Data structure of card items in setting page
 * parameters:
 *  id: Int - The id of the setting item
 *  @StringRes title: Int - the resource id of the item title
 **********************************************/
data class SettingsItem(
    val id: Int,
    @StringRes val title: Int,
)
