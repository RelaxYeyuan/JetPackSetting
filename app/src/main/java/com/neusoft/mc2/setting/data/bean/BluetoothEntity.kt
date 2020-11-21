package com.neusoft.mc2.setting.data.bean

data class BluetoothEntity(
    val name: String?,
    val address: String?,
    val isConnecting: Boolean,
    var isConnected: Boolean
)