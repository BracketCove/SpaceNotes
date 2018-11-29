package com.wiseassblog.data.auth

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.wiseassblog.data.defaultIfEmpty
import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.domainmodel.User
import com.wiseassblog.domain.error.SpaceNotesError
import com.wiseassblog.domain.repository.IAuthRepository
import java.util.concurrent.TimeUnit

class FirebaseAuthRepositoryImpl : IAuthRepository {
    override suspend fun createGoogleUser(idToken: String): Result<Exception, Boolean> {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        return try {
            val task = auth.signInWithCredential(credential)

            Tasks.await(task, 1000, TimeUnit.MILLISECONDS)

            if (task.isSuccessful) Result.build { true }
            else Result.build { throw SpaceNotesError.AuthError }
        } catch (exception: Exception) {
            Result.build { throw exception }

        }
    }

    override suspend fun signOutCurrentUser(): Result<Exception, Unit> {
        return Result.build { auth.signOut() }
    }

    override suspend fun deleteCurrentUser(): Result<Exception, Boolean> {

        return try {
            val user = auth.currentUser ?: throw SpaceNotesError.AuthError

            val task = user.delete()

            Tasks.await(task, 1000, TimeUnit.MILLISECONDS)

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


    //made this an extension property for convenience sake.
    val auth: FirebaseAuth
        get() = FirebaseAuth.getInstance()

}