package io.foundy.hanstargram.repository

import android.graphics.Bitmap
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import io.foundy.hanstargram.repository.model.UserDto
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID

object UserRepository {

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
}