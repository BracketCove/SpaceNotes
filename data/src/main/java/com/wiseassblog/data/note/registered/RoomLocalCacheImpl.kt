package com.wiseassblog.data.note.registered

import com.wiseassblog.data.*
import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.error.SpaceNotesError
import com.wiseassblog.domain.repository.ILocalNoteRepository


/**
 * This datasource is used by the RegisteredNoteRepository
 */
class RoomLocalCacheImpl(private val noteDao: RegisteredNoteDao) : ILocalNoteRepository {
    override suspend fun deleteAll(): Result<Exception, Unit> {
        noteDao.deleteAll()

        return Result.build { Unit }
    }

    override suspend fun updateAll(list: List<Note>): Result<Exception, Unit> {
        list.forEach {
            noteDao.insertOrUpdateNote(it.toRegisteredRoomNote)
        }

        return Result.build { Unit }
    }

    override suspend fun updateNote(note: Note): Result<Exception, Unit> {
        val updated = noteDao.insertOrUpdateNote(note.toRegisteredRoomNote)

        return when {
            //TODO verify that if nothing is updated, the resulting value will be 0
            updated == 0L -> Result.build { throw SpaceNotesError.LocalIOException }
            else -> Result.build { Unit }
        }
    }

    override suspend fun getNote(id: String): Result<Exception, Note?> = Result.build { noteDao.getNoteById(id).toNote }


    override suspend fun getNotes(): Result<Exception, List<Note>> = Result.build { noteDao.getNotes().toNoteListFromRegistered() }


    override suspend fun deleteNote(note: Note): Result<Exception, Unit> {
        noteDao.deleteNote(note.toRegisteredRoomNote)
        return Result.build { Unit }
    }
}


