package com.wiseassblog.data.note.anonymous

import com.wiseassblog.data.toNote
import com.wiseassblog.data.toNoteListFromAnonymous
import com.wiseassblog.data.toAnonymousRoomNote
import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.error.SpaceNotesError
import com.wiseassblog.domain.repository.ILocalNoteRepository

class RoomLocalAnonymousRepositoryImpl(private val noteDao: AnonymousNoteDao) : ILocalNoteRepository {
    override suspend fun deleteAll(): Result<Exception, Unit> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun updateAll(list: List<Note>): Result<Exception, Unit> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun updateNote(note: Note): Result<Exception, Unit> {
        val updated = noteDao.insertOrUpdateNote(note.toAnonymousRoomNote)

        return when {
            //TODO verify that if nothing is updated, the resulting value will be 0
            updated == 0L -> Result.build { throw SpaceNotesError.LocalIOException }
            else -> Result.build { Unit }
        }
    }

    override suspend fun getNote(id: String): Result<Exception, Note?> = Result.build { noteDao.getNoteById(id).toNote }


    override suspend fun getNotes(): Result<Exception, List<Note>> = Result.build { noteDao.getNotes().toNoteListFromAnonymous() }


    override suspend fun deleteNote(note: Note): Result<Exception, Unit> {
        noteDao.deleteNote(note.toAnonymousRoomNote)
        return Result.build { Unit }
    }
}