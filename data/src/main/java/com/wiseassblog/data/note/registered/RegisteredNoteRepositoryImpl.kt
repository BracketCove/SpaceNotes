package com.wiseassblog.data.note.registered

import com.wiseassblog.data.toNote
import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.domain.domainmodel.NoteTransaction
import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.domainmodel.TransactionType
import com.wiseassblog.domain.error.SpaceNotesError
import com.wiseassblog.domain.repository.ILocalNoteRepository
import com.wiseassblog.domain.repository.IRemoteNoteRepository

class RegisteredNoteRepositoryImpl(val remote: IRemoteNoteRepository,
                                   val cache: ILocalNoteRepository) : IRemoteNoteRepository {
    /**
     * Since n number of transactions may need to be pushed to the remote, and may not all
     * be successful, it's rather tricky to return a specific result. I figured that the next
     * best thing would be to return an error if any of them fail, to at least inform the
     * user that something didn't go as planned.
     */
    override suspend fun synchronizeTransactions(transactions: List<NoteTransaction>): Result<Exception, Unit> {

        //track results
        val resultList = mutableListOf<Result<Exception, Unit>>()

        transactions.forEach {
            if (it.transactionType == TransactionType.UPDATE) remote.updateNote(it.toNote)
                    .also { updateResult ->
                        resultList.add(updateResult)
                    }
            else remote.deleteNote(it.toNote).also { deleteResult ->
                resultList.add(deleteResult)
            }
        }

        var successful = true

        //if any result was an error, throw a generic error
        resultList.forEach {
            if (it is Result.Error) successful = false
        }

        if (successful) return Result.build { Unit }
        else return Result.build { throw SpaceNotesError.RemoteIOException }

    }


    override suspend fun getNotes(): Result<Exception, List<Note>> {
        val remoteResult = remote.getNotes()

        when (remoteResult) {
            is Result.Value -> {
                cache.deleteAll()
                cache.updateAll(remoteResult.value)
            }

            is Result.Error -> {
                return cache.getNotes()
            }
        }

        return remoteResult
    }

    override suspend fun getNote(id: String): Result<Exception, Note?> {
        val remoteResult = remote.getNote(id)

        return if (remoteResult is Result.Error) cache.getNote(id)
        else remoteResult
    }

    override suspend fun deleteNote(note: Note): Result<Exception, Unit> = remote.deleteNote(
            note
    )


    override suspend fun updateNote(note: Note): Result<Exception, Unit> = remote.updateNote(
            note
    )
}