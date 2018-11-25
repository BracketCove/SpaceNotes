package com.wiseassblog.domain.repository

import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.domainmodel.User


interface IAuthSource {

    suspend fun getCurrentUser(): Result<Exception, User?>

    suspend fun signOutCurrentUser(): Result<Exception, Unit>

    suspend fun deleteCurrentUser(): Result<Exception, Boolean>

    suspend fun createGoogleUser(idToken: String): Result<Exception, Boolean>

}