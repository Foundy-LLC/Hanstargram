package io.foundy.data.repository

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.foundy.data.model.PostDto
import io.foundy.data.model.UserDto
import kotlinx.coroutines.tasks.await

object ProfileRepository {
    private const val TAG = "ProfileRepository"
    private val db = Firebase.firestore
    private val currentUser = Firebase.auth.currentUser
    private val userCollection = db.collection("users")
    private val postCollection = db.collection("posts")
    private val followCollection = db.collection("followers")

    suspend fun getPostCount(uuid : String) : Long {
        val query = postCollection.whereEqualTo("writerUuid", uuid)
        return query.count().get(AggregateSource.SERVER).await().count
    }

    suspend fun getFollowerCount(uuid : String) : Long{
        val query = followCollection.whereEqualTo("followerUuid", uuid)
        return query.count().get(AggregateSource.SERVER).await().count
    }

    suspend fun getFolloweeCount(uuid : String): Long {
        val query = followCollection.whereEqualTo("followeeUuid", uuid)
        return query.count().get(AggregateSource.SERVER).await().count
    }

    private suspend fun getPostDtoList(uuid : String): MutableList<PostDto> {
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