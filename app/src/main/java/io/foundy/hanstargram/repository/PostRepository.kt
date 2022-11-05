package io.foundy.hanstargram.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import io.foundy.hanstargram.repository.model.FollowDto
import io.foundy.hanstargram.repository.model.LikeDto
import io.foundy.hanstargram.source.PostPagingSource
import io.foundy.hanstargram.view.home.postlist.PostItemUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

object PostRepository {

    suspend fun getPostsByFollower(): Flow<PagingData<PostItemUiState>> {
        try {
            val currentUser = Firebase.auth.currentUser
            require(currentUser != null)
            val db = Firebase.firestore

            val followerCollection = db.collection("followers")
            val postCollection = db.collection("posts")

            val followerQuery = followerCollection.whereEqualTo("followerUuid", currentUser.uid)
            val followerSnapshot = followerQuery.get().await()
            if (followerSnapshot.isEmpty) {
                return emptyFlow()
            }

            val followeeUuids = followerSnapshot.documents.map {
                val followerDto = it.toObject(FollowDto::class.java)
                followerDto!!.followeeUuid
            }
            val queryPostsByFollower = postCollection
                .whereIn("writerUuid", followeeUuids)
                .orderBy("dateTime", Query.Direction.DESCENDING)

            return Pager(PagingConfig(pageSize = 20)) {
                PostPagingSource(queryPostsByFollower = queryPostsByFollower)
            }.flow
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    suspend fun toggleLike(postUuid: String): Result<Unit> {
        val currentUser = Firebase.auth.currentUser
        require(currentUser != null)
        val db = Firebase.firestore
        val likeCollection = db.collection("likes")

        return try {
            val likes = likeCollection
                .whereEqualTo("userUuid", currentUser.uid)
                .whereEqualTo("postUuid", postUuid)
                .get().await().toObjects<LikeDto>()

            if (likes.isEmpty()) {
                val likeUuid = UUID.randomUUID().toString()
                val likeDto = LikeDto(
                    uuid = likeUuid,
                    userUuid = currentUser.uid,
                    postUuid = postUuid
                )
                likeCollection.document(likeUuid).set(likeDto).await()
            } else {
                likeCollection.document(likes.first().uuid).delete().await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletePost(postUuid: String): Result<Unit> {
        val db = Firebase.firestore

        return try {
            db.collection("posts").document(postUuid).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}