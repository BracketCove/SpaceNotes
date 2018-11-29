package com.wiseassblog.domain.interactor

import com.wiseassblog.domain.DispatcherProvider
import com.wiseassblog.domain.ServiceLocator
import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.domain.domainmodel.Result
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import java.lang.Exception

class AnonymousNoteSource {
    suspend fun getNotes(locator: ServiceLocator, dispatcher: DispatcherProvider):
            Result<Exception, List<Note>> = runBlocking {

        val localResult = async(dispatcher.provideIOContext()) {
            locator.localAnon.getNotes()
        }

        localResult.await()
    }

    suspend fun getNoteById(id: String, locator: ServiceLocator, dispatcher: DispatcherProvider):
            Result<Exception, Note?> = runBlocking {

        val localResult = async(dispatcher.provideIOContext()) {
            locator.localAnon.getNote(id)
        }

        localResult.await()
    }

    suspend fun updateNote(note: Note, locator: ServiceLocator, dispatcher: DispatcherProvider):
            Result<Exception, Boolean> = runBlocking {
        val localResult = async(dispatcher.provideIOContext()) {
            locator.localAnon.updateNote(note)
        }

        localResult.await()
    }

    suspend fun deleteNote(note: Note, locator: ServiceLocator, dispatcher: DispatcherProvider):
            Result<Exception, Boolean> = runBlocking {
        val localResult = async(dispatcher.provideIOContext()) {
            locator.localAnon.deleteNote(note)
        }

        localResult.await()

    }


}