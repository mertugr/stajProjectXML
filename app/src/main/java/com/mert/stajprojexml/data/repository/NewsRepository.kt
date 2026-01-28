package com.mert.stajprojexml.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mert.stajprojexml.data.paging.NewsPagingSource
import com.mert.stajprojexml.data.remote.api.NewsApi
import com.mert.stajprojexml.data.remote.dto.ArticleDto
import kotlinx.coroutines.flow.Flow

class NewsRepository(private val api: NewsApi){
    fun getTopHeadlines(country: String): Flow<PagingData<ArticleDto>>  {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { NewsPagingSource(api, country)}
        ).flow
    }
}