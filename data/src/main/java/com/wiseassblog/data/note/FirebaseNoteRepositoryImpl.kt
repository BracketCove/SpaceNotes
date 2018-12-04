package com.wiseassblog.data.note

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.repository.ILocalNoteRepository

class FirebaseNoteRepositoryImpl : ILocalNoteRepository {
    fun getTable(): DatabaseReference = FirebaseDatabase.getInstance().getReference("remote_notes")


    override suspend fun getNotes(): Result<Exception, List<Note>> {
        return Result.build { emptyList<Note>() }
    }

    override suspend fun getNote(id: String): Result<Exception, Note?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun deleteNote(note: Note): Result<Exception, Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun updateNote(note: Note): Result<Exception, Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

data class FirebaseNote (
        val creationDate: String,
        val contents: String,
        val upVotes: Int,
        val imageurl: String,
        val creator: String
)