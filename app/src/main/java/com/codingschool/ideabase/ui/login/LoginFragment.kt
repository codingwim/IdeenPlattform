package com.codingschool.ideabase.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isEmpty
import androidx.core.view.isNotEmpty
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.codingschool.ideabase.R
import com.codingschool.ideabase.databinding.FragmentLoginBinding
import com.jakewharton.rxbinding4.widget.textChanges

class LoginFragment : Fragment(), LoginView {

    private lateinit var viewModel: LoginViewModel
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

        viewModel = LoginViewModel()
        binding.vm = viewModel
        viewModel.attachView(this)
    }

    override fun setInputEmptyError(text: String) {
        binding.tilUsername.error = text
    }

    override fun resetError() {
        binding.etUsername.textChanges()
        binding.tilUsername.error = null
    }
}