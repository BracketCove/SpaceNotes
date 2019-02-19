package com.wiseassblog.data.note.registered

import androidx.room.*
import com.wiseassblog.data.datamodels.RegisteredRoomNote

@Dao
interface RegisteredNoteDao {
    @Query("SELECT * FROM registered_notes ORDER BY creation_date")
    fun getNotes(): List<RegisteredRoomNote>

    @Query("SELECT * FROM registered_notes WHERE creation_date = :creationDate ORDER BY creation_date")
    fun getNoteById(creationDate: String): RegisteredRoomNote

    @Delete
    fun deleteNote(note: RegisteredRoomNote)

    @Query("DELETE FROM registered_notes")
    fun deleteAll()

    //if update successful, will return number of rows effected, which should be 1
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateNote(note: RegisteredRoomNote): Long
}