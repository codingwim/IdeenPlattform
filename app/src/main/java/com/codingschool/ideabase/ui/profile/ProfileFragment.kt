package com.codingschool.ideabase.ui.profile

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.codingschool.ideabase.R
import com.codingschool.ideabase.databinding.FragmentProfileBinding
import com.codingschool.ideabase.ui.detail.DetailFragmentArgs
import com.codingschool.ideabase.ui.register.RegisterFragmentDirections
import com.codingschool.ideabase.utils.ImageHandler
import com.codingschool.ideabase.utils.toast
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

 class ProfileFragment : Fragment(), ProfileView {

    private val viewModel: ProfileViewModel by inject<ProfileViewModel> {
        parametersOf(arguments?.let { ProfileFragmentArgs.fromBundle(it).id })
    }
    private lateinit var binding: FragmentProfileBinding
     private val imageHandler: ImageHandler by inject()

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

        binding.vm = viewModel
        viewModel.attachView(this)
        viewModel.init()
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_profile, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.editProfile -> {
                viewModel.editProfile()
            }
            R.id.addIdea -> {
                viewModel.addIdea()
            }
            R.id.logout -> {
                viewModel.logout()
            }
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun showToast(any: Any) {
        requireActivity().toast(any)
    }

     override fun setProfilePicture(url: String?) {
         imageHandler.getProfilePic(url, binding.ivProfilePicture)
     }

     override fun hideMenu() {
         setHasOptionsMenu(false)
     }

     override fun showMenu() {
         setHasOptionsMenu(true)
     }

    override fun navigateToEditProfileFragment() {
        val action: NavDirections =
            ProfileFragmentDirections.toEditProfile()
        Navigation.findNavController(requireView()).navigate(action)
    }


    override fun navigateToLoginFragment(username: String) {
        val action: NavDirections =
            ProfileFragmentDirections.toLogin(username)
        Navigation.findNavController(requireView()).navigate(action)
    }

    override fun navigateToNewIdeaFragment(editIdeaId: String) {
        val action: NavDirections =
            ProfileFragmentDirections.toEditNewIdea(editIdeaId)
        Navigation.findNavController(requireView()).navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.compositeDisposable.clear()
    }
}