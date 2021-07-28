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
    lateinit var ideaClickListener: (String) -> Unit
    lateinit var commentClickListener: (String, String) -> Unit
    lateinit var rateClickListener: (String) -> Unit
    lateinit var profileClickListener: (String) -> Unit

    class IdeaViewHolder(private val binding: IdeaItemBinding, private val imageHandler: ImageHandler ): RecyclerView.ViewHolder(binding.root) {
        fun setBinding(
            idea: Idea,
            ideaClickListener: (String) -> Unit,
            commentClickListener: (String, String) -> Unit,
            rateClickListener: (String) -> Unit,
            profileClickListener: (String) -> Unit
        ) {
            binding.tvIdeaTitle.text = idea.title //+ " R:" + idea.avgRating.toString()
            binding.tvAuthor.text = idea.authorName
            binding.tvIdeaDescription.text = idea.description
            imageHandler.getProfilePic(idea.author.profilePicture, binding.ivProfilePicture)
            imageHandler.getIdeaImage(idea.imageUrl, binding.ivIdea)
            binding.ivProfilePicture.setOnClickListener {
                profileClickListener(idea.author.id)
            }
            binding.cvTop.setOnClickListener {
                ideaClickListener(idea.id)
            }
            binding.btComment.setOnClickListener {
                commentClickListener(idea.id, idea.title)
            }
            binding.btRate.setOnClickListener {
                rateClickListener(idea.id)
            }
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
        holder.setBinding(idea, ideaClickListener, commentClickListener, rateClickListener, profileClickListener)
    }

    fun addIdeaClickListener(listener: (String) -> Unit) {
        this.ideaClickListener = listener
    }

    fun addCommentClickListener(listener: (String, String) -> Unit) {
        this.commentClickListener = listener
    }

    fun addRateClickListener(listener: (String) -> Unit) {
        this.rateClickListener = listener
    }

    fun addProfileClickListener(listener: (String) -> Unit) {
        this.profileClickListener = listener
    }

    override fun getItemCount(): Int {
        return list.size
    }
}