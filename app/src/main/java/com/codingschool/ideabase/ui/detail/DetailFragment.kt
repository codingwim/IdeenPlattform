package com.codingschool.ideabase.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.codingschool.ideabase.R
import com.codingschool.ideabase.databinding.FragmentDetailBinding
import com.codingschool.ideabase.databinding.FragmentListBinding
import com.codingschool.ideabase.ui.list.ListViewModel
import com.codingschool.ideabase.ui.login.LoginFragmentArgs
import com.codingschool.ideabase.ui.login.LoginViewModel
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class DetailFragment: Fragment(), DetailView {

    private val viewModel: DetailViewModel by inject<DetailViewModel> {
        parametersOf(arguments?.let { DetailFragmentArgs.fromBundle(it).id })
    }

    private lateinit var binding: FragmentDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_list,
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
}