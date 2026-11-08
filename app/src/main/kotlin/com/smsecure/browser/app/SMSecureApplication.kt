package com.smsecure.browser.app

import android.app.Application
import com.smsecure.browser.BuildConfig
import timber.log.Timber

class SMSecureApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(object : Timber.Tree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    // Release tree: zero logging to prevent URL/user data leakage
                }
            })
        }
    }
}
