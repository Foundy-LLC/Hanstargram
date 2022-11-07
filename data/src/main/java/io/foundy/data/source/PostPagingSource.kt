package io.foundy.data.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.foundy.data.model.LikeDto
import io.foundy.data.model.PostDto
import io.foundy.data.model.UserDto
import io.foundy.data.util.timeAgoString
import io.foundy.domain.model.Post
import kotlinx.coroutines.tasks.await

class PostPagingSource(
    private val queryPostsByFollower: Query
) : PagingSource<QuerySnapshot, Post>() {

    private val currentUserId = Firebase.auth.currentUser!!.uid
    private val likeCollection = Firebase.firestore.collection("likes")
    private val userCollection = Firebase.firestore.collection("users")

    override fun getRefreshKey(state: PagingState<QuerySnapshot, Post>): QuerySnapshot? {
        return null
    }

    override suspend fun load(
        params: LoadParams<QuerySnapshot>
    ): LoadResult<QuerySnapshot, Post> {
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

                Post(
                    uuid = postDto.uuid,
                    writerUuid = writer!!.uuid,
                    writerName = writer.name,
                    writerProfileImageUrl = writer.profileImageUrl,
                    content = postDto.content,
                    imageUrl = postDto.imageUrl,
                    likeCount = likes.size,
                    meLiked = meLiked,
                    isMine = postDto.writerUuid == currentUserId,
                    timeAgo = postDto.dateTime.timeAgoString()
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