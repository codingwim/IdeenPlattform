package com.codingschool.ideabase.ui.editprofile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.codingschool.ideabase.R
import com.codingschool.ideabase.databinding.FragmentEditProfileBinding
import com.codingschool.ideabase.utils.ImageHandler
import com.codingschool.ideabase.utils.errorHandler
import com.codingschool.ideabase.utils.getResString
import com.codingschool.ideabase.utils.toast
import com.github.drjacky.imagepicker.ImagePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class EditProfileFragment: Fragment(), EditProfileView {

    private val viewModel: EditProfileViewModel by inject<EditProfileViewModel> {
        parametersOf(arguments?.let { EditProfileFragmentArgs.fromBundle(it).loadPictureSelector })
    }
    private lateinit var binding: FragmentEditProfileBinding

    private val imageHandler: ImageHandler by inject()
    private lateinit var launcher: ActivityResultLauncher<Intent>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data!!
                // Use the uri to load the image
                viewModel.setSelectedProfileImage(uri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_edit_profile,
            container,
            false
        )
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.attachView(this)
        binding.vm = viewModel
        viewModel.init()
    }

    override fun showToast(any: Any) {
        requireActivity().toast(any)
    }

    override fun showInfoDialog() {
        MaterialAlertDialogBuilder(
            requireActivity(),
            R.style.materialDialog
        )
            .setTitle(getString(R.string.dialog_edit_profile_title))
            .setMessage(getString(R.string.dialog_edit_profile_message))
            .setPositiveButton(getString(R.string.understood_button)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun handleErrorResponse(errorMessage: String?) {
        if (requireActivity().errorHandler(errorMessage)) {
            showToast(R.string.network_issue_check_network)
        }
    }

    override fun setProfilePicture(url: String?) {
        imageHandler.getProfilePic(url, binding.ivProfilePicture)
    }

    override fun getProfileImageDialog() {
        ImagePicker.with(requireActivity())
            .cropSquare()
            .cropOval()
            .maxResultSize(180,180,true)
            .createIntentFromDialog {
                launcher.launch(it) }
    }

    override fun showOverlayChangePicture() {
        binding.tvChangePicture.visibility = View.VISIBLE
    }

    override fun setInputFirstnameError(any: Any) {
        binding.tilFirstname.error = requireActivity().getResString(any)
    }

    override fun resetFirstnameError() {
        binding.tilFirstname.error = null
    }

    override fun setInputLastnameError(any: Any) {
        binding.tilLastname.error = requireActivity().getResString(any)
    }

    override fun resetLastnameError() {
        binding.tilLastname.error = null
    }

    override fun setInputPasswordError(any: Any) {
        binding.tilPassword.error = requireActivity().getResString(any)
    }

    override fun resetPasswordError() {
        binding.tilPassword.error = null
    }

    override fun setInputPasswordRepeatError(any: Any) {
        binding.tilPasswordagain.error = requireActivity().getResString(any)
    }

    override fun resetPasswordRepeatError() {
        binding.tilPasswordagain.error = null
    }

    override fun setFocusPasswordInput() {
        binding.tilPassword.requestFocus()
    }

    override fun navigateToLoginRegistered(username: String) {
        val action: NavDirections =
            EditProfileFragmentDirections.toLogin(username)
        Navigation.findNavController(requireView()).navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.compositeDisposable.clear()
    }
}