package com.codingschool.ideabase.ui.loading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.codingschool.ideabase.R
import com.codingschool.ideabase.databinding.FragmentLoadingBinding
import com.codingschool.ideabase.ui.login.LoginFragmentDirections
import com.codingschool.ideabase.ui.register.RegisterFragmentDirections
import com.codingschool.ideabase.utils.toast
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class LoadingFragment: Fragment(), LoadingView {
    private val viewModel: LoadingViewModel by inject<LoadingViewModel> {
        parametersOf(arguments?.let { LoadingFragmentArgs.fromBundle(it).onLine })
    }
    private lateinit var binding: FragmentLoadingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_loading,
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

    override fun navigateToLogin() {
        //toLogin with userName !!
        val action: NavDirections =
            LoadingFragmentDirections.toLogin()
        Navigation.findNavController(requireView()).navigate(action)
    }

    override fun navigateToTopRankedFragment() {
        val action: NavDirections =
            LoadingFragmentDirections.toTopRanked()
        Navigation.findNavController(requireView()).navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.compositeDisposable.clear()
    }

}