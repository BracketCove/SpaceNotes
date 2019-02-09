package com.wiseassblog.data.note.registered

import com.wiseassblog.data.toNote
import com.wiseassblog.data.toNoteListFromRegistered
import com.wiseassblog.data.toRegisteredRoomNote
import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.repository.ILocalNoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/**
 * This datasource is used by the RegisteredNoteRepository
 */
class RoomLocalCacheImpl(private val noteDao: RegisteredNoteDao) : ILocalNoteRepository {
    override suspend fun deleteAll(): Result<Exception, Unit> = withContext(Dispatchers.IO) {
        noteDao.deleteAll()

        Result.build { Unit }
    }

    override suspend fun updateAll(list: List<Note>): Result<Exception, Unit> = withContext(Dispatchers.IO) {
        list.forEach {
            noteDao.insertOrUpdateNote(it.toRegisteredRoomNote)
        }

        Result.build { Unit }
    }

    override suspend fun updateNote(note: Note): Result<Exception, Unit> = withContext(Dispatchers.IO) {
        noteDao.insertOrUpdateNote(note.toRegisteredRoomNote)

        Result.build { Unit }
    }

    override suspend fun getNote(id: String): Result<Exception, Note?> = withContext(Dispatchers.IO) {
        Result.build { noteDao.getNoteById(id).toNote }
    }


    override suspend fun getNotes(): Result<Exception, List<Note>> = withContext(Dispatchers.IO) {
        Result.build { noteDao.getNotes().toNoteListFromRegistered() }
    }


    override suspend fun deleteNote(note: Note): Result<Exception, Unit> = withContext(Dispatchers.IO) {
        noteDao.deleteNote(note.toRegisteredRoomNote)
        Result.build { Unit }
    }
}


