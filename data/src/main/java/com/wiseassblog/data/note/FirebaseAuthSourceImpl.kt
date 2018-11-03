package com.wiseassblog.data.note

import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.domainmodel.User
import com.wiseassblog.domain.repository.IAuthSource
import java.lang.Exception

class FirebaseAuthSourceImpl: IAuthSource {
    override suspend fun getCurrentUser(): Result<Exception, User?> {
       return Result.build { null }
    }

}