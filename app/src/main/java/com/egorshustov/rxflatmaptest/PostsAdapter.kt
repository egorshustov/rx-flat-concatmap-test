package com.egorshustov.rxflatmaptest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.egorshustov.rxflatmaptest.data.Post
import kotlinx.android.synthetic.main.item_post.view.*


class PostsAdapter : RecyclerView.Adapter<PostsAdapter.PostHolder>() {
    private var posts = mutableListOf<Post>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, null, false)
        return PostHolder(view)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    fun updatePost(post: Post) {
        posts[posts.indexOf(post)] = post
        notifyItemChanged(posts.indexOf(post))
    }

    fun getPosts(): List<Post> {
        return posts
    }

    fun setPosts(posts: List<Post>) {
        this.posts = posts.toMutableList()
        notifyDataSetChanged()
    }

    inner class PostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(post: Post) {
            itemView.text_title.text = post.title

            val comments = post.comments
            if (comments == null) {
                showProgressBar(true)
                itemView.text_num_comments.text = ""
            } else {
                showProgressBar(false)
                itemView.text_num_comments.text = comments.size.toString()
            }
        }

        private fun showProgressBar(showProgressBar: Boolean) {
            if (showProgressBar) {
                itemView.progress_comments.visibility = View.VISIBLE
            } else {
                itemView.progress_comments.visibility = View.GONE
            }
        }
    }
}