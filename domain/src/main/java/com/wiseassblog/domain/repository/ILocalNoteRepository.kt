package com.wiseassblog.domain.repository

import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.domain.domainmodel.Result

interface ILocalNoteRepository {
    suspend fun getNotes(): Result<Exception, List<Note>>

    suspend fun getNote(id: String): Result<Exception, Note?>

    suspend fun deleteNote(note: Note): Result<Exception, Unit>

    suspend fun deleteAll(): Result<Exception, Unit>

    suspend fun updateAll(list: List<Note>): Result<Exception, Unit>

    suspend fun updateNote(note: Note): Result<Exception, Unit>
}