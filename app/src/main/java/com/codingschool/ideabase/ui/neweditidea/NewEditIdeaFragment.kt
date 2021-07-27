package com.codingschool.ideabase.ui.neweditidea

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.codingschool.ideabase.MainActivity
import com.codingschool.ideabase.R
import com.codingschool.ideabase.databinding.FragmentNewEditIdeaBinding
import com.codingschool.ideabase.model.remote.ImageHandler
import com.codingschool.ideabase.utils.getResString
import com.codingschool.ideabase.utils.toast
import com.github.drjacky.imagepicker.ImagePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class NewEditIdeaFragment: Fragment(), NewEditIdeaView {

    private val viewModel: NewEditIdeaViewModel by inject<NewEditIdeaViewModel> {
        parametersOf(arguments?.let { NewEditIdeaFragmentArgs.fromBundle(it).editIdea })
    }

    private lateinit var binding: FragmentNewEditIdeaBinding
    private val imageHandler: ImageHandler by inject()
    private lateinit var fab: FloatingActionButton
    private lateinit var launcher: ActivityResultLauncher<Intent>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data!!
                // Use the uri to load the image
                viewModel?.setSelectedImage(uri)
            }
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

        // hide fab button
        val activityView = requireActivity().findViewById<View>(android.R.id.content)
        fab = activityView.findViewById(R.id.fab)
        fab.hide()

        binding.vm = viewModel
        viewModel.attachView(this)
        viewModel.init()

        /*binding.tvCategory.onItemSelectedListener {

        }*/
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

    override fun setIdeaImage(uri: String) {
        imageHandler.getIdeaImage(uri, binding.ivIdeaImage)
    }

    override fun getImageDialog() {
        ImagePicker.with(requireActivity())
            .maxResultSize(800,600,true)
            .createIntentFromDialog {
                launcher.launch(it) }
    }

    override fun navigateBack() {
        Navigation.findNavController(requireView()).navigateUp()
    }

    override fun navigateToAllIdeas() {
        // todo add navgation to all ideas so we can see our newly added idea on top
        Navigation.findNavController(requireView()).navigateUp()
    }
    /*   override fun setSelectedCategory(category: String) {
           binding.tvCategory.setText(category, false)
       }*/
}