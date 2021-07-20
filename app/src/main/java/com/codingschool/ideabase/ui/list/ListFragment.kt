package com.codingschool.ideabase.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.codingschool.ideabase.R
import com.codingschool.ideabase.databinding.FragmentListBinding

class ListFragment : Fragment(), ListView {

    private lateinit var viewModel: ListViewModel
    private lateinit var binding: FragmentListBinding

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

        viewModel = ListViewModel()
        binding.vm = viewModel
        viewModel.attachView(this)
    }
    override fun showToast(text: String) {
        TODO("Not yet implemented")
    }

    override fun showPopupRatingMenu() {
        TODO("Not yet implemented")
    }

    override fun navigateToDetailFragment(id: String) {
        TODO("Not yet implemented")
    }

    override fun navigateToCommentFragment(id: String) {
        TODO("Not yet implemented")
    }
}