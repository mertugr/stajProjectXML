package com.mert.stajprojexml.ui.favs

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mert.stajprojexml.R
import com.mert.stajprojexml.data.local.SessionManager
import com.mert.stajprojexml.data.repository.FavoriteRepository
import com.mert.stajprojexml.databinding.FragmentFavoritesBinding
import androidx.lifecycle.lifecycleScope
import androidx.core.os.bundleOf
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoritesFragment : Fragment(R.layout.fragment_favorites){
    private var binding: FragmentFavoritesBinding? = null
    private lateinit var adapter: FavoritesAdapter
    private lateinit var repo: FavoriteRepository
    private lateinit var session: SessionManager
    private var currentUserId: String? = null
    private var collectJob: kotlinx.coroutines.Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val b = FragmentFavoritesBinding.bind(view)
        binding = b
        session = SessionManager(requireContext())
        repo = FavoriteRepository(requireContext())
        currentUserId = session.currentUserId()

        adapter = FavoritesAdapter { fav ->
            val bundle = bundleOf(
                "title" to fav.title,
                "desc" to (fav.description ?: ""),
                "content" to (fav.content ?: ""),
                "imageUrl" to fav.imageUrl,
                "url" to fav.url
            )
            findNavController().navigate(R.id.newsDetailFragment, bundle)
        }
        b.recyclerFavs.layoutManager = LinearLayoutManager(requireContext())
        b.recyclerFavs.adapter = adapter
        startCollect()
    }

    private fun startCollect() {
        collectJob?.cancel()
        val userId = currentUserId ?: return
        collectJob = viewLifecycleOwner.lifecycleScope.launch {
            repo.favorites().collectLatest { list ->
                binding?.apply {
                    adapter.submit(list)
                    empty.text = if (list.isEmpty()) "No favorites yet" else ""
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val newId = session.currentUserId()
        if (newId != currentUserId) {
            currentUserId = newId
            startCollect()
        }
    }

    override fun onDestroyView() {
        collectJob?.cancel()
        binding = null
        super.onDestroyView()
    }
}
