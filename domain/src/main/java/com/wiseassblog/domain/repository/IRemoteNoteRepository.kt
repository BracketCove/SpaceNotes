package com.wiseassblog.domain.repository

import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.domain.domainmodel.NoteTransaction
import com.wiseassblog.domain.domainmodel.Result
import kotlinx.coroutines.channels.Channel

interface IRemoteNoteRepository {
    suspend fun synchronizeTransactions(transactions: List<NoteTransaction>): Result<Exception, Boolean>

    suspend fun getNotes():Result<Exception, List<Note>>

    suspend fun getNote(id: String): Result<Exception, Note?>

    suspend fun deleteNote(note: Note): Result<Exception, Boolean>

    suspend fun updateNote(note: Note):Result<Exception, Boolean>
}