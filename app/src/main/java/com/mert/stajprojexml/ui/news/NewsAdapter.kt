package com.mert.stajprojexml.ui.news

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mert.stajprojexml.data.remote.dto.ArticleDto
import com.mert.stajprojexml.databinding.ItemArticleBinding
import coil.load

class NewsAdapter(
    private val onClick: (ArticleDto) -> Unit
) : PagingDataAdapter<ArticleDto, NewsAdapter.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemArticleBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding, onClick)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position) ?: return
        holder.bind(item)
    }

    class VH(
        private val binding: ItemArticleBinding,
        private val onClick: (ArticleDto) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ArticleDto) {
            binding.title.text = item.title ?: "(no title)"
            binding.desc.text = item.description ?: ""
            binding.thumb.load(item.urlToImage) {
                crossfade(true)
                placeholder(android.R.color.darker_gray)
                error(android.R.color.darker_gray)
            }
            binding.root.setOnClickListener { onClick(item) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<ArticleDto>() {
            override fun areItemsTheSame(old: ArticleDto, new: ArticleDto): Boolean {
                // url null olabilir diye fallback ekledik
                return (old.url != null && old.url == new.url) ||
                        (old.url == null && old.title == new.title && old.publishedAt == new.publishedAt)
            }

            override fun areContentsTheSame(old: ArticleDto, new: ArticleDto): Boolean = old == new
        }
    }
}
