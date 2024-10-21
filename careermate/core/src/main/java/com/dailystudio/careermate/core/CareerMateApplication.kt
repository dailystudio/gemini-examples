package com.dailystudio.careermate.core

import com.dailystudio.devbricksx.app.DevBricksApplication
import com.dailystudio.devbricksx.development.Logger
import com.facebook.stetho.Stetho
import com.google.firebase.FirebaseApp

class CareerMateApplication : DevBricksApplication() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.USE_STETHO) {
            Stetho.initializeWithDefaults(this)
        }

        Logger.info("application is running in %s mode.",
            if (BuildConfig.DEBUG) "DEBUG" else "RELEASE")

        FirebaseApp.initializeApp(this)
    }

    override fun isDebugBuild(): Boolean {
        return BuildConfig.DEBUG
    }

}