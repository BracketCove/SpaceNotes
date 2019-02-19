package com.wiseassblog.data.note.anonymous

import androidx.room.*
import com.wiseassblog.data.datamodels.AnonymousRoomNote

@Dao
interface AnonymousNoteDao {
    @Query("SELECT * FROM anonymous_notes ORDER BY creation_date")
    fun getNotes(): List<AnonymousRoomNote>

    @Query("SELECT * FROM anonymous_notes WHERE creation_date = :creationDate ORDER BY creation_date")
    fun getNoteById(creationDate: String): AnonymousRoomNote

    @Delete
    fun deleteNote(noteAnonymous: AnonymousRoomNote)

    @Query("DELETE FROM anonymous_notes")
    fun deleteAll()

    //if update successful, will return number of rows effected, which should be 1
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateNote(noteAnonymous: AnonymousRoomNote): Long
}