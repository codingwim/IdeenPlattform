package com.codingschool.ideabase.ui.neweditidea

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.ObservableInt
import androidx.databinding.library.baseAdapters.BR
import com.codingschool.ideabase.R
import com.codingschool.ideabase.model.data.CreateIdea
import com.codingschool.ideabase.model.remote.IdeaApi
import com.codingschool.ideabase.utils.InputStreamRequestBody
import com.codingschool.ideabase.utils.Preferences
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class NewEditIdeaViewModel(
    private val editIdea: String,
    private val ideaApi: IdeaApi,
    private val contentresolver: ContentResolver,
    private val prefs: Preferences
) : BaseObservable() {

    private var view: NewEditIdeaView? = null

    private val compositeDisposable = CompositeDisposable()

    private var categoryList = emptyList<String>()
    private var categoryListIds = emptyList<String>()


    fun attachView(view: NewEditIdeaView) {
        this.view = view
    }

    fun init() {
        // todo add progress indicator loading categories / prefill and wait (completable?) disable update button till loaded
        getAndSetCategoryItems()
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
                categoryListIds = list.map { category ->
                    category.id
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
                // now set all the bindable details, including image
                //initialIamgeUrl will stay the same, even if a new image was loaded
                initialImageUrl = idea.imageUrl
                //ideaImageUrl will change if we choose a new image
                ideaImageUrl = initialImageUrl
                view?.setIdeaImage(ideaImageUrl)
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

    private var initialImageUrl: String = ""
    private var ideaImageUri: Uri = "".toUri()
    private var ideaImageUrl: String = ""

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
        else if (ideaCategory.isEmpty()) view?.setInputCategoryError("Please choose a category for your idea")
        else if (ideaImageUrl.isEmpty()) view?.showToast("Please select and upload an image for your idea")
        else fieldsNotEmpty = true

        if (fieldsNotEmpty) {
            val categoryId = categoryListIds[categoryList.indexOf(ideaCategory)]
            val createIdea = CreateIdea(
                ideaName,
                categoryId,
                ideaDescritpion
            )
            val gson = Gson()
            val ideaString = gson.toJson(createIdea)

            // build the multi form to send the api call
            val imagePart =
                InputStreamRequestBody("image/*".toMediaType(), contentresolver, ideaImageUri)

            // UPDATE OR NEW ?
            if (editIdea.isNotEmpty()) {
                updateIdea(createIdea, imagePart)
            } else {

                val requestBodyForNewIdea = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "body",
                        null,
                        ideaString.toRequestBody("application/json".toMediaTypeOrNull())
                    )
                    .addFormDataPart(
                        "image",
                        "idea_image.jpeg",
                        imagePart
                    )
                    .build()

                ideaApi.addIdea(requestBodyForNewIdea)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ idea ->
                        view?.showToast("Idea successfully added")
                        view?.navigateToAllIdeas()
                    }, { t ->
                        val responseMessage = t.message
                        if (responseMessage != null) {
                            if (responseMessage.contains(
                                    "HTTP 400",
                                    ignoreCase = true
                                )
                            ) {
                                view?.showToast("Parameter missing")
                                Log.d("observer_ex", "Parameter missing: $t")
                            } else view?.showToast(R.string.network_issue_check_network)
                        }
                        Log.e("observer_ex", "exception adding idea: $t")
                    }).addTo(compositeDisposable)
            }
            /*try {
                val call = ideaApi.addIdea(requestBody)

            } catch (e: Exception){
                Log.d("observer_ex", "uploading idea failed exception: ${e.message}")
                view?.showToast("Idea upload failed")
                // add response with Response, not single, so try catch??
            }*/

        }
    }

    private fun updateIdea(createIdea: CreateIdea, imagePart: InputStreamRequestBody) {

        val updateImage = !ideaImageUrl.equals(initialImageUrl, false)
        ideaApi.updateIdea(editIdea, createIdea)
            // TODO this is the wrong way. the updatemage part will run even if the ideaupdate failed. There is no feedback either
            /*.subscribeOn(Schedulers.io())*/
            .onErrorComplete {
                it is HttpException
            }
            .andThen (
                updateImage(imagePart, updateImage)
                    /*ideaApi.updateImageIdea(editIdea, getRequestBodyForUpdatedImage(imagePart))*/
            )
            .observeOn(AndroidSchedulers.mainThread())
            /*.andThen {
                view?.showToast("done")
                Log.d("observer_ex", "Idea updated andthen")
            }*/
            .subscribe({
                view?.showToast("Idea updated successfully")
                Log.d("observer_ex", "Idea updated successfully")
                view?.navigateBack()
            }, { t ->
                val responseMessage = t.message
                if (responseMessage != null) {
                    if (responseMessage.contains(
                            "HTTP 400",
                            ignoreCase = true
                        )
                    ) {
                        view?.showToast("Parameter missing")
                        Log.d("observer_ex", "Parameter missing: $t")
                    } else if (responseMessage.contains(
                            "HTTP 401",
                            ignoreCase = true
                        )
                    ) {
                        view?.showToast("You are not authorized")
                        Log.d("observer_ex", "Not authorized: $t")
                    } else if (responseMessage.contains(
                            "HTTP 403",
                            ignoreCase = true
                        )
                    ) {
                        view?.showToast("The idea has been released by an admin and can't be edited anymore.")
                        Log.d(
                            "observer_ex", "\t\n" +
                                    "Not the author of the idea, or idea already released.: $t"
                        )
                    } else if (responseMessage.contains(
                            "HTTP 404",
                            ignoreCase = true
                        )
                    ) {
                        view?.showToast("Something went wrong, the idea was not found")
                        Log.d("observer_ex", "Idea was not found: $t")
                    } else view?.showToast(R.string.network_issue_check_network)
                }
                Log.e("observer_ex", "exception updating idea: $t")
            }).addTo(compositeDisposable)
    }

    private fun updateImage(imagePart: InputStreamRequestBody, updateImage: Boolean): Completable {
        // TODO make sure to add progress indicator for image upload
        if (updateImage) {
            val requestBodyForUpdatedImage = getRequestBodyForUpdatedImage(imagePart)
            return ideaApi.updateImageIdea(editIdea, requestBodyForUpdatedImage)
        } else return Completable.complete()
    }


    fun onGetImageClick() {
        view?.getImageDialog()
    }

    fun onIdeaNameTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (count < 2) view?.resetEmptyIdeaName()
    }

    fun onDescriptionTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (count < 2) view?.resetEmptyDescription()
    }

    fun setSelectedImage(uri: Uri) {
        ideaImageUri = uri
        ideaImageUrl = uri.toString()
        view?.setIdeaImage(ideaImageUrl)
        uploadImageButtonText.set(R.string.change_image_idea_edit)
        Log.d("observer_ex", "selected Image uri: $uri as string: $ideaImageUrl")
    }

    fun getRequestBodyForUpdatedImage(imagePart: InputStreamRequestBody) =
        MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "image",
                "idea_image.jpeg",
                imagePart
            )
            .build()
}