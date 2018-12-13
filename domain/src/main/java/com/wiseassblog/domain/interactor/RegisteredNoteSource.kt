package com.wiseassblog.domain.interactor

import com.wiseassblog.domain.DispatcherProvider
import com.wiseassblog.domain.ServiceLocator
import com.wiseassblog.domain.domainmodel.*
import com.wiseassblog.domain.repository.IRemoteNoteRepository
import com.wiseassblog.domain.repository.ITransactionRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking


class RegisteredNoteSource {
    suspend fun getNotes(locator: ServiceLocator,
                         dispatcher: DispatcherProvider): Result<Exception, List<Note>> {

        val transactionResult = locator.transactionReg.getTransactions()

        when (transactionResult) {
            is Result.Value -> {
                //if items exist in transaction cache:
                if (transactionResult.value.size != 0) synchronizeTransactionCache(
                        transactionResult.value,
                        locator.remoteReg,
                        locator.transactionReg
                )
            }

            is Result.Error -> {
                //For now we'll just continue to ask remote for the latest data
            }
        }

        return locator.remoteReg.getNotes()
    }

    private suspend fun synchronizeTransactionCache(
            transactions: List<NoteTransaction>,
            remoteReg: IRemoteNoteRepository,
            transactionReg: ITransactionRepository) {

        val synchronizationResult = remoteReg.synchronizeTransactions(transactions)

        //if synchronization was successful, delete items from the transaction cache
        when (synchronizationResult) {
            is Result.Value -> transactionReg.deleteTransactions()
            is Result.Error -> {
                //"Again, not necessarily a fatal error"
            }
        }
    }

    suspend fun getNoteById(id: String,
                            locator: ServiceLocator,
                            dispatcher: DispatcherProvider):
            Result<Exception, Note?> = locator.remoteReg.getNote(id)

    suspend fun updateNote(note: Note,
                           locator: ServiceLocator,
                           dispatcher: DispatcherProvider): Result<Exception, Unit> {
        val remoteResult = locator.remoteReg.updateNote(note)

        if (remoteResult is Result.Value) return remoteResult
        else return locator.transactionReg.updateTransactions(
                note.toTransaction(TransactionType.UPDATE)
        )
    }

    suspend fun deleteNote(note: Note,
                           locator: ServiceLocator,
                           dispatcher: DispatcherProvider): Result<Exception, Unit> {
        val remoteResult = locator.remoteReg.deleteNote(note)

        if (remoteResult is Result.Value) return remoteResult
        else return locator.transactionReg.updateTransactions(
                note.toTransaction(TransactionType.DELETE)
        )
    }
}
