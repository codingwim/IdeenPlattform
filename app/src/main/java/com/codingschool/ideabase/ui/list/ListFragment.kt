package com.codingschool.ideabase.ui.list

import android.os.Bundle
import android.text.InputType
import android.text.Layout
import android.util.Log
import android.view.*
import android.widget.AbsListView
import android.widget.EditText
import androidx.core.view.MarginLayoutParamsCompat
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.codingschool.ideabase.R
import com.codingschool.ideabase.databinding.FragmentListBinding
import com.codingschool.ideabase.utils.toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.android.ext.android.inject


class ListFragment : Fragment(), ListView {

    private val viewModel: ListViewModel by inject()
    private lateinit var binding: FragmentListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_list,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        binding.vm = viewModel
        binding.rvIdeas.adapter = viewModel.adapter
        viewModel.attachView(this)
        viewModel.init()

    }

    override fun showToast(any: Any) {
        requireActivity().toast(any)
    }

    override fun showPopupRatingMenu() {
        TODO("Not yet implemented")
    }

    override fun navigateToDetailFragment(id: String) {
        TODO("Not yet implemented")
    }

    override fun navigateToCommentFragment(id: String) {
        TODO("Not yet implemented")
    }

    override fun showSearchDialog(categoryArray: Array<String>, checkedItems: BooleanArray, searchText: String) {
        val inputEditTextField = EditText(requireActivity())
        if (searchText.isNotEmpty()) inputEditTextField.setText(searchText) else inputEditTextField.setHint("search for...")
        inputEditTextField.inputType = InputType.TYPE_CLASS_TEXT

        MaterialAlertDialogBuilder(
            requireActivity(),
            R.style.materialDialog
        )
            .setTitle("Enter search text below:")
            .setMessage("You can add categories to filter the result. Just click FILTER below")
                // todo better with layout so we can also show the selected categries in a textfield
            //.setView(R.layout.dialog_edit_text)
            .setView(inputEditTextField)
            .setNeutralButton("CANCEL") { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton("FILTER") { dialog, _ ->
                val newSearchText =  if (inputEditTextField.text.isNotEmpty()) inputEditTextField.text.toString() else ""
                showFilterDialog(categoryArray,checkedItems,newSearchText)
                dialog.dismiss()
            }
            .setPositiveButton("SEARCH") { dialog, _ ->
                viewModel.filterWithSelectedItemsAndSearchText(checkedItems, searchText)
                dialog.dismiss()
            }
            .show()
    }


    override fun showFilterDialog(categoryArray: Array<String>, checkedItems: BooleanArray, searchText: String) {

        MaterialAlertDialogBuilder(
            requireActivity(),
            R.style.materialDialog
        )
            .setTitle("Search ideas by name and/or categories")
            //.setMessage("If no categories are selected, the result includes all possible categories.")
            .setMultiChoiceItems(
                categoryArray,
                checkedItems
            ) { dialog, whichSelected, hasSelection ->
                checkedItems[whichSelected] = hasSelection
                Log.d("observer_ex", "wichSelected $whichSelected , hasSelectio: $hasSelection")
            }
            .setNegativeButton("BACK") { dialog, _ ->
                showSearchDialog(categoryArray,checkedItems, searchText)
                dialog.dismiss()
            }
            .setPositiveButton("SEARCH") { dialog, _ ->
                viewModel.filterWithSelectedItemsAndSearchText(checkedItems, searchText)
                dialog.dismiss()
            }
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_list_filter, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.filter -> {
                // open search dialog
                viewModel.setSearchDialog()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}