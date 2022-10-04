package com.example.gerdapp.data

import com.example.gerdapp.R

class SettingDataSource {
    fun loadSettings(): List<SettingsItem> {
        return listOf(
            SettingsItem(1, R.string.text_size_title, R.string.text_size_large),
            SettingsItem(2, R.string.version_title, R.string.version_number)
        )
    }
}