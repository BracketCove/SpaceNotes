package com.wiseassblog.domain.interactor

import com.wiseassblog.domain.ServiceLocator
import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.domain.domainmodel.Result
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.nio.channels.Channels


class PrivateNoteSource {
    suspend fun getNotes(locator: ServiceLocator): Result<Exception, List<Note>> = coroutineScope {

        val localResult = async(Dispatchers.IO) {
            locator.local.getNotes()
        }

        locator.remote.getNotes()


        localResult.await()
    }

    suspend fun getNoteById(id: String,
                            locator: ServiceLocator): Result<Exception, Note?> = coroutineScope {

        val localResult = async(Dispatchers.IO) {
            locator.local.getNote(id)
        }

        localResult.await()
    }

    suspend fun updateNote(note: Note,
                           locator: ServiceLocator): Result<Exception, Boolean> = coroutineScope {
        val localResult = async(Dispatchers.IO) {
            locator.local.updateNote(note)
        }

        localResult.await()
    }

    suspend fun deleteNote(note: Note,
                           locator: ServiceLocator): Result<Exception, Boolean> = coroutineScope {
        val localResult = async(Dispatchers.IO) {
            locator.local.deleteNote(note)
        }

        localResult.await()

    }
}
