package com.wiseassblog.data.note.registered

import com.wiseassblog.data.toNote
import com.wiseassblog.data.toNoteListFromRegistered
import com.wiseassblog.data.toRegisteredRoomNote
import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.error.SpaceNotesError
import com.wiseassblog.domain.repository.ILocalNoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking


/**
 * This datasource is used by the RegisteredNoteRepository
 */
class RoomLocalCacheImpl(private val noteDao: RegisteredNoteDao) : ILocalNoteRepository {
    override suspend fun deleteAll(): Result<Exception, Unit> = runBlocking(Dispatchers.IO) {
        noteDao.deleteAll()

        Result.build { Unit }
    }

    override suspend fun updateAll(list: List<Note>): Result<Exception, Unit> = runBlocking(Dispatchers.IO) {
        list.forEach {
            noteDao.insertOrUpdateNote(it.toRegisteredRoomNote)
        }

        Result.build { Unit }
    }

    override suspend fun updateNote(note: Note): Result<Exception, Unit> = runBlocking(Dispatchers.IO) {
        val updated = noteDao.insertOrUpdateNote(note.toRegisteredRoomNote)

        when {
            //TODO verify that if nothing is updated, the resulting value will be 0
            updated == 0L -> Result.build { throw SpaceNotesError.LocalIOException }
            else -> Result.build { Unit }
        }
    }

    override suspend fun getNote(id: String): Result<Exception, Note?> = runBlocking(Dispatchers.IO) {
        Result.build { noteDao.getNoteById(id).toNote }
    }


    override suspend fun getNotes(): Result<Exception, List<Note>> = runBlocking(Dispatchers.IO) {
        Result.build { noteDao.getNotes().toNoteListFromRegistered() }
    }


    override suspend fun deleteNote(note: Note): Result<Exception, Unit> = runBlocking(Dispatchers.IO) {
        noteDao.deleteNote(note.toRegisteredRoomNote)
        Result.build { Unit }
    }
}


