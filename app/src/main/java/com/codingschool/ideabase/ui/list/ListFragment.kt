package com.codingschool.ideabase.ui.list

import android.os.Bundle
import android.util.Log
import android.view.*
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

    override fun showFilterDialog(categoryArray: Array<String>, checkedItems: BooleanArray) {
        // dummy items and checkeditems, these should be passed from ideApi in viewmodel
        //val items = arrayOf("category 1", "category 2", "category 3", "category 4", "category 5")

        var selectedItems = 0
        MaterialAlertDialogBuilder(
            requireActivity(),
            R.style.materialDialog
        )
            .setTitle("Search ideas by name and/or categories")
            //.setMessage("If no categories are selected, the result includes all possible categories.")
            .setMultiChoiceItems(categoryArray, checkedItems ) { dialog, whichSelected, hasSelection ->
                checkedItems[whichSelected] = hasSelection
                Log.d("observer_ex", "wichSelected $whichSelected , hasSelectio: $hasSelection")
            }

            .setNegativeButton("CANCEL") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("SEARCH") { dialog, _ ->
                viewModel.filterWithSelected(checkedItems)
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
                viewModel.setFilterDialog()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}