package io.foundy.hanstargram.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.foundy.hanstargram.repository.model.FollowDto
import io.foundy.hanstargram.source.PostPagingSource
import io.foundy.hanstargram.view.home.postlist.PostItemUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.tasks.await

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
            val queryPostsByFollower = postCollection.whereIn("writerUuid", followeeUuids)

            return Pager(PagingConfig(pageSize = 20)) {
                PostPagingSource(queryPostsByFollower = queryPostsByFollower)
            }.flow
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}