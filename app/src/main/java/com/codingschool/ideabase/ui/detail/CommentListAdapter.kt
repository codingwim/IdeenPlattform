package com.codingschool.ideabase.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.codingschool.ideabase.databinding.CommentItemBinding
import com.codingschool.ideabase.model.data.Comment

class CommentListAdapter: RecyclerView.Adapter<CommentListAdapter.CommentViewHolder>() {

    private var list: List<Comment> = emptyList()
    lateinit var commentClickListener: (String) -> Unit

    class CommentViewHolder(private val binding: CommentItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun setBinding(
            comment: Comment,
            commentClickListener: (String) -> Unit
        ) {
            binding.tvCommentAuthor.text = comment.authorName
            binding.tvComment.text = comment.message
            binding.root.setOnClickListener{
                commentClickListener(comment.id)
            }
        }
    }

/*    fun setData(list: List<Idea>) {
        this.list = list
        notifyDataSetChanged()
    }*/

    fun updateList(newList: List<Comment>) {
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
                    return oldItem.id == newItem.id
                }
            }
        )
        list = newList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = CommentItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val idea = list[position]
        holder.setBinding(idea, commentClickListener)
    }


    fun addCommentClickListener(listener: (String) -> Unit) {
        this.commentClickListener = listener
    }

    override fun getItemCount(): Int {
        return list.size
    }
}