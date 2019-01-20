package com.wiseassblog.domain.interactor

import com.wiseassblog.domain.NoteServiceLocator
import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.domainmodel.User
import kotlinx.coroutines.channels.Channel


class PublicNoteSource {
    suspend fun getNotes(locator: NoteServiceLocator): Result<Exception, List<Note>> {
        val listener = Channel<Result<Exception, List<Note>>>()

//    launch {
//        remote.getNotes(listener)
//    }
//
//    launch {
//        local.getNotes(listener)
//    }

        return listener.receive()
    }

    suspend fun getNoteById(id: String,
                            locator: NoteServiceLocator): Result<Exception, Note> {


        return Result.build {
            Note("28/10/2018",
                    "When I understand that this glass is already broken, every moment with it becomes precious.",
                    0,
                    "gps_icon",
                    User(
                            "8675309",
                            "Ajahn Chah",
                            ""
                    )
            )
        }
    }

    suspend fun updateNote(note: Note,
                           locator: NoteServiceLocator): Result<Exception, Boolean> {
        val listener = Channel<Result<Exception, Boolean>>()

//    launch {
//        locator.remote.updateNote(note, listener)
//    }
//
//    launch {
//        locator.local.updateNote(note, listener)
//    }

        return listener.receive()
    }

    suspend fun deleteNote(id: String,
                           locator: NoteServiceLocator): Result<Exception, Boolean> {
        val listener = Channel<Result<Exception, Boolean>>()

//    launch {
//        locator.remote.updateNote(note, listener)
//    }
//
//    launch {
//        locator.local.updateNote(note, listener)
//    }

        return listener.receive()
    }
}
