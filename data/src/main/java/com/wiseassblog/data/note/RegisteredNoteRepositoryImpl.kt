package com.wiseassblog.data.note

import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.domain.domainmodel.NoteTransaction
import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.repository.ILocalNoteRepository
import com.wiseassblog.domain.repository.IRemoteNoteRepository

class RegisteredNoteRepositoryImpl(val remote: IRemoteNoteRepository,
                                   val cache: ILocalNoteRepository) : IRemoteNoteRepository {
    override suspend fun synchronizeTransactions(transactions: List<NoteTransaction>): Result<Exception, Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

    override suspend fun deleteNote(note: Note): Result<Exception, Boolean> = remote.deleteNote(
            note
    )


    override suspend fun updateNote(note: Note): Result<Exception, Boolean> = remote.updateNote(
            note
    )
}