package com.codingschool.ideabase

import androidx.room.Room
import com.codingschool.ideabase.utils.baseUrl
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

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
    single {
        OkHttpClient.Builder()
            .addNetworkInterceptor { chain ->
                chain.proceed(
                    chain.request().newBuilder()
                        .header(
                            "Authorization",
                            "Basic d2ltQG1haWwuY29tOndpbTEyMzQ1Njc4"
                        )
                        .build()
                )
            }
            .build()
    }

    single {
        Retrofit.Builder()
            .client(OkHttpClient())
            .baseUrl(baseUrl)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
/*
    factory { provideUserApi(get()) }*/


}

/*fun provideUserApi(retrofit: Retrofit): UserApi = retrofit.create(UserApi::class.java)*/

