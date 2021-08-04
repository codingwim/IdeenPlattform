package com.codingschool.ideabase

import android.content.ContentResolver
import com.codingschool.ideabase.model.remote.IdeaApi
import com.codingschool.ideabase.ui.comment.CommentViewModel
import com.codingschool.ideabase.ui.detail.CommentListAdapter
import com.codingschool.ideabase.ui.detail.DetailViewModel
import com.codingschool.ideabase.ui.editprofile.EditProfileViewModel
import com.codingschool.ideabase.ui.list.IdeaListAdapter
import com.codingschool.ideabase.ui.list.ListViewModel
import com.codingschool.ideabase.ui.loading.LoadingViewModel
import com.codingschool.ideabase.ui.login.LoginViewModel
import com.codingschool.ideabase.ui.neweditidea.NewEditIdeaViewModel
import com.codingschool.ideabase.ui.profile.ProfileViewModel
import com.codingschool.ideabase.ui.register.RegisterViewModel
import com.codingschool.ideabase.utils.ImageHandler
import com.codingschool.ideabase.utils.Preferences
import com.codingschool.ideabase.utils.baseUrl
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {

    single {
        Preferences(androidContext())
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

    single {
        ImageHandler(androidContext(), get())
    }

    single<ContentResolver> {
        androidApplication().contentResolver
    }

    factory { provideUserApi(get()) }

    factory {
        IdeaListAdapter(imageHandler = get())
    }

    factory {
        CommentListAdapter(imageHandler = get())
    }

    factory { parameters ->
        LoadingViewModel(online = parameters.get(), ideaApi = get(), prefs = get())
    }

    factory { parameters ->
        LoginViewModel(uNameFromArgs = parameters.get(), ideaApi = get(), prefs = get())
    }

    factory {
        RegisterViewModel(ideaApi = get())
    }

    factory { parameters ->
        ListViewModel(topOrAll = parameters.get(), adapter = get(), ideaApi = get(), prefs = get())
    }

    factory { parameters ->
        DetailViewModel(id = parameters.get(), adapter = get(), ideaApi = get(), prefs = get())
    }

    factory { parameters ->
        NewEditIdeaViewModel(
            editIdeaId = parameters.get(),
            ideaApi = get(),
            prefs = get(),
            contentResolver = get()
        )
    }

    factory { parameters ->
        CommentViewModel(id = parameters.get(), ideaApi = get(), prefs = get())
    }

    factory { parameters ->
        ProfileViewModel(id = parameters.get(), ideaApi = get(), prefs = get())
    }
    factory { parameters ->
        EditProfileViewModel(
            loadPictureLoader = parameters.get(),
            ideaApi = get(),
            prefs = get(),
            contentResolver = get()
        )
    }
}

fun getAuthFromPrefs(prefs: Preferences) = prefs.getAuthString()

fun provideUserApi(retrofit: Retrofit): IdeaApi = retrofit.create(IdeaApi::class.java)





