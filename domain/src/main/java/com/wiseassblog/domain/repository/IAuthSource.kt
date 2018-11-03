package com.wiseassblog.domain.repository

import com.wiseassblog.domain.domainmodel.User
import java.lang.Exception
import com.wiseassblog.domain.domainmodel.Result


interface IAuthSource {

    suspend fun getCurrentUser(): Result<Exception, User?>

}