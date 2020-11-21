package com.neusoft.mc2.setting.base

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import kotlin.properties.Delegates

class BaseApplication : Application(), ViewModelStoreOwner {

    private val TAG = "BaseApplication"
    private val activityList = ArrayList<Activity>()

    companion object {
        var instance: BaseApplication by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        this.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityDestroyed(activity: Activity) {
                Log.d(TAG, "onActivityDestroyed: $activity")
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                Log.d(TAG, "onActivityCreated: $activity")
                if (!activityList.contains(activity)) {
                    activityList.add(activity)
                }
            }

            override fun onActivityResumed(activity: Activity) {
            }

        })
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    private val mAppViewModelStore: ViewModelStore by lazy {
        ViewModelStore()
    }

    override fun getViewModelStore(): ViewModelStore = mAppViewModelStore

    fun removeAllActivity() {
        for (activity in activityList) {
            activity.finish()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d(TAG, "onConfigurationChanged: ")
        removeAllActivity()
    }
}
