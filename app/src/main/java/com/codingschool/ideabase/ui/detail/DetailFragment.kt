package com.codingschool.ideabase.ui.detail

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.codingschool.ideabase.MainActivity
import com.codingschool.ideabase.R
import com.codingschool.ideabase.databinding.FragmentDetailBinding
import com.codingschool.ideabase.utils.ImageHandler
import com.codingschool.ideabase.utils.errorHandler
import com.codingschool.ideabase.utils.toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class DetailFragment: Fragment(), DetailView {

    private val viewModel: DetailViewModel by inject {
        parametersOf(arguments?.let { DetailFragmentArgs.fromBundle(it).id })
    }

    private lateinit var binding: FragmentDetailBinding

    private val imageHandler: ImageHandler by inject()
    private var menuForManager = false
    private var managerIsAuthor = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

    override fun showIdeaReleased() {
        binding.vwLine2.setBackgroundColor(Color.YELLOW)
    }

    override fun navigateBack() {
        Navigation.findNavController(requireView()).navigateUp()
    }

    override fun navigateToEditFragment(id: String) {
            val action: NavDirections =
                DetailFragmentDirections.toEditNewIdea(id)
            Navigation.findNavController(requireView()).navigate(action)
    }

    override fun setRatingIcon(drawIcon: Int) {
        binding.btRate.setImageResource(drawIcon)
    }

    override fun showMenu() {
        setHasOptionsMenu(true)
    }

    override fun hideMenu() {
        setHasOptionsMenu(false)
    }

    override fun addReleaseMenuItem(isAuthor: Boolean) {
        // user MANAGER, add release menu item, and if also author, edit/delete in onprepare options menu
        menuForManager = true
        managerIsAuthor = isAuthor
        requireActivity().invalidateOptionsMenu()
    }

    override fun setActionBarTitle(title: String) {
        (activity as MainActivity).supportActionBar?.title = title
    }

    override fun releaseDialog() {
        MaterialAlertDialogBuilder(
            requireActivity()
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

    override fun showPopupRatingDialog(id: String, checkedItem: Int) {
        val ratingArray =
            arrayOf(
                getString(R.string.rating_1),
                getString(R.string.rating_2),
                getString(R.string.rating_3),
                getString(R.string.rating_4),
                getString(R.string.rating_5))
        var newCheckedItem = 0
        MaterialAlertDialogBuilder(
            requireActivity(),
            R.style.materialDialog
        )
            .setTitle(getString(R.string.dialog_title_rate))
            .setSingleChoiceItems(ratingArray,checkedItem) { _, which ->
                newCheckedItem = which
            }
            .setNegativeButton(getString(R.string.btn_cancel_dialog)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.btn_rate)) { dialog, _ ->
                viewModel.setRating(id, checkedItem, newCheckedItem)
                dialog.dismiss()
            }
            .show()
    }

    override fun handleErrorResponse(errorMessage: String?) {
        if (!requireActivity().errorHandler(errorMessage)) showToast(R.string.network_issue_check_network)
        navigateBack()
    }

    override fun navigateToCommentFragment(id: String) {
        val action: NavDirections =
            DetailFragmentDirections.toComment(id)
        Navigation.findNavController(requireView()).navigate(action)
    }

    override fun navigateToProfile(id: String) {
        val action: NavDirections =
            DetailFragmentDirections.toProfile(id)
        Navigation.findNavController(requireView()).navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.compositeDisposable.clear()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_idea_detail, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        // if  admin, add release menu item
        menu.findItem(R.id.release)?.isEnabled = menuForManager
        menu.findItem(R.id.delete)?.isEnabled = (!menuForManager) or (menuForManager && managerIsAuthor)
        menu.findItem(R.id.edit)?.isEnabled = (!menuForManager) or (menuForManager && managerIsAuthor)
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