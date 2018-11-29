package com.wiseassblog.domain

import com.wiseassblog.domain.repository.INoteRepository
import com.wiseassblog.domain.repository.IAuthRepository

class ServiceLocator(val localAnon: INoteRepository,
                     val remoteReg: INoteRepository,
                     val cacheReg: INoteRepository,
                     val authRepository: IAuthRepository)