package com.smsecure.browser.app

import android.app.Application
import com.smsecure.browser.BuildConfig
import com.smsecure.browser.browser.GeckoRuntimeManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class SMSecureApplication : Application() {
    private val applicationScope = CoroutineScope(Dispatchers.IO)

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

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                Timber.e(throwable, "Fatal uncaught exception on thread %s", thread.name)
            } catch (ignored: Exception) {
            }
        }

        // Pre-initialize GeckoRuntime on background thread to prevent UI thread ANR / crash on startup
        applicationScope.launch {
            try {
                GeckoRuntimeManager.getRuntime(applicationContext)
            } catch (e: Exception) {
                Timber.e(e, "Background GeckoRuntime pre-initialization failed")
            }
        }
    }
}
