package io.foundy.data.repository

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object AuthRepository {

    fun isSignedIn(): Boolean {
        return Firebase.auth.currentUser != null
    }

    val currentUserUuid: String? = Firebase.auth.currentUser?.uid

    suspend fun signUp(email: String, password: String): Result<Unit> {
        return try {
            Firebase.auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            Firebase.auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    fun hasUserInfo(uuid: String, hasUserInfoCallback: (Boolean) -> Unit) {
        val db = Firebase.firestore
        db.collection("users").document(uuid).get()
            .addOnSuccessListener {
                hasUserInfoCallback(it.data != null)
            }
            .addOnFailureListener {
                hasUserInfoCallback(false)
            }
    }
}