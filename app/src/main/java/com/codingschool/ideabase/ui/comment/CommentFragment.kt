package com.codingschool.ideabase.ui.comment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.codingschool.ideabase.R
import com.codingschool.ideabase.databinding.FragmentCommentBinding
import com.codingschool.ideabase.utils.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class CommentFragment : Fragment(), CommentView {

    private val viewModel: CommentViewModel by inject {
        parametersOf(arguments?.let { CommentFragmentArgs.fromBundle(it).id })
    }

    private lateinit var binding: FragmentCommentBinding
    private val imageHandler: ImageHandler by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        //focus edittext
        binding.etComment.requestFocus()

        binding.vm = viewModel
        viewModel.attachView(this)
    }

    override fun showToast(any: Any) {
        requireActivity().toast(any)
    }

    override fun setProfileImage(url: String) {
        imageHandler.getProfilePic(url, binding.ivProfilePicture)
    }

    override fun setCommentEmptyError(any: Any) {
        binding.tvCommentTitle.text = requireActivity().getResString(any)
        binding.tvCommentTitle.setTextColor(Color.RED)
    }

    override fun resetCommentEmptyError() {
        binding.tvCommentTitle.text =
            requireActivity().getResString(R.string.prof_info_will_be_visible_commenting)
        binding.tvCommentTitle.setTextColor(Color.BLACK)
    }

    override fun navigateBack() {
        view?.let { requireActivity().hideKeyboard(it) }
        Navigation.findNavController(requireView()).navigateUp()
    }

    override fun handleErrorResponse(errorMessage: String?) {
        if (requireActivity().errorHandler(errorMessage)) showToast(R.string.network_issue_check_network)
    }

    override fun cancelDialog() {
        view?.let { requireActivity().hideKeyboard(it) }
        MaterialAlertDialogBuilder(
            requireActivity()
        )
            .setTitle(getString(R.string.dialog_save_draft_title))
            .setMessage(getString(R.string.dialog_comment_draft_message))
            .setNegativeButton(getString(R.string.remove_draft_dialog)) { dialog, _ ->
                viewModel.onCancelWithoutDraft()
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.save)) { dialog, _ ->
                viewModel.onCancelWithDraft()
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.compositeDisposable.clear()
    }
}