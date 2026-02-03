package com.mert.stajprojexml.ui.news

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mert.stajprojexml.R
import com.mert.stajprojexml.data.remote.api.ApiClient
import com.mert.stajprojexml.data.repository.NewsRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NewsListFragment : Fragment(R.layout.fragment_news_list) {

    private val api by lazy { ApiClient.createNewsApi() }
    private val repo by lazy { NewsRepository(api) }
    private val vm: NewsViewModel by viewModels { NewsViewModelFactory(repo) }
    
    private lateinit var adapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("NEWS_UI", "NewsListFragment opened")
        val recycler = view.findViewById<RecyclerView>(R.id.recycler)


        adapter = NewsAdapter { article ->
            val bundle = bundleOf(
                "title" to (article.title ?: ""),
                "desc" to (article.description ?: ""),
                "content" to (article.content ?: ""),
                "imageUrl" to article.urlToImage,
                "url" to article.url
            )
            findNavController().navigate(R.id.newsDetailFragment, bundle)
        }

        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(requireContext())

        adapter.addLoadStateListener { state ->
            val error = state.refresh as? LoadState.Error
                ?: state.prepend as? LoadState.Error
                ?: state.append as? LoadState.Error
            if (error != null) {
                Log.e("NEWS_API", "loadState error", error.error)
                Toast.makeText(requireContext(), error.error.message ?: "Load error", Toast.LENGTH_SHORT).show()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            vm.news.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }
}
