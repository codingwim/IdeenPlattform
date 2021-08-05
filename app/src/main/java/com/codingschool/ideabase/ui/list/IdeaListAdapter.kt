package com.codingschool.ideabase.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.codingschool.ideabase.R
import com.codingschool.ideabase.databinding.IdeaItemBinding
import com.codingschool.ideabase.model.data.Idea
import com.codingschool.ideabase.utils.ImageHandler
import com.codingschool.ideabase.utils.Status
import com.codingschool.ideabase.utils.Trend

class IdeaListAdapter(private val imageHandler: ImageHandler) :
    RecyclerView.Adapter<IdeaListAdapter.IdeaViewHolder>() {

    private var list: List<Idea> = emptyList()
    private var topOrAll: Boolean = true
    private var isManager: Boolean = false
    private lateinit var ideaClickListener: (String) -> Unit
    private lateinit var commentClickListener: (String) -> Unit
    private lateinit var rateClickListener: (String, Int) -> Unit
    private lateinit var profileClickListener: (String) -> Unit

    class IdeaViewHolder(
        private val binding: IdeaItemBinding,
        private val imageHandler: ImageHandler
    ) : RecyclerView.ViewHolder(binding.root) {
        fun setBinding(
            idea: Idea,
            topOrAll: Boolean,
            isManager: Boolean,
            ideaClickListener: (String) -> Unit,
            commentClickListener: (String) -> Unit,
            rateClickListener: (String, Int) -> Unit,
            profileClickListener: (String) -> Unit
        ) {
            binding.tvIdeaTitle.text = idea.title
            if (idea.released && isManager) {
                val yellow = ContextCompat.getColor(this.itemView.context, R.color.yellow)
                binding.cvTop.strokeColor = yellow
                binding.cvTop.strokeWidth = 2
            }
            binding.tvAuthor.text = idea.authorName
            binding.tvIdeaDescription.text = idea.description
            if (topOrAll) {
                if (idea.trend != null) {
                    when (idea.trend) {
                        Trend.UP -> {
                            binding.ivTrendUp.visibility = View.VISIBLE
                            binding.ivTrendDown.visibility = View.GONE
                        }
                        Trend.DOWN -> {
                            binding.ivTrendDown.visibility = View.VISIBLE
                            binding.ivTrendUp.visibility = View.GONE
                        }
                        else -> {
                            binding.ivTrendUp.visibility = View.GONE
                            binding.ivTrendDown.visibility = View.GONE
                        }
                    }
                }
            } else {
                if (idea.status != null) {
                    binding.tvStatus.text = getStatusText(idea)
                    val black = ContextCompat.getColor(this.itemView.context, R.color.black)
                    if (idea.status != Status.NONE) {
                        binding.cvTop.strokeColor = black
                        binding.cvTop.strokeWidth = 2
                    }
                }
            }
            setRatingImage(idea.avgRating)
            imageHandler.getProfilePic(idea.author.profilePicture, binding.ivProfilePicture)
            imageHandler.getIdeaImage(idea.imageUrl, binding.ivIdea)
            binding.ivProfilePicture.setOnClickListener {
                profileClickListener(idea.author.id)
            }
            binding.cvTop.setOnClickListener {
                ideaClickListener(idea.id)
            }
            binding.btComment.setOnClickListener {
                commentClickListener(idea.id)
            }
            binding.btRate.setOnClickListener {
                rateClickListener(idea.id, this.bindingAdapterPosition)
            }
        }

        private fun setRatingImage(avgRating: Double) {
            val drawIcon = when (avgRating) {
                in 0.1..1.5 -> R.drawable.rated_1
                in 1.5..2.5 -> R.drawable.rated_2
                in 2.5..3.5 -> R.drawable.rated_3
                in 3.5..4.5 -> R.drawable.rated_4
                in 4.5..5.0 -> R.drawable.rated_5
                else -> R.drawable.rated_none
            }
            binding.btRate.setImageResource(drawIcon)
        }

        private fun getStatusText(idea: Idea): String {
            return when (idea.status) {
                Status.RELEASED -> this.itemView.context.getString(R.string.released_status)
                Status.UPDATED -> this.itemView.context.getString(R.string.updated_status)
                Status.NEW -> this.itemView.context.getString(R.string.new_status)
                else -> ""
            }
        }
    }

    fun setTopOrAll(topOrAll: Boolean) {
        this.topOrAll = topOrAll
    }

    fun setIsManager() {
        this.isManager = true
    }

    fun updateList(newList: List<Idea>) {
        val diffResult = DiffUtil.calculateDiff(
            object : DiffUtil.Callback() {
                override fun getOldListSize(): Int = list.size

                override fun getNewListSize(): Int = newList.size

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val oldItem = list[oldItemPosition]
                    val newItem = newList[newItemPosition]
                    return (oldItem.id == newItem.id)
                }

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    val oldItem = list[oldItemPosition]
                    val newItem = newList[newItemPosition]
                    return oldItem.title == newItem.title && oldItem.description == newItem.description && oldItem.category == newItem.category && oldItem.imageUrl == newItem.imageUrl && oldItem.avgRating == newItem.avgRating
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
        holder.setBinding(
            idea,
            topOrAll,
            isManager,
            ideaClickListener,
            commentClickListener,
            rateClickListener,
            profileClickListener
        )
    }

    fun addIdeaClickListener(listener: (String) -> Unit) {
        this.ideaClickListener = listener
    }

    fun addCommentClickListener(listener: (String) -> Unit) {
        this.commentClickListener = listener
    }

    fun addRateClickListener(listener: (String, Int) -> Unit) {
        this.rateClickListener = listener
    }

    fun addProfileClickListener(listener: (String) -> Unit) {
        this.profileClickListener = listener
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateRating(position: Int, idea: Idea) {
        val updatesList = list.toMutableList()
            updatesList[position] = idea
        list=updatesList
        notifyItemChanged(position)
    }
}