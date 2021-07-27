package com.codingschool.ideabase.ui.neweditidea

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.codingschool.ideabase.MainActivity
import com.codingschool.ideabase.R
import com.codingschool.ideabase.databinding.FragmentNewEditIdeaBinding
import com.codingschool.ideabase.model.remote.ImageHandler
import com.codingschool.ideabase.utils.getResString
import com.codingschool.ideabase.utils.toast
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

 /*   override fun setSelectedCategory(category: String) {
        binding.tvCategory.setText(category, false)
    }*/
}