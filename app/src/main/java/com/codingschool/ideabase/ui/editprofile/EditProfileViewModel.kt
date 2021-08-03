package com.codingschool.ideabase.ui.editprofile

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.codingschool.ideabase.R
import com.codingschool.ideabase.model.data.UpdateUser
import com.codingschool.ideabase.model.remote.IdeaApi
import com.codingschool.ideabase.model.remote.InputStreamRequestBody
import com.codingschool.ideabase.utils.Preferences
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import retrofit2.HttpException
import java.util.regex.Pattern

class EditProfileViewModel(
    private val loadPictureLoader: Boolean,
    private val ideaApi: IdeaApi,
    private val prefs: Preferences,
    private val contentResolver: ContentResolver
) : BaseObservable() {


    private var view: EditProfileView? = null
    val compositeDisposable = CompositeDisposable()

    fun init() {
        if (prefs.getAuthString().isNotEmpty()) {
            setMyCredentialsFromAPI()
            if (loadPictureLoader) onGetProfileImageClick()
        } else {
            view?.showToast("You are not authorized to edit this profile")
            //view?.navigateBack()
        }
    }

    fun attachView(view: EditProfileView) {
        this.view = view
    }

    private var initialProfileImageUrl: String = ""
    private var profileImageUri: Uri = "".toUri()
    private var profileImageUrl: String = ""

    var email: String =""

    @get:Bindable
    var firstname: String = ""

    @get:Bindable
    var lastname: String = ""

    @get:Bindable
    var password: String = ""

    @get:Bindable
    var password2: String = ""

    fun onGetProfileImageClick() {
        view?.getProfileImageDialog()
    }

    fun setSelectedProfileImage(uri: Uri) {
        profileImageUri = uri
        profileImageUrl = uri.toString()
        view?.setProfilePicture(profileImageUrl)
        view?.showOverlayChangePicture()

        updateProfilePicture()
    }

    private fun updateProfilePicture() {
        val imagePart =
            InputStreamRequestBody("image/*".toMediaType(), contentResolver, profileImageUri)
        val requestBodyForUpdatedImage = getRequestBodyForUpdatedImage(imagePart)
        ideaApi.updateMyProfilePicture(requestBodyForUpdatedImage)
            .onErrorComplete {
                it is HttpException
            }
            .andThen {
                initialProfileImageUrl = profileImageUrl
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("observer_ex", "Profile image updated")
                //view?.navigateBack()
            }, { t ->
                val responseMessage = t.message
                if (responseMessage != null) {
                    if (responseMessage.contains(
                            "HTTP 400",
                            ignoreCase = true
                        )
                    ) {
                        view?.showToast(R.string.parameter_missing_message)
                        Log.d("observer_ex", "Parameter missing: $t")
                    } else if (responseMessage.contains(
                            "HTTP 401",
                            ignoreCase = true
                        )
                    ) {
                        view?.showToast(R.string.not_authorized)
                        Log.d("observer_ex", "Not authorized: $t")
                    } else view?.showToast(R.string.network_issue_check_network)
                }
                Log.e("observer_ex", "exception updating idea: $t")
            }).addTo(compositeDisposable)



    }

    fun onSaveClick() {
        // check empty fields
        var fieldsNotEmpty = false

        when {
            firstname.isEmpty() -> view?.setInputFirstnameError(R.string.enter_first_name_error)
            lastname.isEmpty() -> view?.setInputLastnameError(R.string.error_enter_last_name)
            password.isEmpty() -> view?.setInputPasswordError(R.string.pwd_min_error)
            password2.isEmpty() -> view?.setInputPasswordRepeatError(R.string.pwd_same_error)
            else -> fieldsNotEmpty = true
        }

        // if fields not empty, check validity
        var validCredentialsToRegister = false
        if (fieldsNotEmpty) {
            // check valid e-mail

            if (!(password.equals(password2))) {
                view?.setInputPasswordRepeatError(R.string.pwd_are_ot_same_error)
            } else
            // check pwd valid
                if (!validPassword(password)) {
                    password = ""
                    password2 = ""
                    notifyPropertyChanged(BR.password)
                    notifyPropertyChanged(BR.password2)
                    view?.setFocusPasswordInput()
                    view?.setInputPasswordError("Password must contain a minimum of 8 characters, including at least 1 number/1 letter and a special char")
                } else {
                    validCredentialsToRegister = true
                }
        }

        // if credentials valid, register User via API call
        if (validCredentialsToRegister) {
            val updatedUser = UpdateUser(
                email,
                password,
                firstname,
                lastname,
            )
            ideaApi.updateUser(updatedUser)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("observer_ex", "user updated over API")
                    view?.showToast(R.string.please_login_again)
                    view?.navigateToLoginRegistered(email)
                }, { t ->
                    val responseMessage = t.message
                    if (responseMessage != null) {
                        if (responseMessage.contains(
                                "HTTP 401",
                                ignoreCase = true
                            )
                        ) view?.showToast("You are not authorized to log in")
                        else if (responseMessage.contains(
                                "HTTP 400",
                                ignoreCase = true
                            )
                        ) view?.showToast(R.string.parameter_missing_message)
                        else view?.showToast(R.string.network_issue_check_network)
                    }
                    Log.e("observer_ex", "exception updating user: $t")
                }).addTo(compositeDisposable)
        }

    }
    private fun setMyCredentialsFromAPI() {
        ideaApi.getOwnUser().observeOn(AndroidSchedulers.mainThread())
            //.subscribeOn(Schedulers.io())
            .subscribe({ user ->
                view?.showInfoDialog()
                initialProfileImageUrl = user.profilePicture ?: ""
                profileImageUrl = initialProfileImageUrl
                if (profileImageUrl.isNotEmpty())
                view?.setProfilePicture(profileImageUrl)
                view?.showOverlayChangePicture()

                email = user.email
                firstname = user.firstname
                lastname = user.lastname

                notifyPropertyChanged(BR.firstname)
                notifyPropertyChanged(BR.lastname)
            }, { t ->
                /*val responseMessage = t.message
                if (responseMessage != null) {
                    if (responseMessage.contains(
                            "HTTP 404",
                            ignoreCase = true
                        )
                    ) {

                        view?.setInputPasswordError("")
                        view?.showToast("Authorization is not valid")
                    } else if (responseMessage.contains(
                            "HTTP 401",
                            ignoreCase = true
                        )
                    ) view?.showToast("You are not authorized to log in")
                    else view?.showToast(R.string.network_issue_check_network)
                }*/
                Log.e("observer_ex", "exception getting user info: $t")
            }).addTo(compositeDisposable)
    }

    private fun validPassword(password: String): Boolean {
        return password.length > 4
    }

    fun onFirstnameTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if ((count > 0) && (before==0)) view?.resetFirstnameError()
    }

    fun onLastnameTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if ((count > 0) && (before==0)) view?.resetLastnameError()
    }

    fun onPasswordTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if ((count > 0) && (before==0)) view?.resetPasswordError()
    }

    fun onPassword2TextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if ((count > 0) && (before==0)) view?.resetPasswordRepeatError()
    }
    private val PASSWORD_PATTERN = Pattern.compile(
        "^" +  "(?=.*[0-9])" +         //at least 1 digit
                "(?=.*[a-zA-Z])" +  //at least 1 letter->any letter
                "(?=.*[@#$%^&+=!?])" +  //at least 1 special character
                "(?=\\S+$)" +  //no white spaces
                ".{8,}" +  //at least 8 characters
                "$"
    )
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