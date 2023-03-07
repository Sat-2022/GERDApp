package com.example.gerdapp.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**********************************************
 * Data structure of card items in main page
 * parameters:
 *  stringResourceId: Int - The resource id of the string to show
 *  imageResourceId: Int - The resource id of the image to show
 *  imageColor: Int - The color of the image
 **********************************************/
data class CardItem(
    @StringRes val stringResourceId: Int,
    @DrawableRes val imageResourceId: Int,
    val imageColor: Int
)