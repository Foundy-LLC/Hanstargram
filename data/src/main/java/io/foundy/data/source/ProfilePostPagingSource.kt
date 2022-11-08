package io.foundy.data.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObjects
import io.foundy.data.model.PostDto
import kotlinx.coroutines.tasks.await

class ProfilePostPagingSource(
    private val queryPostByUuid: Query
) : PagingSource<QuerySnapshot, PostDto>() {

    override fun getRefreshKey(state: PagingState<QuerySnapshot, PostDto>): QuerySnapshot? {
        return null
    }

    override suspend fun load(
        params: LoadParams<QuerySnapshot>
    ): LoadResult<QuerySnapshot, PostDto> {
        return try {
            val currentPage = params.key ?: queryPostByUuid.get().await()
            if (currentPage.isEmpty) {
                return LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }
            val lastVisiblePost = currentPage.documents[currentPage.size() - 1]
            val nextPage = queryPostByUuid.startAfter(lastVisiblePost).get().await()
            val PostDto = currentPage.toObjects<PostDto>()

            LoadResult.Page(
                data = PostDto,
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}