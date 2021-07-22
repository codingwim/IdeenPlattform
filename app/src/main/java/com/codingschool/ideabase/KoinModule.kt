package com.codingschool.ideabase

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit

val appModule = module {

  /*  single<AppDatabase> { (
        Room
            .databaseBuilder(
                androidContext(),
                AppDatabase::class.java,
                "app-db"
            )
            // REMOVE ON PRODUCTION VERSION
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
            )
    }*/

   /* single {
        Retrofit.Builder()
            .baseUrl( baseUrl)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .addConverterFactory(GsonConverterFactory.create())
            .build()


    }*/
/*
    factory { provideUserApi(get()) }*/



}

/*fun provideUserApi(retrofit: Retrofit): UserApi = retrofit.create(UserApi::class.java)*/

