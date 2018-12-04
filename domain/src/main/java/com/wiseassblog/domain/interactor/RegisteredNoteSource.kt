package com.wiseassblog.domain.interactor

import com.wiseassblog.domain.DispatcherProvider
import com.wiseassblog.domain.ServiceLocator
import com.wiseassblog.domain.domainmodel.*
import com.wiseassblog.domain.repository.IRemoteNoteRepository
import com.wiseassblog.domain.repository.ITransactionRepository
import kotlinx.coroutines.*


class RegisteredNoteSource {
    suspend fun getNotes(locator: ServiceLocator,
                         dispatcher: DispatcherProvider): Result<Exception, List<Note>> = runBlocking {

        val notesResult = async(dispatcher.provideIOContext()) {
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

                locator.remoteReg.getNotes()
        }


        notesResult.await()
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
                            dispatcher: DispatcherProvider): Result<Exception, Note?> = coroutineScope {

        val noteResult = async(dispatcher.provideIOContext()) {
            locator.remoteReg.getNote(id)
        }

        noteResult.await()
    }

    suspend fun updateNote(note: Note,
                           locator: ServiceLocator,
                           dispatcher: DispatcherProvider): Result<Exception, Boolean> = coroutineScope {
        val updateResult = async(dispatcher.provideIOContext()) {
            val remoteResult = locator.remoteReg.updateNote(note)

            if (remoteResult is Result.Value) return@async remoteResult
            else return@async locator.transactionReg.updateTransactions(
                    note.toTransaction(TransactionType.UPDATE)
            )
        }

        updateResult.await()
    }

    suspend fun deleteNote(note: Note,
                           locator: ServiceLocator,
                           dispatcher: DispatcherProvider): Result<Exception, Boolean> = coroutineScope {
        val deleteResult = async(dispatcher.provideIOContext()) {
            val remoteResult = locator.remoteReg.deleteNote(note)

            if (remoteResult is Result.Value) return@async remoteResult
            else return@async locator.transactionReg.updateTransactions(
                    note.toTransaction(TransactionType.DELETE)
            )
        }

        deleteResult.await()

    }
}
