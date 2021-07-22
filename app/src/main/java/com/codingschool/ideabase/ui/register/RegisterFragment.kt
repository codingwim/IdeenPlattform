package com.codingschool.ideabase.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.codingschool.ideabase.R
import com.codingschool.ideabase.databinding.FragmentRegisterBinding
import com.codingschool.ideabase.ui.login.LoginFragmentDirections
import com.codingschool.ideabase.utils.getResString
import com.codingschool.ideabase.utils.toast
import org.koin.android.ext.android.inject


class RegisterFragment : Fragment(), RegisterView {

    private val viewModel: RegisterViewModel by inject()
    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_register,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.vm = viewModel
        viewModel.attachView(this)
    }


    override fun showToast(any: Any) {
        requireActivity().toast(any)
    }

    override fun setInputFirstnameError(text: String) {
        binding.tilFirstname.error = text
    }

    override fun resetFirstnameError() {
        binding.tilFirstname.error = null
    }

    override fun setInputLastnameError(text: String) {
        binding.tilLastname.error = text
    }

    override fun resetLastnameError() {
        binding.tilLastname.error = null
    }

    override fun setInputEmailError(text: String) {
        binding.tilEmail.error = text
    }

    override fun resetEmailError() {
        binding.tilEmail.error = null
    }

    override fun setInputPasswordError(text: String) {
        binding.tilPassword.error = text
    }

    override fun resetPasswordError() {
        binding.tilPassword.error = null
    }

    override fun setInputPasswordRepeatError(text: String) {
        binding.tilPasswordagain.error = text
    }

    override fun resetPasswordRepeatError() {
        binding.tilPasswordagain.error = null
    }

    override fun setFocusPasswordInput() {
        binding.tilPassword.requestFocus()
    }

    override fun navigateToLoginRegistered(username: String) {
        //toLogin with userName !!
        val action: NavDirections =
            RegisterFragmentDirections.toLogin(username)
        Navigation.findNavController(requireView()).navigate(action)
    }

    override fun navigateCancelRegistration() {
        //toLogin with userName null
        val action: NavDirections =
            RegisterFragmentDirections.toLogin()
        Navigation.findNavController(requireView()).navigate(action)
    }

}