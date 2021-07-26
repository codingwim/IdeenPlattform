package com.codingschool.ideabase.ui.detail

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.codingschool.ideabase.R
import com.codingschool.ideabase.databinding.FragmentDetailBinding
import com.codingschool.ideabase.databinding.FragmentListBinding
import com.codingschool.ideabase.model.remote.ImageHandler
import com.codingschool.ideabase.ui.list.ListFragmentDirections
import com.codingschool.ideabase.ui.list.ListViewModel
import com.codingschool.ideabase.ui.login.LoginFragmentArgs
import com.codingschool.ideabase.ui.login.LoginViewModel
import com.codingschool.ideabase.utils.toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class DetailFragment: Fragment(), DetailView {

    private val viewModel: DetailViewModel by inject<DetailViewModel> {
        parametersOf(arguments?.let { DetailFragmentArgs.fromBundle(it).id })
    }

    private lateinit var binding: FragmentDetailBinding
    private lateinit var fab: FloatingActionButton
    private val imageHandler: ImageHandler by inject()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
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
        val activityView = requireActivity().findViewById<View>(android.R.id.content)

        setHasOptionsMenu(true)

        // hide fab button
        fab = activityView.findViewById(R.id.fab)
        fab.hide()

        binding.vm = viewModel
        binding.rvComments.adapter = viewModel.adapter
        viewModel.attachView(this)
        viewModel.init()
    }

    override fun showToast(any: Any) {
        requireActivity().toast(any)
    }

    override fun setIdeaImage(uri: String) {
        imageHandler.getIdeaImage(uri, binding.ivIdea)
    }

    override fun navigateBack() {
        Navigation.findNavController(requireView()).navigateUp()
    }

    override fun navigateToEditFragment(id: String) {
            val action: NavDirections =
                DetailFragmentDirections.toEditNewIdea(id)
            Navigation.findNavController(requireView()).navigate(action)
    }

    override fun hideMenu() {
        setHasOptionsMenu(false)
    }

    override fun hideCommentTitle() {
        binding.tvCommentTitle.visibility = View.GONE
    }

    override fun removeReleaseMenuItem() {
        // idea released, hide delete menu item
        // todo addmenu item might be easier ?

    }

    override fun removeEditMenuItem() {


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_idea_detail, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        // if NOT (admin ), hide release menu item

        // if idea released, hide delete menu item
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete -> {
                viewModel.deleteIdea()
            }
            R.id.edit -> {
                viewModel.editIdea()
            }
            R.id.release -> {
                viewModel.releaseIdea()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}