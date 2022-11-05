package io.foundy.hanstargram.repository

import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import io.foundy.hanstargram.view.profile.ProfileInfoUiState
import io.foundy.hanstargram.view.profile.ProfilePostUiState
import kotlinx.coroutines.tasks.await

object ProfileRepository {
    suspend fun getProfileInfo(uuid : String): ProfileInfoUiState {
        try {
            val currentUser = Firebase.auth.currentUser
            require(currentUser != null)
            val db = Firebase.firestore

            /* 유저 기본 정보 불러오기 */
            /* data class라서 set이 안된다??? */
            var name = ""
            var introduce = ""
            var profileImg = ""

            val userCollection = db.collection("users")
            val userDocument = userCollection.document(uuid)

            userDocument.get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val instance = it.result
                    name = instance["name"].toString()
                    introduce = instance["uuid"].toString() // ToDo DB에 자기소개 추가
                    profileImg = instance["profileImageUrl"].toString()
                }
                else {
                    name = "Failed"
                    introduce = "Failed"
                    profileImg = "Failed"
                }
            }.await()

            /* 유저 카운트 정보 불러오기 */
            var post : Long = 0
            var follower : Long = 0
            var followee : Long = 0
            var collection : CollectionReference
            var query : Query

            collection = Firebase.firestore.collection("posts")
            query = collection.whereEqualTo("writerUuid", uuid)
            query.count().get(AggregateSource.SERVER).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    post = task.result.count
                }else{
                    Log.d("ProfileRepository", "Count failed: ", task.getException())
                }
            }.await()

            collection = Firebase.firestore.collection("followers")
            query = collection.whereEqualTo("followerUuid", uuid)
            query.count().get(AggregateSource.SERVER).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    follower = task.result.count
                }else{
                    Log.d("ProfileRepository", "Count failed: ", task.getException())
                }
            }.await()

            collection = Firebase.firestore.collection("followers")
            query = collection.whereEqualTo("followeeUuid", uuid)
            query.count().get(AggregateSource.SERVER).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    followee = task.result.count
                }else{
                    Log.d("ProfileRepository", "Count failed: ", task.getException())
                }
            }.await()

            return ProfileInfoUiState(
                name = name,
                introduce = introduce,
                profileImg = profileImg,
                post = post,
                follower = follower,
                followee = followee,
                isMine = (currentUser.uid == uuid)
            )
        } catch (e: Exception) {
            return ProfileInfoUiState()
        }
    }

    fun getProfilePost(uuid : String): MutableList<ProfilePostUiState> {
        val list : MutableList<ProfilePostUiState> = mutableListOf()
        return try {
            val currentUser = Firebase.auth.currentUser
            require(currentUser != null)
            val db = Firebase.firestore
            val userCollection = db.collection("users")
            val userDocument = userCollection.document(uuid)
            list
        } catch (e: Exception) {
            list
        }
    }

}