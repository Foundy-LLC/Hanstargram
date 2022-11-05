package io.foundy.hanstargram.repository

import android.graphics.Bitmap
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import io.foundy.hanstargram.repository.model.UserDto
import io.foundy.hanstargram.source.UserPagingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID

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
}