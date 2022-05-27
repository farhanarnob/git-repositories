package com.example.github.repositories

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.github.repositories.data.LocalDataStore
import com.example.github.repositories.data.RepositoryDTO

class RepositoryAdapter(
    val repositoryAdapterItemListener: RepositoryAdapterItemListener
) : ListAdapter<RepositoryDTO, RepositoryAdapter.ViewHolder>(DiffCallback())  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(getItem(position))
    }
    private class DiffCallback : DiffUtil.ItemCallback<RepositoryDTO>() {
        override fun areItemsTheSame(
            oldItem: RepositoryDTO,
            newItem: RepositoryDTO,
        ): Boolean {
            if (oldItem.id != newItem.id) return false
            // check if id is the same
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: RepositoryDTO,
            newItem: RepositoryDTO,
        ): Boolean {
            // check if content is the same
            // equals using data class
            return oldItem == newItem
        }
    }
    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val container: View = itemView.findViewById(R.id.news_container)
        val titleTxt: TextView = itemView.findViewById(R.id.title)
        val imageVw: ImageView = itemView.findViewById(R.id.image)
        val descriptionTxt: TextView = itemView.findViewById(R.id.description)
        val authorTxt: TextView = itemView.findViewById(R.id.author)

        @SuppressLint("SetTextI18n")
        fun bindData(item: RepositoryDTO) {
            titleTxt.text = "#" + (position + 1) + ": " + item.full_name!!.toUpperCase()
            descriptionTxt.text = if (item.description!!.length > 150) item.description!!.take(150)
                .plus("...") else item.description
            authorTxt.text = item.owner!!.login
            imageVw.setImageResource(
                if (LocalDataStore.instance.getBookmarks().contains(item))
                    R.drawable.baseline_bookmark_black_24
                else
                    R.drawable.baseline_bookmark_border_black_24
            )
            container.setOnClickListener {
                repositoryAdapterItemListener.detailItemClick(item)
            }
        }
    }
    interface RepositoryAdapterItemListener{
        fun detailItemClick(repositoryDTO: RepositoryDTO)
    }
}