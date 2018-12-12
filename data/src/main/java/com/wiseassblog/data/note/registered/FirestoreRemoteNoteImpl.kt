package com.wiseassblog.data.note.registered

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.wiseassblog.data.toNote
import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.domain.domainmodel.NoteTransaction
import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.error.SpaceNotesError
import com.wiseassblog.domain.repository.IRemoteNoteRepository
import java.util.concurrent.TimeUnit

private const val COLLECTION_NAME = "notes"

class FirestoreRemoteNoteImpl : IRemoteNoteRepository {
    override suspend fun synchronizeTransactions(transactions: List<NoteTransaction>): Result<Exception, Unit> {
        TODO("Currently handled in RegisteredNoteRepositoryImpl")
    }


    override suspend fun getNotes(): Result<Exception, List<Note>> {
        var reference = firestore.collection(COLLECTION_NAME)

        return try {
            val task = reference.get()

            Tasks.await(task, 1000, TimeUnit.MILLISECONDS)

            if (task.isSuccessful) resultToNoteList(task.result)
            else Result.build { throw SpaceNotesError.RemoteIOException }

        } catch (exception: Exception) {
            Result.build { throw exception }
        }
    }

    private fun resultToNoteList(result: QuerySnapshot?): Result<Exception, List<Note>> {
        val noteList = mutableListOf<Note>()

        result?.forEach { documentSnapshop ->
            noteList.add(documentSnapshop.toObject(FirebaseNote::class.java).toNote)
        }

        return Result.build {
            noteList
        }
    }


    override suspend fun getNote(id: String): Result<Exception, Note?> {
        var reference = firestore.collection(COLLECTION_NAME)
                .document(id)

        return try {
            val task = reference.get()

            Tasks.await(task, 1000, TimeUnit.MILLISECONDS)

            if (task.isSuccessful) Result.build {
                task.result?.toObject(FirebaseNote::class.java)?.toNote
            }
            else Result.build { throw SpaceNotesError.RemoteIOException }

        } catch (exception: Exception) {
            Result.build { throw exception }
        }

    }

    override suspend fun deleteNote(note: Note): Result<Exception, Unit> {
        return try {
            val task = firestore.collection(COLLECTION_NAME)
                    .document(note.creationDate)
                    .delete()

            Tasks.await(task, 1000, TimeUnit.MILLISECONDS)

            if (task.isSuccessful) Result.build { Unit }
            else Result.build { throw SpaceNotesError.RemoteIOException }

        } catch (exception: Exception) {
            Result.build { throw exception }
        }    }

    override suspend fun updateNote(note: Note): Result<Exception, Unit> {
        return try {
            val task = firestore.collection(COLLECTION_NAME)
                    .document(note.creationDate)
                    .set(note)

            Tasks.await(task, 1000, TimeUnit.MILLISECONDS)

            if (task.isSuccessful) Result.build { Unit }
            else Result.build { throw SpaceNotesError.RemoteIOException }

        } catch (exception: Exception) {
            Result.build { throw exception }
        }
    }

    //made this an extension property for convenience sake.
    val firestore: FirebaseFirestore
        get() = FirebaseFirestore.getInstance()

}

data class FirebaseNote(
        val creationDate: String?,
        val contents: String?,
        val upVotes: Int?,
        val imageurl: String?,
        val creator: String?
)