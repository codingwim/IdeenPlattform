package com.codingschool.ideabase.ui.comment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.codingschool.ideabase.R
import com.codingschool.ideabase.databinding.FragmentCommentBinding
import com.codingschool.ideabase.ui.login.LoginFragmentArgs
import com.codingschool.ideabase.utils.getResString
import com.codingschool.ideabase.utils.toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class CommentFragment : Fragment(), CommentView {

    private val viewModel: CommentViewModel by inject<CommentViewModel> {
        parametersOf(
            arguments?.let { CommentFragmentArgs.fromBundle(it).id })
        parametersOf(
            arguments?.let { CommentFragmentArgs.fromBundle(it).title })
    }

    private lateinit var binding: FragmentCommentBinding
    private lateinit var fab: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_comment,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activityView = requireActivity().findViewById<View>(android.R.id.content)

        // hide fab button
        fab = activityView.findViewById(R.id.fab)
        fab.hide()

        binding.vm = viewModel
        viewModel.attachView(this)
        viewModel.init()
    }

    override fun showToast(any: Any) {
        requireActivity().toast(any)
    }

    override fun setCommentEmptyError(any: Any) {
        binding.tvCommentTitle.text = requireActivity().getResString(any)
    }

    override fun setTitle(title: String) {
        binding.tvCommentTitle.text = "Idea: " + title
    }

}