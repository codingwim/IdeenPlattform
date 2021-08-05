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
import com.codingschool.ideabase.model.remote.InputStreamRequestBody
import com.codingschool.ideabase.utils.Preferences
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class NewEditIdeaViewModel(
    private val editIdeaId: String,
    private val ideaApi: IdeaApi,
    private val prefs: Preferences,
    private val contentResolver: ContentResolver
) : BaseObservable() {

    private var view: NewEditIdeaView? = null

    val compositeDisposable = CompositeDisposable()

    private var categoryListDE = emptyList<String>()
    private var categoryListEN = emptyList<String>()
    private var categoryListIds = emptyList<String>()

    fun attachView(view: NewEditIdeaView) {
        this.view = view
    }

    fun init() {
        // optionally: add progress indicator loading categories / prefill and wait (completable?) disable update button till loaded
        getAndSetCategoryItems()
        // set edittexts with hint or prefill depending on NEW editIdea("") or EDIT editIdea(id)
        if (editIdeaId.isNotEmpty()) {
            getIdeaAndPrefill()
        } else if (prefs.hasSavedIdeaDraft()) getPrefillDraft()
    }

    private fun getAndSetCategoryItems() {
        ideaApi.getAllCategories()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ list ->
                // locale check to take right value
                categoryListEN = list.map { category ->
                    category.name_en
                }
                categoryListDE = list.map { category ->
                    category.name_de
                }
                categoryListIds = list.map { category ->
                    category.id
                }
                view?.setCategoryListItems(if (prefs.isLangEn()) categoryListEN else categoryListDE)
            }, { t ->
                view?.handleErrorResponse(t.message)
                Log.e("IdeaBase_log", "exception getting categories: $t")
            }).addTo(compositeDisposable)
    }

    private fun getPrefillDraft() {
        ideaImageUrl = prefs.getImageDraft()
        ideaImageUri = ideaImageUrl.toUri()
        ideaName = prefs.getIdeaNameDraft()
        ideaDescription = prefs.getIdeaDescriptionDraft()
        ideaCategory = prefs.getIdeaCategoryDraft()
        view?.setIdeaImage(ideaImageUrl)
        view?.setSelectedCategory(ideaCategory)
        uploadImageButtonText.set(R.string.change_image_idea_edit)
        notifyPropertyChanged(BR.ideaName)
        notifyPropertyChanged(BR.ideaCategory)
        notifyPropertyChanged(BR.ideaDescription)
    }

    private fun getIdeaAndPrefill() {
        saveButtonText.set(R.string.save_idea_edit)
        uploadImageButtonText.set(R.string.change_image_idea_edit)
        view?.setActionBarTitleEdit()

        ideaApi.getIdeaById(editIdeaId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ idea ->
                // set all the bindable details, including image
                // initialImageUrl will stay the same, even if a new image was loaded
                initialImageUrl = idea.imageUrl
                //ideaImageUrl will change if we choose a new image
                ideaImageUrl = initialImageUrl
                view?.setIdeaImage(ideaImageUrl)
                ideaName = idea.title
                // locale check to get correct language category
                ideaCategory =
                    if (prefs.isLangEn()) idea.category.name_en else idea.category.name_de
                view?.setSelectedCategory(ideaCategory)
                ideaDescription = idea.description
                notifyPropertyChanged(BR.ideaName)
                notifyPropertyChanged(BR.ideaCategory)
                notifyPropertyChanged(BR.ideaDescription)

            }, { t ->
                view?.handleErrorResponse(t.message)
                Log.e("IdeaBase_log", "exception getting idea: $t")
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
    var ideaDescription: String = ""

    @get: Bindable
    val saveButtonText = ObservableInt(R.string.save_idea_new)

    @get: Bindable
    val uploadImageButtonText = ObservableInt(R.string.upload_image_idea_new)

    fun onSaveClick() {

        var fieldsNotEmptyOrTooLong = false

        if (!(ideaName.length > 20) && !(ideaDescription.length > 1000)) {
            when {
                ideaName.isEmpty() -> view?.setInputNameError(R.string.error_empty_name)
                ideaDescription.isEmpty() -> view?.setInputDescriptionError(R.string.error_empty_description)
                ideaCategory.isEmpty() -> view?.setInputCategoryError(R.string.error_empty_category)
                ideaImageUrl.isEmpty() -> view?.showToast(R.string.error_empty_image)
                else -> fieldsNotEmptyOrTooLong = true
            }
        }

        if (fieldsNotEmptyOrTooLong) {

            val categoryId =
                categoryListIds[if (prefs.isLangEn()) categoryListEN.indexOf(ideaCategory) else categoryListDE.indexOf(
                    ideaCategory
                )]
            val createIdea = CreateIdea(
                ideaName,
                categoryId,
                ideaDescription
            )
            val gson = Gson()
            val ideaString = gson.toJson(createIdea)
            val imagePart =
                InputStreamRequestBody("image/*".toMediaType(), contentResolver, ideaImageUri)

            // UPDATE OR NEW ?
            if (editIdeaId.isNotEmpty()) {
                updateIdea(createIdea, imagePart)
            } else {
                // NEW idea
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
                        view?.infoDialog(idea.id)
                        prefs.clearIdeaDraft()
                    }, { t ->
                        view?.handleErrorResponse(t.message)
                        Log.e("IdeaBase_log", "exception adding idea: $t")
                    }).addTo(compositeDisposable)
            }
        }
    }

    private fun updateIdea(createIdea: CreateIdea, imagePart: InputStreamRequestBody) {
        val updateImage = !ideaImageUrl.equals(initialImageUrl, false)
        ideaApi.updateIdea(editIdeaId, createIdea)
            .doOnError { t ->
                view?.handleErrorResponse(t.message)
            }
            .andThen(
                updateImage(imagePart, updateImage)
            )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.showToast(R.string.idea_updated_successflly)
                view?.navigateBack()
            }, { t ->
                view?.handleErrorResponse(t.message)
                Log.e("IdeaBase_log", "exception updating idea: $t")
                view?.navigateBack()
            }).addTo(compositeDisposable)
    }

    private fun updateImage(imagePart: InputStreamRequestBody, updateImage: Boolean): Completable {
        return if (updateImage) {
            val requestBodyForUpdatedImage = getRequestBodyForUpdatedImage(imagePart)
            ideaApi.updateImageIdea(editIdeaId, requestBodyForUpdatedImage)
        } else Completable.complete()
    }

    fun onGetImageClick() {
        view?.getImageDialog()
    }

    fun onIdeaNameTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if ((count > 0) && (before == 0)) view?.resetEmptyIdeaName()
    }

    fun onDescriptionTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if ((count > 0) && (before == 0)) view?.resetEmptyDescription()
    }

    fun setSelectedImage(uri: Uri) {
        ideaImageUri = uri
        ideaImageUrl = uri.toString()
        view?.setIdeaImage(ideaImageUrl)
        uploadImageButtonText.set(R.string.change_image_idea_edit)
    }

    private fun getRequestBodyForUpdatedImage(imagePart: InputStreamRequestBody) =
        MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "image",
                "idea_image.jpeg",
                imagePart
            )
            .build()

    fun onBackPressed() {
        if ((editIdeaId.isEmpty()) &&
            (ideaCategory.isNotEmpty() or
                    ideaDescription.isNotEmpty() or
                    ideaImageUrl.isNotEmpty() or
                    ideaCategory.isNotEmpty() or
                    ideaName.isNotEmpty()
                    )
        ) view?.cancelDialog() else view?.navigateBack()
    }

    fun onCancelWithoutDraft() {
        prefs.clearIdeaDraft()
        view?.showToast(R.string.adding_cancelled_without_draft)
        view?.navigateBack()
    }

    fun onCancelWithDraft() {
        prefs.setImageDraft(ideaImageUrl)
        prefs.setIdeaNameDraft(ideaName)
        prefs.setIdeaCategoryDraft(ideaCategory)
        prefs.setIdeaDescriptionDraft(ideaDescription)
        prefs.setIdeaDraftSaved(true)
        view?.showToast(R.string.idea_saved)
        view?.navigateBack()
    }
}