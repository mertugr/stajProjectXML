package com.mert.stajprojexml.ui.news

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mert.stajprojexml.R
import com.mert.stajprojexml.data.remote.api.ApiClient
import com.mert.stajprojexml.data.repository.NewsRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NewsListFragment : Fragment(R.layout.fragment_news_list) {

    private lateinit var adapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("NEWS_UI", "NewsListFragment opened")
        val recycler = view.findViewById<RecyclerView>(R.id.recycler)


        adapter = NewsAdapter { article ->
            // TODO:
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
        val api = ApiClient.createNewsApi()
        val repo = NewsRepository(api)
        val vm = NewsViewModel(repo)

        viewLifecycleOwner.lifecycleScope.launch {
            vm.news.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }
}
