package com.codingschool.ideabase.ui.neweditidea

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.codingschool.ideabase.MainActivity
import com.codingschool.ideabase.R
import com.codingschool.ideabase.databinding.FragmentNewEditIdeaBinding
import com.codingschool.ideabase.model.remote.ImageHandler
import com.codingschool.ideabase.utils.toast
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class NewEditIdeaFragment: Fragment(), NewEditIdeaView {

    private val viewModel: NewEditIdeaViewModel by inject<NewEditIdeaViewModel> {
        parametersOf(arguments?.let { NewEditIdeaFragmentArgs.fromBundle(it).editIdea })
    }

    private lateinit var binding: FragmentNewEditIdeaBinding
    private val imageHandler: ImageHandler by inject()

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

    override fun resetEmptyIdeaName() {
        TODO("Not yet implemented")
    }

    override fun showToast(any: Any) {
        requireActivity().toast(any)
    }

    override fun resetEmptyDescription() {
        TODO("Not yet implemented")
    }

    override fun setIdeaImage(uri: String) {
        imageHandler.getIdeaImage(uri, binding.ivIdeaImage)
    }
}