package com.wiseassblog.data.auth

import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.wiseassblog.data.defaultIfEmpty
import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.domainmodel.User
import com.wiseassblog.domain.error.SpaceNotesError
import com.wiseassblog.domain.repository.IAuthRepository
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class FirebaseAuthRepositoryImpl(val auth: FirebaseAuth = FirebaseAuth.getInstance()) : IAuthRepository {

    override suspend fun createGoogleUser(idToken: String): Result<Exception, Boolean> = runBlocking {
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential)
            //Unfortunately I could get Tasks.await() to work properly, so for now we just delay
            //for a moment to give the task time to complete :(
            delay(1000L)
            Result.build { true }
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

            val task = user.delete()

            Tasks.await(task, 5000, TimeUnit.MILLISECONDS)

            if (task.isSuccessful) Result.build { true }
            else Result.build { throw SpaceNotesError.AuthError }

        } catch (exception: Exception) {
            Result.build { throw exception }
        }

    }

    override suspend fun getCurrentUser(): Result<Exception, User?> {
        val firebaseUser = auth.currentUser

        if (firebaseUser == null) return Result.build { null }
        else return Result.build {
            User(
                    firebaseUser.uid,
                    firebaseUser.displayName ?: "",
                    firebaseUser.photoUrl.defaultIfEmpty
            )
        }
    }
}