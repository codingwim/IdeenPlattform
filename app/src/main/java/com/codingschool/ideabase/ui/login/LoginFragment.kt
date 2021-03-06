package com.codingschool.ideabase.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.codingschool.ideabase.R
import com.codingschool.ideabase.databinding.FragmentLoginBinding
import com.codingschool.ideabase.utils.getResString
import com.codingschool.ideabase.utils.toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class LoginFragment : Fragment(), LoginView {

    private val viewModel: LoginViewModel by inject {
        parametersOf(arguments?.let { LoginFragmentArgs.fromBundle(it).userName })
        }
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_login,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        viewModel.attachView(this)
        viewModel.init()
    }

    override fun showToast(any: Any) {
        requireActivity().toast(any)
    }

    override fun setInputUsernameError(any: Any) {
        binding.tilUsername.error = requireActivity().getResString(any)
    }

    override fun showSetProfilePictureDialog() {
        MaterialAlertDialogBuilder(
            requireActivity()
        )
            .setTitle(getString(R.string.set_profile_pic))
            .setMessage(getString(R.string.porfile_message))
            .setNegativeButton(getString(R.string.btn_prof_set_not)) { dialog, _ ->
                viewModel.onSetPictureNow()
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.btn_later)) { dialog, _ ->
                navigateToTopRankedFragment()
                dialog.dismiss()
            }
            .show()
    }

    override fun navigateToEditProfileFragmentAndLoadPictureSelector() {
        val action: NavDirections =
            LoginFragmentDirections.toEditProfile(loadPictureSelector = true)
        Navigation.findNavController(requireView()).navigate(action)
    }

    override fun navigateToRegisterFragment() {
        val action: NavDirections =
            LoginFragmentDirections.toRegister()
        Navigation.findNavController(requireView()).navigate(action)
    }

    override fun navigateToTopRankedFragment() {
        val action: NavDirections =
            LoginFragmentDirections.toTopRanked()
        Navigation.findNavController(requireView()).navigate(action)
    }

    override fun resetUsernameError() {
        binding.tilUsername.error = null
    }

    override fun setInputPasswordError(any: Any) {
        binding.tilPassword.error = requireActivity().getResString(any)
    }

    override fun resetPasswordError() {
        binding.tilPassword.error = null
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.compositeDisposable.clear()
    }
}