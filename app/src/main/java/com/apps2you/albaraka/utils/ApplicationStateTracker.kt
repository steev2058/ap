package com.apps2you.albaraka.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle

class ApplicationStateTracker(
    private val applicationStateCallback: ApplicationStateCallback
) : Application.ActivityLifecycleCallbacks {

    private var numStarted = 0

    override fun onActivityStarted(activity: Activity) {
        if (numStarted == 0) {
            applicationStateCallback.applicationWentToForeground()
        }
        numStarted++
    }

    override fun onActivityStopped(activity: Activity) {
        numStarted--
        if (numStarted == 0) {
            applicationStateCallback.applicationWentToBackground()
        }
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityDestroyed(p0: Activity) {
    }

    override fun onActivityResumed(p0: Activity) {
    }

    override fun onActivityPaused(p0: Activity) {
    }

    interface ApplicationStateCallback {
        fun applicationWentToForeground()
        fun applicationWentToBackground()
    }
}