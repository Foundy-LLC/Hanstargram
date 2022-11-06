package io.foundy.hanstargram.repository

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.foundy.hanstargram.view.profile.ProfileInfoUiState
import io.foundy.hanstargram.repository.model.PostDto
import io.foundy.hanstargram.view.profile.ProfileUiState
import kotlinx.coroutines.tasks.await

object ProfileRepository {
    private const val TAG = "ProfileRepository"
    private val db = Firebase.firestore
    private val currentUser = Firebase.auth.currentUser
    private val userCollection = db.collection("users")
    private val postCollection = db.collection("posts")
    private val followCollection = db.collection("followers")

    private suspend fun getPosts(uuid : String) : Long {
        val query = postCollection.whereEqualTo("writerUuid", uuid)
        return query.count().get(AggregateSource.SERVER).await().count
    }

    private suspend fun getFollower(uuid : String) : Long{
        val query = followCollection.whereEqualTo("followerUuid", uuid)
        return query.count().get(AggregateSource.SERVER).await().count
    }

    private suspend fun getFollowee(uuid : String): Long {
        val query = followCollection.whereEqualTo("followeeUuid", uuid)
        return query.count().get(AggregateSource.SERVER).await().count
    }

    private suspend fun getProfilePost(uuid : String): MutableList<PostDto> {
        val postList : MutableList<PostDto> = mutableListOf()

        try {
            require(currentUser != null)

            val postQuery = postCollection.whereEqualTo("writerUuid", uuid)
            val postSnapshot = postQuery.get().await()

            if(postSnapshot.isEmpty){
                return postList
            }

            postSnapshot.documents.map {
                val postDto = it.toObject(PostDto::class.java)
                postList.add(postDto!!)
            }

            return postList
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            return postList
        }
    }

    suspend fun getProfileUiState(uuid : String): ProfileUiState {
        lateinit var profileUiState : ProfileUiState

        try {
            require(currentUser != null)

            var name : String = "Null"
            var introduce : String = "Null"
            var profileImage : String = "Null"

            userCollection.document(uuid).get()
                .addOnSuccessListener { instance ->
                    name = instance["name"].toString()
                    introduce = instance["uuid"].toString() // ToDo DB에 자기소개 추가
                    profileImage = instance["profileImageUrl"].toString()
                }
                .addOnFailureListener { e ->
                    name = "Failed $e"
                    introduce = "Failed $e"
                    profileImage = "Failed $e"
                }
            .await()

            profileUiState = ProfileUiState(
                ProfileInfoUiState(
                    name = name,
                    introduce = introduce,
                    profileImg = profileImage,
                    post = getPosts(uuid),
                    follower = getFollower(uuid),
                    followee = getFollowee(uuid),
                    isMine = (currentUser.uid == uuid)
                ),
                getProfilePost(uuid),
                "wht"
            )

            Log.d(TAG, profileUiState.toString())
            return profileUiState
        } catch (e: Exception) {
            Log.e(TAG, e.toString() + "프로필 읽어오기 인포 에러")
            return ProfileUiState()
        }
    }



}