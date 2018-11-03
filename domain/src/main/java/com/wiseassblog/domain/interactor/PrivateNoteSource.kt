package com.wiseassblog.domain.interactor

import com.wiseassblog.domain.ServiceLocator
import com.wiseassblog.domain.domainmodel.ColorType
import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.domainmodel.User
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay


class PrivateNoteSource {
    suspend fun getNotes(locator: ServiceLocator): Result<Exception, List<Note>> {

        delay(2000L)

        val note = Note("28/10/2018",
                "When I understand that this glass is already broken, every moment with it becomes precious.",
                0,
                ColorType.GREEN,
                User(
                        "8675309",
                        "Ajahn Chah",
                        ""
                )
        )


        return Result.build { listOf(note, note, note) }
    }

    suspend fun getNoteById(id: String,
                            locator: ServiceLocator): Result<Exception, Note> {


        return Result.build {
            Note("28/10/2018",
                    "When I understand that this glass is already broken, every moment with it becomes precious.",
                    0,
                    ColorType.GREEN,
                    User(
                            "8675309",
                            "Ajahn Chah",
                            ""
                    )
            )
        }
    }

    suspend fun updateNote(note: Note,
                           locator: ServiceLocator): Result<Exception, Boolean> {
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
                           locator: ServiceLocator): Result<Exception, Boolean> {
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
