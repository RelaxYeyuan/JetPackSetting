package com.neusoft.mc2.setting.utils

import android.app.ActivityManager
import android.content.Context
import android.text.TextUtils

object Utils {

    /**
     * 判断某个Activity 界面是否在前台
     */
    fun isForeground(context: Context?, className: String): Boolean {
        if (context == null || TextUtils.isEmpty(className)) {
            return false
        }
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val list =
            am.getRunningTasks(1)
        if (list != null && list.size > 0) {
            val cpn = list[0].topActivity
            return className == cpn.className
        }
        return false
    }

    fun checkWifiNameQuotes(name: String): String {
        return if (name.isNotEmpty()) {
            name.substring(1, name.length - 1)
        } else name
    }
}