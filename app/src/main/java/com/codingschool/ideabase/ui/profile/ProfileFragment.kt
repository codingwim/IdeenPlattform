package com.codingschool.ideabase.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.codingschool.ideabase.R
import com.codingschool.ideabase.databinding.FragmentProfileBinding
import com.codingschool.ideabase.ui.list.ListViewModel
import com.codingschool.ideabase.utils.toast

class ProfileFragment : Fragment(), ProfileView {

    private lateinit var viewModel: ProfileViewModel
    private lateinit var binding: FragmentProfileBinding

            override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_profile,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ProfileViewModel()
        binding.vm = viewModel
        viewModel.attachView(this)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.editProfil -> {

//go to something
            }
            R.id.addIdea -> {
//go to something
            }
            R.id.loggout -> {
//go to something
            }
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun showToast(any: Any) {
        requireActivity().toast(any)
    }

    override fun showPopupReleaseAlert() {
        TODO("Not yet implemented")
    }

    override fun showPopupDeleteAlert() {
        TODO("Not yet implemented")
    }

    override fun navigateToEditProfileFragment(id: String) {
        TODO("Not yet implemented")
    }

    override fun navigateToLoginFragment() {
        TODO("Not yet implemented")
    }
}