package io.foundy.hanstargram.repository

import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object AuthRepository {

    fun isSignedIn(): Boolean {
        return Firebase.auth.currentUser != null
    }

    fun signInWith(idToken: String, onComplete: (result: Result<Any>) -> Unit) {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        Firebase.auth.signInWithCredential(firebaseCredential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onComplete(Result.success(true))
            } else {
                onComplete(Result.failure(task.exception!!))
            }
        }
    }
}