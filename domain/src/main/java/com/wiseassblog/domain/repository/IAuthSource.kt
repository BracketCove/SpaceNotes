package com.wiseassblog.domain.repository

import com.wiseassblog.domain.domainmodel.User
import java.lang.Exception
import com.wiseassblog.domain.domainmodel.Result


interface IAuthSource {

    suspend fun getCurrentUser(): Result<Exception, User?>

    suspend fun signOutCurrentUser(): Result<Exception, Boolean>

    suspend fun deleteCurrentUser(): Result<Exception, Boolean>

}