package com.codingschool.ideabase.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codingschool.ideabase.databinding.IdeaItemBinding
import com.codingschool.ideabase.model.data.Idea
import com.codingschool.ideabase.model.remote.ImageHandler

class IdeaListAdapter(private val imageHandler: ImageHandler): RecyclerView.Adapter<IdeaListAdapter.IdeaViewHolder>() {

    private var list: List<Idea> = emptyList()

    class IdeaViewHolder(private val binding: IdeaItemBinding, private val imageHandler: ImageHandler ): RecyclerView.ViewHolder(binding.root) {
        fun setBinding(idea: Idea) {
            binding.tvIdeaTitle.text = idea.title
            binding.tvAuthor.text = idea.Author()
            binding.tvIdeaDescription.text = idea.description
            idea.author.profilePicture?.let { imageHandler.getProfilePic(it, binding.ivProfilePicture ) }
            if (idea.imageUrl.isNotEmpty()) imageHandler.getIdeaImage(idea.imageUrl, binding.ivIdea)
        }
    }

    fun setData(list: List<Idea>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IdeaViewHolder {
        val binding = IdeaItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return IdeaViewHolder(binding, imageHandler)
    }

    override fun onBindViewHolder(holder: IdeaViewHolder, position: Int) {
        val idea = list[position]
        holder.setBinding(idea)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}