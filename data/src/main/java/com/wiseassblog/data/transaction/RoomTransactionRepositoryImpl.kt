package com.wiseassblog.data.transaction

import com.wiseassblog.data.note.registered.RegisteredTransactionDao
import com.wiseassblog.data.toNoteTransactionListFromRegistered
import com.wiseassblog.data.toRegisteredRoomTransaction
import com.wiseassblog.domain.domainmodel.NoteTransaction
import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.repository.ITransactionRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.runBlocking

class RoomTransactionRepositoryImpl(val transactionDao: RegisteredTransactionDao) : ITransactionRepository {
    override suspend fun getTransactions():
            Result<Exception, List<NoteTransaction>> = runBlocking(IO) {
        Result.build {
            transactionDao.getTransactions().toNoteTransactionListFromRegistered()
        }
    }

    override suspend fun deleteTransactions(): Result<Exception, Unit> = runBlocking(IO) {
        Result.build {
            transactionDao.deleteAll()
        }
    }

    override suspend fun updateTransactions(transaction: NoteTransaction):
            Result<Exception, Unit> = runBlocking(IO) {
        Result.build {
            transactionDao.insertOrUpdateTransaction(
                    transaction.toRegisteredRoomTransaction
            ).toUnit()
        }
    }

    private fun Long.toUnit(): Unit = Unit
}