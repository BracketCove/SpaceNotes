package com.wiseassblog.domain

import com.wiseassblog.domain.repository.ILocalNoteRepository
import com.wiseassblog.domain.repository.IAuthRepository
import com.wiseassblog.domain.repository.IRemoteNoteRepository
import com.wiseassblog.domain.repository.ITransactionRepository

class ServiceLocator(val localAnon: ILocalNoteRepository,
                     val remoteReg: IRemoteNoteRepository,
                     val transactionReg: ITransactionRepository,
                     val authRepository: IAuthRepository)