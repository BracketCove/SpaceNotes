package com.wiseassblog.data.note

import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.repository.INoteRepository
import kotlinx.coroutines.channels.Channel

class RoomNoteRepositoryImpl : INoteRepository {
    override suspend fun updateNote(note: Note, listener: Channel<Result<Exception, Boolean>>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getNote(id: String, listener: Channel<Result<Exception, Note>>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getNotes(listener: Channel<Result<Exception, List<Note>>>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }



    override suspend fun deleteNote(id: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}