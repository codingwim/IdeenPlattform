package com.codingschool.ideabase.ui.neweditidea

import android.app.Application
import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.ObservableInt
import androidx.databinding.library.baseAdapters.BR
import com.codingschool.ideabase.R
import com.codingschool.ideabase.model.data.CreateIdea
import com.codingschool.ideabase.model.remote.IdeaApi
import com.codingschool.ideabase.utils.Preferences
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody

class NewEditIdeaViewModel(
    private val editIdea: String,
    private val ideaApi: IdeaApi,
    private val prefs: Preferences
): BaseObservable() {

    private var view: NewEditIdeaView? = null

    private val compositeDisposable = CompositeDisposable()

    private var categoryList = emptyList<String>()

    fun attachView(view: NewEditIdeaView) {
        this.view = view
    }
    fun init() {
        // todo add progress indicator loading categories / prefill and wait (completable?) disable update button till loaded
        getAndSetCategoryItems()
        ideaCategory = "event"
        // set edittexts with hint or prefill depending on editIdea (id) or editIdea("") (default)
        if (editIdea.isNotEmpty()) {
            getIdeaAndPrefill()
        }
    }

    private fun getAndSetCategoryItems() {
        ideaApi.getAllCategories()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ list ->
                // TODO add locale check to take right value
                categoryList = list.map { category ->
                    category.name_en
                }
                view?.setCategoryListItems(categoryList)
            }, { t ->
                val responseMessage = t.message
                if (responseMessage != null) {
                    if (responseMessage.contains(
                            "HTTP 401",
                            ignoreCase = true
                        )
                    ) {
                        Log.d("observer_ex", "401 Authorization not valid")
                        view?.showToast("You are not autorized to log in")
                    } else view?.showToast(R.string.network_issue_check_network)
                }
                Log.e("observer_ex", "exception getting categories: $t")
            }).addTo(compositeDisposable)
    }

    private fun getIdeaAndPrefill() {
        saveButtonText.set(R.string.save_idea_edit)
        uploadImageButtonText.set(R.string.change_image_idea_edit)
        view?.setActionBarTitle("Edit idea")

        ideaApi.getIdeaById(editIdea)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ idea ->
                // set idea name in title
                view?.setActionBarTitle(idea.title)
                // now set all the bindable details, including image
                view?.setIdeaImage(idea.imageUrl)
                ideaName = idea.title
                // TODO locale check to get correct language category
                ideaCategory = idea.category.name_en
                /*view?.setSelectedCategory(ideaCategory)*/
                ideaDescritpion = idea.description
                notifyPropertyChanged(BR.ideaName)
                notifyPropertyChanged(BR.ideaCategory)
                notifyPropertyChanged(BR.ideaDescritpion)

            }, { t ->
                val responseMessage = t.message
                if (responseMessage != null) {
                    if (responseMessage.contains(
                            "HTTP 401",
                            ignoreCase = true
                        )
                    ) {
                        Log.d("observer_ex", "401 Authorization not valid")
                        view?.showToast("You are not autorized to log in")
                    } else if (responseMessage.contains(
                            "HTTP 404",
                            ignoreCase = true
                        )
                    ) {
                        Log.d("observer_ex", "404 Idea not found")
                        view?.showToast("Idea not found")
                    } else view?.showToast(R.string.network_issue_check_network)
                }
                Log.e("observer_ex", "exception getting idea: $t")
            }).addTo(compositeDisposable)

    }

    var ideaImageUrl: String = "dummy"

    @get:Bindable
    var ideaName: String = ""

    @get:Bindable
    var ideaCategory: String = ""

    @get:Bindable
    var ideaDescritpion: String = ""

    @get: Bindable
    val saveButtonText = ObservableInt(R.string.save_idea_new)

    @get: Bindable
    val uploadImageButtonText = ObservableInt(R.string.upload_image_idea_new)



    fun onSaveClick() {
        // check empty fields
        var fieldsNotEmpty = false

        if (ideaName.isEmpty()) view?.setInputNameError("Please enter a name for your idea")
        else if (ideaDescritpion.isEmpty()) view?.setInputDescriptionError("Please enter a description for your idea")
        else if (ideaCategory.isEmpty()) view?.setInputCategoryError("Please choose a categroy for your idea")
        else if (ideaImageUrl.isEmpty()) view?.showToast("Please select and upload an image for your idea")
        else fieldsNotEmpty = true

        if (fieldsNotEmpty) {
            val createIdea = CreateIdea(
                ideaName,
                ideaCategory,
                ideaDescritpion
            )
            var gson = Gson()
            val ideaString = gson.toJson(createIdea)

            // build the multi form to send the api call
             val requestBody = MultipartBody.Builder()
                 .setType(MultipartBody.FORM)
                 .addFormDataPart(
                     "body",
                     null,
                     RequestBody.create("application/json".toMediaTypeOrNull(), ideaString )
                 )
                 .addFormDataPart(
                     "image",
                     "idea_image.jpeg",
                     context.assets.open("dummyImage.jpeg").readBytes()
                         .toRequestBody("image/jpg".toMediaTypeOrNull())
                     )
                 .build()
            try {
                ideaApi.addIdea(requestBody)

            } catch (e: Exception){
                Log.d("observer_ex", "uploading idea failed exception: ${e.message}")
                view?.showToast("Idea upload failed")
                // add response with Response, not single, so try catch??
            }

        }
    }

    fun onUploadImageClick() {

    }

    fun onIdeaNameTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (count < 2) view?.resetEmptyIdeaName()
    }


    fun onDescriptionTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (count < 2) view?.resetEmptyDescription()
    }
}