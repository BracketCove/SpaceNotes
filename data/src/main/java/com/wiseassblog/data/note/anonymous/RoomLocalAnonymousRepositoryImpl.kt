package com.wiseassblog.data.note.anonymous

import com.wiseassblog.data.toAnonymousRoomNote
import com.wiseassblog.data.toNote
import com.wiseassblog.data.toNoteListFromAnonymous
import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.error.SpaceNotesError
import com.wiseassblog.domain.repository.ILocalNoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoomLocalAnonymousRepositoryImpl(private val noteDao: AnonymousNoteDao) : ILocalNoteRepository {
    //Not to be used
    override suspend fun deleteAll(): Result<Exception, Unit> = Result.build { throw SpaceNotesError.LocalIOException }

    //Not to be used
    override suspend fun updateAll(list: List<Note>): Result<Exception, Unit> = Result.build { throw SpaceNotesError.LocalIOException }

    override suspend fun updateNote(note: Note): Result<Exception, Unit> = withContext(Dispatchers.IO) {
        val updated = noteDao.insertOrUpdateNote(note.toAnonymousRoomNote)

        when {
            //TODO verify that if nothing is updated, the resulting value will be 0
            updated == 0L -> Result.build { throw SpaceNotesError.LocalIOException }
            else -> Result.build { Unit }
        }
    }

    override suspend fun getNote(id: String): Result<Exception, Note?> = withContext(Dispatchers.IO) { Result.build { noteDao.getNoteById(id).toNote } }


    override suspend fun getNotes(): Result<Exception, List<Note>> = withContext(Dispatchers.IO) { Result.build { noteDao.getNotes().toNoteListFromAnonymous() } }


    override suspend fun deleteNote(note: Note): Result<Exception, Unit> = withContext(Dispatchers.IO) {
        noteDao.deleteNote(note.toAnonymousRoomNote)
        Result.build { Unit }
    }
}