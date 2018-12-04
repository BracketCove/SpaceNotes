package com.wiseassblog.domain.repository

import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.domainmodel.NoteTransaction

interface ITransactionRepository {
    suspend fun getTransactions():Result<Exception, List<NoteTransaction>>

    suspend fun deleteTransactions(): Result<Exception, Boolean>

    suspend fun updateTransactions(transaction: NoteTransaction):Result<Exception, Boolean>
}