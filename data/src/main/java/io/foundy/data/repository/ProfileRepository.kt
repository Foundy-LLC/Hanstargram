package io.foundy.data.repository

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import io.foundy.data.model.FollowDto
import io.foundy.data.model.PostDto
import io.foundy.data.model.UserDto
import kotlinx.coroutines.tasks.await
import java.util.*

object ProfileRepository {
    private const val TAG = "ProfileRepository"
    private val db = Firebase.firestore
    private val currentUser = Firebase.auth.currentUser
    private val userCollection = db.collection("users")
    private val postCollection = db.collection("posts")
    private val followCollection = db.collection("followers")

    suspend fun getPostCount(uuid : String) : Long {
        return try{
            val query = postCollection.whereEqualTo("writerUuid", uuid)
            query.count().get(AggregateSource.SERVER).await().count
        } catch (e: Exception){
            return -1
        }
    }

    suspend fun getFollowerCount(uuid : String) : Long{
        return try {
            val query = followCollection.whereEqualTo("followeeUuid", uuid)
            query.count().get(AggregateSource.SERVER).await().count
        } catch (e : Exception){
            return -1
        }
    }

    suspend fun getFolloweeCount(uuid : String) : Long {
        return try {
            val query = followCollection.whereEqualTo("followerUuid", uuid)
            query.count().get(AggregateSource.SERVER).await().count
        } catch (e : Exception){
            return -1
        }
    }

    suspend fun isFollowThisUser(uuid : String): Boolean {
        return try {
            val followCheckQuery = followCollection
                .whereEqualTo("followerUuid", currentUser?.uid)
                .whereEqualTo("followeeUuid", uuid)
            return !followCheckQuery.get().await().isEmpty
        } catch (e : Exception){
            return false
        }
    }

    suspend fun doFollow(uuid : String) : Result<Unit> {
        require(currentUser != null)
        return try {
            val followUuid = UUID.randomUUID().toString()
            val followDto = FollowDto(
                uuid = followUuid,
                followerUuid = currentUser.uid,
                followeeUuid = uuid
            )
            followCollection.document(followUuid).set(followDto).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun unFollow(uuid : String) : Result<Unit> {
        require(currentUser != null)
        return try {
            val follow = followCollection
                .whereEqualTo("followerUuid", currentUser.uid)
                .whereEqualTo("followeeUuid", uuid)
                .get().await().toObjects<FollowDto>()

            if(follow.isEmpty()){
                Result.success(Unit)
            }
            else{
                followCollection.document(follow.first().uuid).delete().await()
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPostDtoList(uuid : String): MutableList<PostDto> {
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

    suspend fun getUserDto(uuid : String): UserDto {
        try {
            require(currentUser != null)

            val name : String = "Null"
            val profileImage : String = "Null"
            val userQuery = userCollection.document(uuid)
            val userSnapshot = userQuery.get().await()

            return if(userSnapshot.exists()){
                userSnapshot.toObject(UserDto::class.java)!!
            } else{
                UserDto(uuid, name, "", "", profileImage)
            }

        } catch (e: Exception) {
            Log.e(TAG, e.toString() + "프로필 읽어오기 인포 에러")
            return UserDto()
        }
    }



}