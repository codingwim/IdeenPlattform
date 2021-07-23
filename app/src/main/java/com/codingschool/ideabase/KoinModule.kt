package com.codingschool.ideabase

import android.media.Image
import androidx.room.Room
import com.ashokvarma.gander.GanderInterceptor
import com.codingschool.ideabase.model.data.room.AppDataBase
import com.codingschool.ideabase.model.remote.IdeaApi
import com.codingschool.ideabase.model.remote.ImageHandler
import com.codingschool.ideabase.ui.detail.DetailViewModel
import com.codingschool.ideabase.ui.list.IdeaListAdapter
import com.codingschool.ideabase.ui.list.ListViewModel
import com.codingschool.ideabase.ui.login.LoginViewModel
import com.codingschool.ideabase.ui.neweditidea.NewEditIdeaFragment
import com.codingschool.ideabase.ui.neweditidea.NewEditIdeaViewModel
import com.codingschool.ideabase.ui.register.RegisterViewModel
import com.codingschool.ideabase.utils.Preferences
import com.codingschool.ideabase.utils.baseUrl
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {

    single<Preferences> {
        Preferences(androidContext())
    }

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
                            getAuthFromPrefs(get())
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

    single<ImageHandler> {
        ImageHandler(androidContext(), get())
    }

    factory { provideUserApi(get()) }

    factory<IdeaListAdapter> {
        IdeaListAdapter(imageHandler = get())
    }

    factory<LoginViewModel> { parameters ->
        LoginViewModel(uNameFromArgs = parameters.get(), ideaApi = get(), prefs = get())
    }

    factory<RegisterViewModel> {
        RegisterViewModel(ideaApi = get())
    }

    factory<ListViewModel> {
        ListViewModel(adapter = get(),ideaApi = get(), prefs = get())
    }

    factory<DetailViewModel> { parameters ->
        DetailViewModel(id = parameters.get(), ideaApi = get(), prefs = get())
    }

    factory<NewEditIdeaViewModel> { parameters ->
        NewEditIdeaViewModel(newIdea = parameters.get(), ideaApi = get(), prefs = get())
    }
}


fun getAuthFromPrefs(prefs: Preferences) = prefs.getAuthString()

fun provideUserApi(retrofit: Retrofit): IdeaApi = retrofit.create(IdeaApi::class.java)





