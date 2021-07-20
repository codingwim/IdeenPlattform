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

class DetailFragment: Fragment(), DetailView {
    private lateinit var viewModel: DetailViewModel
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

        viewModel = DetailViewModel()
        binding.vm = viewModel
        viewModel.attachView(this)
    }
}