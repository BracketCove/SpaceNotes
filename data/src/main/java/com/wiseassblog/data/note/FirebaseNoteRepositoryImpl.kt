package com.wiseassblog.data.note

import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.repository.INoteRepository
import kotlinx.coroutines.channels.Channel

class FirebaseNoteRepositoryImpl : INoteRepository {
    override suspend fun getNotes(): Result<Exception, List<Note>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getNote(id: String): Result<Exception, Note?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun deleteNote(note: Note): Result<Exception, Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun updateNote(note: Note): Result<Exception, Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}