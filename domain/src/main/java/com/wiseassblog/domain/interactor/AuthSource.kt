package com.wiseassblog.domain.interactor

import com.wiseassblog.domain.ServiceLocator
import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.domainmodel.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.runBlocking

class AuthSource {

    suspend fun getCurrentUser(locator: ServiceLocator): Result<Exception, User?> = runBlocking {
        val result = async(Dispatchers.IO) {
            locator.authRepository.getCurrentUser()
        }

        result.await()
    }

    suspend fun deleteCurrentUser(locator: ServiceLocator): Result<Exception, Boolean> = runBlocking {
        val result = async(Dispatchers.IO) {
            locator.authRepository.deleteCurrentUser()
        }

        result.await()
    }

    suspend fun signOutCurrentUser(locator: ServiceLocator): Result<Exception, Unit> = runBlocking {
        val result = async(Dispatchers.IO) {
            locator.authRepository.signOutCurrentUser()
        }

        result.await()

    }

    suspend fun createGoogleUser(idToken: String, locator: ServiceLocator): Result<Exception, User?> = runBlocking {
        val result = async {
            locator.authRepository.createGoogleUser(idToken)
            locator.authRepository.getCurrentUser()
        }

        result.await()
    }

}