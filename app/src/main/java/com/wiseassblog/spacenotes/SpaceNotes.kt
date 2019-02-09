package com.wiseassblog.spacenotes

import android.app.Application
import com.squareup.leakcanary.LeakCanary


class SpaceNotes: Application() {



    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)
    }
}