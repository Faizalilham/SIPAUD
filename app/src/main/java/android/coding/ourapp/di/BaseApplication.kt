package android.coding.ourapp.di

import ActivityLifecycleObserver
import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication: Application(){

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        registerActivityLifecycleCallbacks(ActivityLifecycleObserver(this))
    }
}