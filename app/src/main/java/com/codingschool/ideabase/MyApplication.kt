package com.codingschool.ideabase

import android.app.Application
import com.ashokvarma.gander.Gander
import com.ashokvarma.gander.imdb.GanderIMDB
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MyApplication : Application() {

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()

        startKoin() {
            androidLogger(Level.DEBUG)
            androidContext(this@MyApplication)
            modules(appModule)
        }

        Gander.setGanderStorage(GanderIMDB.getInstance())
    }
        companion object {
            private var instance: MyApplication? = null
        }
}