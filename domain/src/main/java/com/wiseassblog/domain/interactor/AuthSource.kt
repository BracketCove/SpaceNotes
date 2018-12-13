package com.wiseassblog.domain.interactor

import com.wiseassblog.domain.ServiceLocator
import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.domainmodel.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.runBlocking

class AuthSource {

    suspend fun getCurrentUser(locator: ServiceLocator):
            Result<Exception, User?> = locator.authRepository.getCurrentUser()

    suspend fun deleteCurrentUser(locator: ServiceLocator):
            Result<Exception, Boolean> = locator.authRepository.deleteCurrentUser()

    suspend fun signOutCurrentUser(locator: ServiceLocator):
            Result<Exception, Unit> = locator.authRepository.signOutCurrentUser()

    suspend fun createGoogleUser(idToken: String, locator: ServiceLocator):
            Result<Exception, Unit> = locator.authRepository.createGoogleUser(idToken)

}