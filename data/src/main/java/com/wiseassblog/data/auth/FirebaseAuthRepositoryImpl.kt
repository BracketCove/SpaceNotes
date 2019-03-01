package com.wiseassblog.data.auth

import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.wiseassblog.data.awaitTaskCompletable
import com.wiseassblog.data.defaultIfEmpty
import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.domainmodel.User
import com.wiseassblog.domain.error.SpaceNotesError
import com.wiseassblog.domain.repository.IAuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FirebaseAuthRepositoryImpl(val auth: FirebaseAuth = FirebaseAuth.getInstance()) : IAuthRepository {

    override suspend fun createGoogleUser(idToken: String):
            Result<Exception, Unit> = withContext(Dispatchers.IO) {
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            awaitTaskCompletable(auth.signInWithCredential(credential))

            Tasks.await(auth.signInWithCredential(credential))

            Result.build { Unit }
        } catch (e: Exception) {
            Result.build { throw e }
        }
    }


    override suspend fun signOutCurrentUser(): Result<Exception, Unit> {
        return Result.build {
            auth.signOut()
        }
    }

    override suspend fun deleteCurrentUser(): Result<Exception, Boolean> {
        return try {
            val user = auth.currentUser ?: throw SpaceNotesError.AuthError

            awaitTaskCompletable(user.delete())
            Result.build { true }
        } catch (exception: Exception) {
            Result.build { throw exception }
        }
    }

    override suspend fun getCurrentUser(): Result<Exception, User?> {
        val firebaseUser = auth.currentUser

        if (firebaseUser == null) {
            return Result.build { null }
        }
        else return Result.build {
            User(
                    firebaseUser.uid,
                    firebaseUser.displayName ?: "",
                    firebaseUser.photoUrl.defaultIfEmpty
            )
        }
    }
}