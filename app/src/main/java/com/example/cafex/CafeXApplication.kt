package com.example.cafex

import android.app.Application
import com.example.cafex.di.AppContainer
import com.example.cafex.di.AppViewModelFactory

class CafeXApplication : Application() {
    lateinit var container: AppContainer
        private set

    lateinit var viewModelFactory: AppViewModelFactory
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(applicationContext)
        viewModelFactory = AppViewModelFactory(container)
    }
}
