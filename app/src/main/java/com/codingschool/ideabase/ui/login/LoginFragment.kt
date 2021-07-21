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
import com.codingschool.ideabase.utils.toast
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf


class LoginFragment : Fragment(), LoginView {

    private val viewModel: LoginViewModel by inject<LoginViewModel> {
        parametersOf(arguments?.let { LoginFragmentArgs.fromBundle(it).userName })
        }
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
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
    }

    override fun showToast(text: String) {
        requireActivity().toast(text)
    }

    override fun setInputEmptyError(text: String) {
        binding.tilUsername.error = text
    }

    override fun navigateToRegisterFragment() {
        val action: NavDirections =
            LoginFragmentDirections.toRegister()
        Navigation.findNavController(requireView()).navigate(action)
    }

    override fun resetError() {
        binding.tilUsername.error = null
    }
}