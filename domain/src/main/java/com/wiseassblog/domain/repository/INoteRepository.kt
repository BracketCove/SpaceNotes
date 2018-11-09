package com.wiseassblog.domain.repository

import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.domain.domainmodel.Result
import kotlinx.coroutines.channels.Channel

interface INoteRepository {
    suspend fun getNotes(listener: Channel<Result<Exception, List<Note>>>)

    suspend fun getNote(id: String, listener: Channel<Result<Exception, Note?>>)

    suspend fun deleteNote(note: Note, listener: Channel<Result<Exception, Boolean>>)

    suspend fun updateNote(note: Note, listener: Channel<Result<Exception, Boolean>>)
}