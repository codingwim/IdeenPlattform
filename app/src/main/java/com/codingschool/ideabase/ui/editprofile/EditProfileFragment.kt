package com.codingschool.ideabase.ui.editprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.codingschool.ideabase.R
import com.codingschool.ideabase.databinding.FragmentEditProfileBinding

class EditProfileFragment: Fragment(), EditProfileView {

    private lateinit var viewModel: EditProfileViewModel
    private lateinit var binding: FragmentEditProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_edit_profile,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = EditProfileViewModel()
        viewModel.attachView(this)
    }

    override fun showToast(text: String) {
        TODO("Not yet implemented")
    }

    override fun navigateToLoginRegistered(username: String) {
        TODO("Not yet implemented")
    }
}