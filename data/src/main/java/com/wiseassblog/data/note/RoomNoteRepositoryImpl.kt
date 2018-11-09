package com.wiseassblog.data.note

import androidx.room.*
import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.error.SpaceNotesError
import com.wiseassblog.domain.repository.INoteRepository
import kotlinx.coroutines.channels.Channel


class RoomNoteRepositoryImpl(private val noteDao: RoomNoteDao) : INoteRepository {

    override suspend fun updateNote(note: Note, listener: Channel<Result<Exception, Boolean>>) {
        val updated = noteDao.insertOrUpdateNote(note.toRoomNote)

        when {
            //TODO verify that if nothing is updated, the resulting value will be 0
            updated == 0L -> listener.send(Result.build { throw SpaceNotesError.LocalIOException})
            updated > 0L -> listener.send(Result.build { true })
        }
    }

    override suspend fun getNote(id: String, listener: Channel<Result<Exception, Note?>>) {
        val roomNote = noteDao.getNoteById(id)

        listener.send( Result.build { roomNote.toNote })
    }

    override suspend fun getNotes(listener: Channel<Result<Exception, List<Note>>>) {
        val roomNotes = noteDao.getNotes()

        listener.send( Result.build { roomNotes.toNoteList() })    }

    override suspend fun deleteNote(note: Note, listener: Channel<Result<Exception, Boolean>>) {
        noteDao.deleteNote(note.toRoomNote)

        listener.send(Result.build { true })
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