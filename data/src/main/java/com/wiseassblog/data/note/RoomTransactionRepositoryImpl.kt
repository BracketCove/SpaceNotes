package com.wiseassblog.data.note

import com.wiseassblog.domain.domainmodel.NoteTransaction
import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.repository.ITransactionRepository

class RoomTransactionRepositoryImpl(): ITransactionRepository {
    override suspend fun getTransactions(): Result<Exception, List<NoteTransaction>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun deleteTransactions(): Result<Exception, Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun updateTransactions(transaction: NoteTransaction): Result<Exception, Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}