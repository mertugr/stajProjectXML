package com.mert.stajprojexml.ui.news

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.mert.stajprojexml.R
import com.mert.stajprojexml.data.local.SessionManager
import com.mert.stajprojexml.data.local.FavoriteArticle
import com.mert.stajprojexml.data.repository.FavoriteRepository
import com.mert.stajprojexml.databinding.FragmentNewsDetailBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NewsDetailFragment : Fragment(R.layout.fragment_news_detail) {

    private var binding: FragmentNewsDetailBinding? = null
    private var isFavorite = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val b = FragmentNewsDetailBinding.bind(view)
        binding = b

        val args = requireArguments()
        val title = args.getString("title").orEmpty()
        val desc = args.getString("desc").orEmpty()
        val rawContent = args.getString("content").orEmpty()
        val content = rawContent.replace(Regex("… \\[\\+\\d+ chars]"), "...")
        val imageUrl = args.getString("imageUrl")
        val url = args.getString("url")
        val favRepo = FavoriteRepository(requireContext())
        val session = SessionManager(requireContext())
        val currentUserId = session.currentUserId()

        b.title.text = title
        b.desc.text = desc.ifEmpty { content }
        b.content.text = content

        b.image.load(imageUrl) {
            crossfade(true)
            placeholder(android.R.color.darker_gray)
            error(android.R.color.darker_gray)
        }

        b.back.setOnClickListener { findNavController().popBackStack() }

        fun updateFavIcon() {
            b.fav.setImageResource(
                if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            favRepo.isFavorite(url.orEmpty()).collectLatest { fav ->
                isFavorite = fav
                updateFavIcon()
            }
        }

        b.fav.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                if (isFavorite) {
                    favRepo.remove(url.orEmpty())
                } else {
                    url?.let {
                        favRepo.add(
                            FavoriteArticle(
                                userId = currentUserId,
                                url = it,
                                title = title,
                                description = desc,
                                imageUrl = imageUrl,
                                content = content
                            )
                        )
                    }
                }
            }
        }

        b.toolbarTitle.text = "BTC News"

        b.openLink.setOnClickListener {
            url?.let {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
