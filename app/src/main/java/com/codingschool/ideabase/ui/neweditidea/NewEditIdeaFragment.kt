package com.codingschool.ideabase.ui.neweditidea

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.codingschool.ideabase.R
import com.codingschool.ideabase.databinding.FragmentLoginBinding
import com.codingschool.ideabase.databinding.FragmentNewEditIdeaBinding
import com.codingschool.ideabase.ui.login.LoginViewModel

class NewEditIdeaFragment: Fragment(), NewEditIdeaView {
    private lateinit var viewModel: NewEditIdeaViewModel
    private lateinit var binding: FragmentNewEditIdeaBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_new_edit_idea,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = NewEditIdeaViewModel()
        binding.vm = viewModel
        viewModel.attachView(this)
    }
}