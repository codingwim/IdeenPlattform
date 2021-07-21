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


class RegisterFragment : Fragment(), RegisterView {

    private lateinit var viewModel: RegisterViewModel
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

        viewModel = RegisterViewModel()
        binding.vm = viewModel
        viewModel.attachView(this)
    }


/*    override fun showToast(text: String) {
        requireActivity().toast(text)
    }

    override fun showToast(res: Int) {
        requireActivity().toast(res)
        requireActivity().getResString(res)
    }*/

    override fun showToast(any: Any) {
        requireActivity().toast(any)
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