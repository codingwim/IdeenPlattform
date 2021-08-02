package com.codingschool.ideabase.ui.neweditidea

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.codingschool.ideabase.MainActivity
import com.codingschool.ideabase.R
import com.codingschool.ideabase.databinding.FragmentNewEditIdeaBinding
import com.codingschool.ideabase.utils.ImageHandler
import com.codingschool.ideabase.utils.getResString
import com.codingschool.ideabase.utils.hideKeyboard
import com.codingschool.ideabase.utils.toast
import com.github.drjacky.imagepicker.ImagePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class NewEditIdeaFragment: Fragment(), NewEditIdeaView {

    private val viewModel: NewEditIdeaViewModel by inject<NewEditIdeaViewModel> {
        parametersOf(arguments?.let { NewEditIdeaFragmentArgs.fromBundle(it).editIdea })
    }

    private lateinit var binding: FragmentNewEditIdeaBinding
    private val imageHandler: ImageHandler by inject()
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private val navcontroller by lazy(::findNavController)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data!!
                // Use the uri to load the image
                viewModel.setSelectedImage(uri)
            }
        }

        // we need menu to catch back arrow in action bar
        setHasOptionsMenu(true)

        // below catches android back button
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(this) {
                viewModel.onBackPressed()
            }

    }

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

        binding.vm = viewModel
        viewModel.attachView(this)
        viewModel.init()


    }

/*    override fun getStringResource(res: Int) {
        requireActivity().getString(res)
    }*/

    override fun setActionBarTitle(title: String) {
        (activity as MainActivity).getSupportActionBar()?.title = title
    }

    override fun setCategoryListItems(items: List<String>) {
        val adapter = ArrayAdapter(requireContext(), R.layout.category_list_item, items)
        (binding.tvCategory as? AutoCompleteTextView)?.setAdapter(adapter)
    }

    override fun resetEmptyIdeaName() {
        binding.tilIdeaName.error = null
    }

    override fun showToast(any: Any) {
        requireActivity().toast(any)
    }

    override fun resetEmptyDescription() {
        binding.tilDescription.error = null
    }

    override fun setInputNameError(any: Any) {
        binding.tilIdeaName.error = requireActivity().getResString(any)
    }

    override fun setInputDescriptionError(any: Any) {
        binding.tilDescription.error = requireActivity().getResString(any)
    }

    override fun setInputCategoryError(any: Any) {
        binding.tilCategory.error = requireActivity().getResString(any)
    }

    override fun setIdeaImage(url: String) {
        imageHandler.getIdeaImage(url, binding.ivIdeaImage)
    }

    override fun setSelectedCategory(category: String) {
        binding.tvCategory.setText(category, false)
    }

    override fun getImageDialog() {
        ImagePicker.with(requireActivity())
            .crop(16f, 9f)
            .maxResultSize(480,360,true)
            .createIntentFromDialog {
                launcher.launch(it) }
    }

    override fun infoDialog(id: String) {
        MaterialAlertDialogBuilder(
            requireActivity(),
            R.style.materialDialog
        )
            .setTitle(getString(R.string.idea_add_dialog_title))
            .setMessage(getString(R.string.idea_add_dialog_message))
            .setPositiveButton(getString(R.string.understood_button)) { dialog, _ ->
                navigateToDetailFragment(id)
                dialog.dismiss()
            }
            .show()
    }

    override fun cancelDialog() {
        view?.let { requireActivity().hideKeyboard(it) }
        MaterialAlertDialogBuilder(
            requireActivity()
        )
            .setTitle("Save idea as draft?")
            .setMessage("It will be available the next time you click the add idea button!")
            .setNegativeButton("REMOVE") { dialog, _ ->
                viewModel.onCancelWithoutDraft()
                dialog.dismiss()
            }
            .setPositiveButton("SAVE") { dialog, _ ->
                viewModel.onCancelWithDraft()
                dialog.dismiss()
            }
            .show()
    }

    override fun navigateToDetailFragment(id: String) {
        val action: NavDirections =
            NewEditIdeaFragmentDirections.toDetail(id)
        Navigation.findNavController(requireView()).navigate(action)
    }

    override fun navigateBack() {
        Navigation.findNavController(requireView()).navigateUp()
    }

    override fun navigateToAllIdeas() {
        val action: NavDirections =
            NewEditIdeaFragmentDirections.toAllIdeas()
        Navigation.findNavController(requireView()).navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.compositeDisposable.clear()
    }

    override fun onOptionsItemSelected(menuItem : MenuItem) : Boolean {
        if (menuItem.getItemId() == android.R.id.home) {
            requireActivity().onBackPressed()
            return true // must return true to consume it here
        }
        return super.onOptionsItemSelected(menuItem)
    }


}