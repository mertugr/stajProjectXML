package com.mert.stajprojexml.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mert.stajprojexml.BuildConfig
import com.mert.stajprojexml.data.remote.api.NewsApi
import com.mert.stajprojexml.data.remote.dto.ArticleDto

class NewsPagingSource(
    private val api: NewsApi,
    private val country: String
) : PagingSource<Int, ArticleDto>() {

    override fun getRefreshKey(state: PagingState<Int, ArticleDto>): Int? {
        val anchor = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchor)
        return page?.prevKey?.plus(1) ?: page?.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticleDto> {
        return try {

            val page = params.key ?: 1
            val pageSize = params.loadSize

            val response = api.topHeadlines(
                country = country,
                page = page,
                pageSize = pageSize,
                apiKey = BuildConfig.NEWS_API_KEY
            )
            Log.d("NEWS_API", "status=${response.status}, code=${response.code}, msg=${response.message}, articles=${response.articles?.size}")
            if (response.status != "ok") {
                val msg = response.message ?: "API error"
                return LoadResult.Error(IllegalStateException(msg))
            }

            val items = response.articles ?: emptyList()
            Log.d("PAGING", "page=$page items=${items.size}")

            LoadResult.Page(
                data = items,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (items.isEmpty()) null else page + 1
            )

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
