package io.foundy.data.repository

import android.graphics.Bitmap
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import io.foundy.data.model.FollowDto
import io.foundy.data.model.UserDto
import io.foundy.data.source.UserPagingSource
import io.foundy.domain.model.UserDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.*

object UserRepository {

    private const val PAGE_SIZE = 30

    suspend fun saveInitUserInfo(
        name: String,
        profileImage: Bitmap?
    ): Result<Unit> {
        val user = Firebase.auth.currentUser
        require(user != null)
        val userDto = UserDto(
            uuid = user.uid,
            name = name,
            email = user.email,
            introduce = "${name}님의 자소소개 입니다."
        )

        val userReference = Firebase.firestore.collection("users").document(user.uid)

        try {
            userReference.set(userDto).await()
        } catch (e: Exception) {
            return Result.failure(e)
        }

        if (profileImage == null) {
            return Result.success(Unit)
        }

        val uuid = UUID.randomUUID().toString()
        val imageUrl = "${uuid}.png"
        val imageReference = Firebase.storage.reference.child(imageUrl)
        val byteArrayOutputStream = ByteArrayOutputStream()

        profileImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val data = byteArrayOutputStream.toByteArray()

        try {
            imageReference.putBytes(data).await()
        } catch (e: Exception) {
            return Result.failure(e)
        }

        val newUserDto = userDto.copy(profileImageUrl = imageUrl)

        try {
            userReference.set(newUserDto).await()
        } catch (e: Exception) {
            return Result.failure(e)
        }

        return Result.success(Unit)
    }

    fun searchUser(name: String): Flow<PagingData<UserDto>> {
        val userCollection = Firebase.firestore.collection("users")
        val queryUsersByName = if (name.isEmpty()) {
            // 검색어가 빈 경우 전체 목록을 보인다.
            userCollection
                .orderBy("name")
                .limit(PAGE_SIZE.toLong())
        } else {
            userCollection
                .orderBy("name")
                .startAt(name)
                .endAt(name + "\uf8ff")
                .limit(PAGE_SIZE.toLong())
        }

        return Pager(PagingConfig(pageSize = PAGE_SIZE)) {
            UserPagingSource(queryUsersByName = queryUsersByName)
        }.flow
    }

    suspend fun follow(targetUserUuid: String): Result<Unit> {
        val currentUser = Firebase.auth.currentUser
        val followCollection = Firebase.firestore.collection("followers")
        check(currentUser != null)

        try {
            val alreadyFollowing = !followCollection
                .whereEqualTo("followerUuid", currentUser.uid)
                .whereEqualTo("followeeUuid", targetUserUuid)
                .get().await().isEmpty
            if (alreadyFollowing) {
                throw IllegalStateException("이미 팔로우 중인 상대를 팔로우 하였습니다.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure(e)
        }

        val followDto = FollowDto(
            uuid = UUID.randomUUID().toString(),
            followeeUuid = targetUserUuid,
            followerUuid = currentUser.uid
        )
        return try {
            followCollection.document(followDto.uuid).set(followDto).await()
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun unfollow(targetUserUuid: String): Result<Unit> {
        val currentUser = Firebase.auth.currentUser
        val followCollection = Firebase.firestore.collection("followers")
        check(currentUser != null)

        return try {
            val followDto = followCollection
                .whereEqualTo("followerUuid", currentUser.uid)
                .whereEqualTo("followeeUuid", targetUserUuid)
                .get().await().first().toObject(FollowDto::class.java)

            followCollection.document(followDto.uuid).delete()
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun getUserDetail(userUuid: String): Result<UserDetail> {
        val db = Firebase.firestore
        val currentUser = Firebase.auth.currentUser
        val userCollection = db.collection("users")
        val postCollection = db.collection("posts")
        val followCollection = db.collection("followers")
        check(currentUser != null)

        try {
            val userDto = userCollection.document(userUuid)
                .get().await().toObject(UserDto::class.java)!!
            val postCount = postCollection.whereEqualTo("writerUuid", userUuid).count()
                .get(AggregateSource.SERVER).await().count
            val followersCount = followCollection.whereEqualTo("followeeUuid", userUuid)
                .count().get(AggregateSource.SERVER).await().count
            val followingCount = followCollection.whereEqualTo("followerUuid", userUuid)
                .count().get(AggregateSource.SERVER).await().count
            val isCurrentUserFollowing = !followCollection
                .whereEqualTo("followerUuid", currentUser.uid)
                .whereEqualTo("followeeUuid", userUuid)
                .get().await().isEmpty

            return Result.success(
                UserDetail(
                    uuid = userDto.uuid,
                    name = userDto.name,
                    email = userDto.email,
                    introduce = userDto.introduce,
                    profileImageUrl = userDto.profileImageUrl,
                    postCount = postCount,
                    followersCount = followersCount,
                    followingCount = followingCount,
                    isCurrentUserFollowing = isCurrentUserFollowing,
                    isMe = userUuid == currentUser.uid
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure(e)
        }
    }
}
