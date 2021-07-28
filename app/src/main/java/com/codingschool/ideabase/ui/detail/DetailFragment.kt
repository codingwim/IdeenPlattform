package com.codingschool.ideabase.ui.detail

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.codingschool.ideabase.MainActivity
import com.codingschool.ideabase.R
import com.codingschool.ideabase.databinding.FragmentDetailBinding
import com.codingschool.ideabase.model.remote.ImageHandler
import com.codingschool.ideabase.utils.hideKeyboard
import com.codingschool.ideabase.utils.toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class DetailFragment: Fragment(), DetailView {

    private val viewModel: DetailViewModel by inject<DetailViewModel> {
        parametersOf(arguments?.let { DetailFragmentArgs.fromBundle(it).id })
    }

    private lateinit var binding: FragmentDetailBinding

    private val imageHandler: ImageHandler by inject()
    private var menuForManager = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_detail,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activityView = requireActivity().findViewById<View>(android.R.id.content)

        setHasOptionsMenu(true)
        val fab: FloatingActionButton = activityView.findViewById(R.id.fab)
        fab.hide()

        binding.vm = viewModel
        binding.rvComments.adapter = viewModel.adapter
        viewModel.attachView(this)
        viewModel.init()


    }

    override fun showToast(any: Any) {
        requireActivity().toast(any)
    }

    override fun setIdeaImage(url: String) {
        imageHandler.getIdeaImage(url, binding.ivIdea)
    }

    override fun navigateBack() {
        Navigation.findNavController(requireView()).navigateUp()
    }

    override fun navigateToEditFragment(id: String) {
            val action: NavDirections =
                DetailFragmentDirections.toEditNewIdea(id)
            Navigation.findNavController(requireView()).navigate(action)
    }

    override fun hideMenu() {
        setHasOptionsMenu(false)
    }

    override fun hideCommentTitle() {
        binding.tvCommentTitle.visibility = View.GONE
    }

    override fun addReleaseMenuItem() {
        // user MANGAGER, add release menu item
        menuForManager = true
        requireActivity().invalidateOptionsMenu()
    }


    override fun setActionBarTitle(title: String) {
        (activity as MainActivity).getSupportActionBar()?.title = title
    }

    override fun releaseDialog() {
        MaterialAlertDialogBuilder(
            requireActivity(),
            R.style.materialDialog
        )
            .setTitle(getString(R.string.dialog_title_release))
            .setMessage(getString(R.string.dialog_message_release))
            .setNegativeButton(getString(R.string.btn_cancel_dialog)) { dialog, _ ->
                viewModel.onCancelRelease()
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.btn_release)) { dialog, _ ->
                viewModel.onConfirmRelease()
                dialog.dismiss()
            }
            .show()
    }

    override fun deleteDialog() {
        MaterialAlertDialogBuilder(
            requireActivity(),
            R.style.materialDialog
        )
            .setTitle(getString(R.string.dialog_title_delete))
            .setMessage(getString(R.string.dialog_message_delete))
            .setNegativeButton(getString(R.string.btn_cancel_dialog)) { dialog, _ ->
                viewModel.onCancelDelete()
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.btn_delete)) { dialog, _ ->
                viewModel.onConfirmDelete()
                dialog.dismiss()
            }
            .show()
    }

    override fun navigateToProfile(id: String) {
        val action: NavDirections =
            DetailFragmentDirections.toProfile(id)
        Navigation.findNavController(requireView()).navigate(action)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_idea_detail, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        // if  admin, add release menu item
        menu.findItem(R.id.release)?.setEnabled(menuForManager)
        menu.findItem(R.id.delete)?.setEnabled(!menuForManager)
        // if idea released, hide menu item done with hide and hideMenu()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete -> {
                deleteDialog()
            }
            R.id.edit -> {
                viewModel.editIdea()
            }
            R.id.release -> {
                releaseDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}