package com.wiseassblog.data.note

import androidx.room.*
import com.wiseassblog.data.toNote
import com.wiseassblog.data.toNoteList
import com.wiseassblog.data.toRoomNote
import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.error.SpaceNotesError
import com.wiseassblog.domain.repository.INoteRepository


class RoomNoteRepositoryImpl(private val noteDao: RoomNoteDao) : INoteRepository {

    override suspend fun updateNote(note: Note): Result<Exception, Boolean> {
        val updated = noteDao.insertOrUpdateNote(note.toRoomNote)

        return when {
            //TODO verify that if nothing is updated, the resulting value will be 0
            updated == 0L -> Result.build { throw SpaceNotesError.LocalIOException }
            else -> Result.build { true }
        }
    }

    override suspend fun getNote(id: String) : Result<Exception, Note?> = Result.build { noteDao.getNoteById(id).toNote }


    override suspend fun getNotes(): Result<Exception, List<Note>> = Result.build { noteDao.getNotes().toNoteList() }



    override suspend fun deleteNote(note: Note) : Result<Exception, Boolean>{
        noteDao.deleteNote(note.toRoomNote)
        return Result.build { true }
    }
}

//If you're Data Models for a given API require API specific code, then create a separate Data
//Model instead of polluting your domain with platform specific APIs.
@Entity(
        tableName = "local_notes",
        indices = [Index("creation_date")]
)
data class RoomNote(

        @PrimaryKey
        @ColumnInfo(name = "creation_date")
        val creationDate: String,

        @ColumnInfo(name = "contents")
        val contents: String,

        @ColumnInfo(name = "up_votes")
        val upVotes: Int,

        @ColumnInfo(name = "image_url")
        val imageUrl: String,

        @ColumnInfo(name = "creatorId")
        val creatorId: String
)