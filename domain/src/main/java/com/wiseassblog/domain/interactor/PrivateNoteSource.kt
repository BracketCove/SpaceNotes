package com.wiseassblog.domain.interactor

import com.wiseassblog.domain.ServiceLocator
import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.domain.domainmodel.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withContext


class PrivateNoteSource {
    suspend fun getNotes(locator: ServiceLocator): Result<Exception, List<Note>> {

        val listener = Channel<Result<Exception, List<Note>>>()

        withContext(Dispatchers.IO) {
            locator.local.getNotes(listener)
        }

        return listener.receive()
    }

    suspend fun getNoteById(id: String,
                            locator: ServiceLocator): Result<Exception, Note?> {

        val listener = Channel<Result<Exception, Note?>>()

        withContext(Dispatchers.IO) {
            locator.local.getNote(id, listener)
        }


        return listener.receive()
    }

    suspend fun updateNote(note: Note,
                           locator: ServiceLocator): Result<Exception, Boolean> {
        val listener = Channel<Result<Exception, Boolean>>()

//        locator.remote.updateNote(note, listener)
        withContext(Dispatchers.IO) {
            locator.local.updateNote(note, listener)
        }


        return listener.receive()
    }

    suspend fun deleteNote(note: Note,
                           locator: ServiceLocator): Result<Exception, Boolean> {
        val listener = Channel<Result<Exception, Boolean>>()

        withContext(Dispatchers.IO) {
            locator.local.deleteNote(note, listener)
        }


        return listener.receive()
    }
}
