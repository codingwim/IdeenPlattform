package com.codingschool.ideabase.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
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
            imageHandler.getProfilePic(idea.author.profilePicture, binding.ivProfilePicture)
            imageHandler.getIdeaImage(idea.imageUrl, binding.ivIdea)
        }
    }

/*    fun setData(list: List<Idea>) {
        this.list = list
        notifyDataSetChanged()
    }*/

    fun updateList(newList: List<Idea>) {
        val diffResult = DiffUtil.calculateDiff(
            object : DiffUtil.Callback() {
                override fun getOldListSize(): Int = list.size

                override fun getNewListSize(): Int = newList.size

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val oldItem= list[oldItemPosition]
                    val newItem = newList[newItemPosition]
                    return (oldItem.id == newItem.id)
                }

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    val oldItem= list[oldItemPosition]
                    val newItem = newList[newItemPosition]
                    return oldItem == newItem
                }

            }
        )

        list = newList
        diffResult.dispatchUpdatesTo(this)
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