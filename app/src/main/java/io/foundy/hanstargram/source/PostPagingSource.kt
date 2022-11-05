package io.foundy.hanstargram.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.foundy.hanstargram.repository.model.LikeDto
import io.foundy.hanstargram.repository.model.PostDto
import io.foundy.hanstargram.repository.model.UserDto
import io.foundy.hanstargram.view.home.postlist.PostItemUiState
import kotlinx.coroutines.tasks.await

class PostPagingSource(
    private val queryPostsByFollower: Query
) : PagingSource<QuerySnapshot, PostItemUiState>() {

    private val currentUserId = Firebase.auth.currentUser!!.uid
    private val likeCollection = Firebase.firestore.collection("likes")
    private val userCollection = Firebase.firestore.collection("users")

    override fun getRefreshKey(state: PagingState<QuerySnapshot, PostItemUiState>): QuerySnapshot? {
        return null
    }

    override suspend fun load(
        params: LoadParams<QuerySnapshot>
    ): LoadResult<QuerySnapshot, PostItemUiState> {
        return try {
            val currentPage = params.key ?: queryPostsByFollower.get().await()
            if (currentPage.isEmpty) {
                return LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }
            val lastVisiblePost = currentPage.documents[currentPage.size() - 1]
            val nextPage = queryPostsByFollower.startAfter(lastVisiblePost).get().await()
            val postDtos = currentPage.toObjects(PostDto::class.java)
            val posts = postDtos.map { postDto ->
                val likes = likeCollection.whereEqualTo("postUuid", postDto.uuid).get().await()
                    .toObjects(LikeDto::class.java)
                val writer = userCollection.document(postDto.writerUuid).get().await()
                    .toObject(UserDto::class.java)
                val meLiked = likes.any { like -> like.userUuid == currentUserId }

                PostItemUiState(
                    uuid = postDto.uuid,
                    writerName = writer!!.name,
                    writerProfileImageUrl = writer.profileImageUrl,
                    content = postDto.content,
                    imageUrl = postDto.imageUrl,
                    likeCount = likes.size,
                    meLiked = meLiked
                )
            }

            LoadResult.Page(
                data = posts,
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}