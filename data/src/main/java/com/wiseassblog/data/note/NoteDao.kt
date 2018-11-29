package com.wiseassblog.data.note

import androidx.room.*
import com.wiseassblog.data.entities.RoomNote

@Dao
interface RoomNoteDao {
    @Query("SELECT * FROM local_notes ORDER BY creation_date")
    fun getNotes(): List<RoomNote>

    @Query("SELECT * FROM local_notes WHERE creation_date = :creationDate ORDER BY creation_date")
    fun getNoteById(creationDate: String): RoomNote

    @Delete
    fun deleteNote(note: RoomNote)

    //if update successful, will return number of rows effected, which should be 1
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateNote(note: RoomNote): Long
}