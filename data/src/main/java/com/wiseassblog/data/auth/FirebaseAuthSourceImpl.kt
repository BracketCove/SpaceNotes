package com.wiseassblog.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.wiseassblog.data.defaultIfEmpty
import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.domainmodel.User
import com.wiseassblog.domain.repository.IAuthSource
import java.lang.Exception

class FirebaseAuthSourceImpl : IAuthSource {
    override suspend fun getCurrentUser(): Result<Exception, User?> {
        val auth = getAuth()

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

    fun getAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
}