package com.wiseassblog.data.transaction

import com.wiseassblog.data.note.registered.RegisteredTransactionDao
import com.wiseassblog.data.toNoteTransactionListFromRegistered
import com.wiseassblog.data.toRegisteredRoomTransaction
import com.wiseassblog.domain.domainmodel.NoteTransaction
import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.repository.ITransactionRepository

class RoomTransactionRepositoryImpl(val transactionDao: RegisteredTransactionDao) : ITransactionRepository {
    override suspend fun getTransactions(): Result<Exception, List<NoteTransaction>> = Result.build {
        transactionDao.getTransactions().toNoteTransactionListFromRegistered()
    }

    override suspend fun deleteTransactions(): Result<Exception, Unit> = Result.build {
        transactionDao.deleteAll()
    }

    override suspend fun updateTransactions(transaction: NoteTransaction): Result<Exception, Unit> = Result.build {
        transactionDao.insertOrUpdateTransaction(transaction.toRegisteredRoomTransaction).toUnit()
    }

    private fun Long.toUnit():Unit = Unit
}