package com.codingschool.ideabase

import android.content.Context
import androidx.room.Room
import com.ashokvarma.gander.GanderInterceptor
import com.codingschool.ideabase.model.data.room.AppDataBase
import com.codingschool.ideabase.model.remote.IdeaApi
import com.codingschool.ideabase.ui.login.LoginViewModel
import com.codingschool.ideabase.ui.register.RegisterViewModel
import com.codingschool.ideabase.utils.Preferences
import com.codingschool.ideabase.utils.baseUrl
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.inject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {

    single<AppDataBase> {
        (
                Room
                    .databaseBuilder(
                        androidContext(),
                        AppDataBase::class.java,
                        "app-db"
                    )
                    // REMOVE ON PRODUCTION VERSION
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
                )
    }
    single {
        OkHttpClient.Builder()
            .addNetworkInterceptor { chain ->
                chain.proceed(
                    chain.request().newBuilder()
                        .header(
                            "Authorization",
                            getAuthFromPrefs(androidContext())
                        )
                        .build()
                )
            }
            .addInterceptor(
                GanderInterceptor(androidApplication())
                    .showNotification(true)
            )
            .build()
    }

    single {
        Retrofit.Builder()
            .client(get())
            .baseUrl(baseUrl)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<Preferences> {
        Preferences(androidContext())
    }

    factory { provideUserApi(get()) }

    factory<LoginViewModel> { parameters ->
        LoginViewModel(uNameFromArgs = parameters.get(), get(), get())
    }

    factory<RegisterViewModel> {
        RegisterViewModel(get())
    }
}

fun getAuthFromPrefs(context: Context): String {
    val prefs = Preferences(context)
    return prefs.getAuthString()
}

fun provideUserApi(retrofit: Retrofit): IdeaApi = retrofit.create(IdeaApi::class.java)

