package com.wiseassblog.domain.servicelocator

import com.wiseassblog.domain.repository.ILocalNoteRepository
import com.wiseassblog.domain.repository.IPublicNoteRepository
import com.wiseassblog.domain.repository.IRemoteNoteRepository
import com.wiseassblog.domain.repository.ITransactionRepository

class NoteServiceLocator(val localAnon: ILocalNoteRepository,
                         val remoteReg: IRemoteNoteRepository,
                         val transactionReg: ITransactionRepository,
                         val remotePublic: IPublicNoteRepository)