package com.neusoft.mc2.setting.data.bean

data class WifiEntity(
    val name: String,
    val status: String,
    val isConnecting: Boolean,
    val isConnected: Boolean
)