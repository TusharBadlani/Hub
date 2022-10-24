package cessini.technology.commonui.presentation.comment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cessini.technology.commonui.R
import cessini.technology.commonui.databinding.CommentItemBinding
import cessini.technology.model.Comment
import com.bumptech.glide.Glide

class CommentAdapter : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    private lateinit var context: Context
    private var comments = emptyList<Comment>()

    fun submitList(comments: List<Comment>) {
        this.comments = comments
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        context = parent.context
        val binding = CommentItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(comments[position])
    }

    override fun getItemCount() = comments.size

    inner class CommentViewHolder(val binding: CommentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(comment: Comment) {
            binding.txtCommentBody.text = comment.text
            binding.txtUsernameComment.text = comment.channelName

            Glide
                .with(context)
                .load(comment.profilePicture)
                .placeholder(R.drawable.ic_user_defolt_avator)
                .circleCrop()
                .into(binding.imgUserImage)
        }

    }

}
