package com.mert.stajprojexml.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mert.stajprojexml.data.remote.dto.ArticleDto
import com.mert.stajprojexml.data.repository.NewsRepository
import kotlinx.coroutines.flow.Flow

class NewsViewModel(private val repo: NewsRepository) : ViewModel() {
    val news: Flow<PagingData<ArticleDto>> =
        repo.getTopHeadlines(country = "us").cachedIn(viewModelScope)
}