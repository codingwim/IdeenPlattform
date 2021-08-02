package com.codingschool.ideabase.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    lateinit var ideaClickListener: (String) -> Unit
    lateinit var commentClickListener: (String, String) -> Unit
    lateinit var rateClickListener: (String) -> Unit
    lateinit var profileClickListener: (String) -> Unit

    class IdeaViewHolder(
        private val binding: IdeaItemBinding,
        private val imageHandler: ImageHandler
    ) : RecyclerView.ViewHolder(binding.root) {
        fun setBinding(
            idea: Idea,
            topOrAll: Boolean,
            ideaClickListener: (String) -> Unit,
            commentClickListener: (String, String) -> Unit,
            rateClickListener: (String) -> Unit,
            profileClickListener: (String) -> Unit
        ) {
            binding.tvIdeaTitle.text = idea.title //+ " R:" + idea.avgRating.toString()
            binding.tvAuthor.text = idea.authorName
            binding.tvIdeaDescription.text = idea.description
            if (topOrAll) {
                //binding.tvStatus.text = idea.avgRating.toString()
                if (idea.trend != null) {
                    if (idea.trend == Trend.UP) {
                        binding.ivTrendUp.visibility = View.VISIBLE
                        binding.ivTrendDown.visibility = View.GONE
                    } else if (idea.trend == Trend.DOWN) {
                        binding.ivTrendDown.visibility = View.VISIBLE
                        binding.ivTrendUp.visibility = View.GONE
                    } else {
                        binding.ivTrendUp.visibility = View.GONE
                        binding.ivTrendDown.visibility = View.GONE
                    }
                } //else binding.tvStatus.text = idea.avgRating.toString() + "null"
            } else binding.tvStatus.text = getStatusText(idea)
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
                commentClickListener(idea.id, idea.title)
            }
            binding.btRate.setOnClickListener {
                rateClickListener(idea.id)
            }
        }

        private fun setRatingImage(avgRating: Double) {
            val drawIcon =  when (avgRating) {
                in 0.1..1.5 -> R.drawable.rated_1
                in 1.5..2.5 -> R.drawable.rated_2
                in 2.5..3.5 -> R.drawable.rated_3
                in 3.5..4.5 -> R.drawable.rated_4
                in 4.5..5.0 -> R.drawable.rated_5
                else ->  R.drawable.rated_none
            }
            binding.btRate.setImageResource(drawIcon)
        }

        private fun getStatusText(idea: Idea): String {
            return if (idea.status == Status.RELEASED) "Released"
            else if (idea.status == Status.UPDATED) "Updated"
            else if (idea.status == Status.NEW) "New"
            else ""
        }

    }



/*    fun setData(list: List<Idea>) {
        this.list = list
        notifyDataSetChanged()
    }*/

fun setTopOrAll(topOrAll: Boolean) {
    this.topOrAll = topOrAll
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
                return oldItem.title == newItem.title && oldItem.description == newItem.description && oldItem.category == newItem.category && oldItem.imageUrl == newItem.imageUrl
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
        ideaClickListener,
        commentClickListener,
        rateClickListener,
        profileClickListener
    )
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