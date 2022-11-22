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
import io.foundy.data.repository.PostRepository
import io.foundy.data.util.timeAgoString
import io.foundy.domain.model.Post
import kotlinx.coroutines.tasks.await

class PostPagingSource(
    private val getWriterUuids: suspend () -> List<String>
) : PagingSource<QuerySnapshot, Post>() {

    private val currentUserId = Firebase.auth.currentUser!!.uid
    private val likeCollection = Firebase.firestore.collection("likes")
    private val userCollection = Firebase.firestore.collection("users")
    private val queryPosts = Firebase.firestore.collection("posts")
        .orderBy("dateTime", Query.Direction.DESCENDING)
        .limit(PostRepository.PAGE_SIZE.toLong())

    override fun getRefreshKey(state: PagingState<QuerySnapshot, Post>): QuerySnapshot? {
        return null
    }

    override suspend fun load(
        params: LoadParams<QuerySnapshot>
    ): LoadResult<QuerySnapshot, Post> {
        val writerUuidList = getWriterUuids()

        return try {
            val currentPage = params.key ?: queryPosts.get().await()
            if (currentPage.isEmpty) {
                return LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }
            val lastVisiblePost = currentPage.documents[currentPage.size() - 1]
            val nextPage = queryPosts.startAfter(lastVisiblePost).get().await()
            val postDtos = currentPage.toObjects(PostDto::class.java)
            val posts = postDtos
                // TODO(민성): 쓸데없이 모든 포스트를 요청하고나서 필터링을 하고 있다. 보이지 않을 게시물까지 로드하여 낭비가
                //  크기 때문에 차라리 한 번에 모든 게시글을 가져온 뒤 차례로 보이는 게 나을 수 있다.
                .filter { postDto ->
                    writerUuidList.contains(postDto.writerUuid)
                }
                .map { postDto ->
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