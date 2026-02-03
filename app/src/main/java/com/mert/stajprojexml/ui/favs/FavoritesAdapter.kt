package com.mert.stajprojexml.ui.favs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mert.stajprojexml.data.local.FavoriteArticle
import com.mert.stajprojexml.databinding.ItemArticleBinding
import coil.load

class FavoritesAdapter(
    private val onClick: (FavoriteArticle) -> Unit
) : RecyclerView.Adapter<FavoritesAdapter.VH>() {

    private val items = mutableListOf<FavoriteArticle>()

    fun submit(list: List<FavoriteArticle>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemArticleBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding, onClick)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class VH(
        private val binding: ItemArticleBinding,
        private val onClick: (FavoriteArticle) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FavoriteArticle) {
            binding.title.text = item.title
            binding.desc.text = item.description.orEmpty()
            binding.thumb.load(item.imageUrl) {
                crossfade(true)
                placeholder(android.R.color.darker_gray)
                error(android.R.color.darker_gray)
            }
            binding.root.setOnClickListener { onClick(item) }
        }
    }
}
