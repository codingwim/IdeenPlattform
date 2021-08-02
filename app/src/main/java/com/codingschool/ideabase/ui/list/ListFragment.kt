package com.codingschool.ideabase.ui.list

import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.codingschool.ideabase.MainActivity
import com.codingschool.ideabase.R
import com.codingschool.ideabase.databinding.FragmentListBinding
import com.codingschool.ideabase.utils.getResString
import com.codingschool.ideabase.utils.toast
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class ListFragment : Fragment(), ListView {

    private val viewModel: ListViewModel by inject<ListViewModel> {
        parametersOf(arguments?.let { ListFragmentArgs.fromBundle(it).topOrAll })
    }

    private lateinit var binding: FragmentListBinding
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var topBadge: BadgeDrawable
    private lateinit var allBadge: BadgeDrawable

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

        //access botomnav badges
        bottomNav = (requireActivity() as MainActivity).findViewById<BottomNavigationView>(R.id.nav_view)
        topBadge = bottomNav.getOrCreateBadge(R.id.navigation_top_ranked)
        allBadge = bottomNav.getOrCreateBadge(R.id.navigation_all_ideas)

        // set fab button
        binding.fab.setOnClickListener {
            viewModel.addIdeaClicked()
        }
        setHasOptionsMenu(true)

        binding.vm = viewModel
        binding.rvIdeas.adapter = viewModel.adapter
        viewModel.attachView(this)
        viewModel.init()
    }

    override fun showToast(any: Any) {
        requireActivity().toast(any)
    }

    override fun getString(any: Any) =
        requireActivity().getResString(any)

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
            .setSingleChoiceItems(ratingArray,checkedItem) { dialog, which ->
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

    override fun navigateToDetailFragment(id: String) {
        val action: NavDirections =
            ListFragmentDirections.toDetail(id)
        Navigation.findNavController(requireView()).navigate(action)
    }

    override fun navigateToCommentFragment(id: String) {
        val action: NavDirections =
            ListFragmentDirections.toComment(id)
        Navigation.findNavController(requireView()).navigate(action)
    }

    override fun navigateToNewIdeaFragment() {
        val action: NavDirections =
            ListFragmentDirections.toEditNewIdea("")
        Navigation.findNavController(requireView()).navigate(action)
    }

    override fun navigateToProfile(id: String) {
        val action: NavDirections =
            ListFragmentDirections.toProfile(id)
        Navigation.findNavController(requireView()).navigate(action)
    }

    override fun moveToPositionInRecyclerview(position: Int) {
        binding.rvIdeas.smoothScrollToPosition(position)
    }

    override fun showSearchDialog(
        categoryArray: Array<String>,
        checkedItems: BooleanArray,
        searchText: String,
        messageSelecteCategories: String
    ) {
        val inputEditTextField = EditText(requireActivity())
        if (searchText.isNotEmpty()) inputEditTextField.setText(searchText) else inputEditTextField.setHint(
            getString(R.string.search_for_hint_dialog)
        )
        inputEditTextField.inputType = InputType.TYPE_CLASS_TEXT
        /*val linearLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT )
        linearLayoutParams.setMargins(150,0,150,0)
        inputEditTextField.layoutParams = linearLayoutParams*/

        MaterialAlertDialogBuilder(
            requireActivity(),
            R.style.materialDialog
        )
            .setTitle(getString(R.string.title_search_dialog))
            .setMessage(messageSelecteCategories)
            // TODO remove dialog_edit_text.xml if not used
            //.setView(R.layout.dialog_edit_text)
            //.setView(inputEditTextField, 30,0,30,0)
            .setView(inputEditTextField)
            .setNeutralButton(getString(R.string.btn_cancel_dialog)) { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.btn_filter_dialog)) { dialog, _ ->
                val newSearchText =
                    if (inputEditTextField.text.isNotEmpty()) inputEditTextField.text.toString() else ""
                viewModel.setFilterDialog(
                    categoryArray,
                    checkedItems,
                    newSearchText,
                    messageSelecteCategories
                )
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.btn_search_dialog)) { dialog, _ ->
                val newSearchText =
                    if (inputEditTextField.text.isNotEmpty()) inputEditTextField.text.toString() else ""
                viewModel.filterWithSelectedItemsAndSearchText(checkedItems, newSearchText)
                dialog.dismiss()
            }
            .show()
    }

    override fun showFilterDialog(
        categoryArray: Array<String>,
        checkedItems: BooleanArray,
        searchText: String,
        messageSelecteCategories: String
    ) {

        MaterialAlertDialogBuilder(
            requireActivity()
        )
            .setTitle(getString(R.string.title_filter_dialog))
            //.setMessage("If no categories are selected, the result includes all possible categories.")
            .setMultiChoiceItems(
                categoryArray,
                checkedItems
            ) { dialog, whichSelected, hasSelection ->
                checkedItems[whichSelected] = hasSelection
                //Log.d("observer_ex", "wichSelected $whichSelected , hasSelectio: $hasSelection")
            }
            .setNegativeButton(getString(R.string.btn_back_dialog)) { dialog, _ ->
                viewModel.setSearchDialog(categoryArray, checkedItems, searchText)
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.btn_search_dialog)) { dialog, _ ->
                viewModel.filterWithSelectedItemsAndSearchText(checkedItems, searchText)
                dialog.dismiss()
            }
            .show()
    }

    override fun hideTopBadge() {
        topBadge.isVisible = false
    }

    override fun setTopBadge() {
        topBadge.isVisible = true
    }

    override fun hideAllBadge() {
        allBadge.isVisible = false
        allBadge.clearNumber()
    }

    override fun setAllBadge(numberOfNewItems: Int) {
        allBadge.isVisible = true
        allBadge.number = numberOfNewItems
    }

    override fun setAllBadgeNoNumber() {
        allBadge.clearNumber()
        allBadge.isVisible = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.compositeDisposable.clear()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_list_filter, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.filter -> {
                viewModel.setInitialSearchDialog()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}