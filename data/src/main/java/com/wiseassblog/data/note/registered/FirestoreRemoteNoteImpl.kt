package com.wiseassblog.data.note.registered

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.wiseassblog.data.awaitTaskCompletable
import com.wiseassblog.data.awaitTaskResult
import com.wiseassblog.data.toFirebaseNote
import com.wiseassblog.data.toNote
import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.domain.domainmodel.NoteTransaction
import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.repository.IRemoteNoteRepository

private const val COLLECTION_NAME = "notes"

class FirestoreRemoteNoteImpl(
        val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : IRemoteNoteRepository {

    //Currently handled in RegisteredNoteRepositoryImpl
    override suspend fun synchronizeTransactions(transactions: List<NoteTransaction>): Result<Exception, Unit> = Result.build { Unit }


    override suspend fun getNotes(): Result<Exception, List<Note>> {
        var reference = firestore.collection(COLLECTION_NAME)

        return try {
            val task = awaitTaskResult(reference.get())

            return resultToNoteList(task)
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
            val task = awaitTaskResult(reference.get())

            Result.build {
                task.toObject(FirebaseNote::class.java)?.toNote
            }
        } catch (exception: Exception) {
            Result.build { throw exception }
        }

    }

    override suspend fun deleteNote(note: Note): Result<Exception, Unit> {
        return try {
            awaitTaskCompletable(firestore.collection(COLLECTION_NAME)
                    .document(note.creationDate)
                    .delete()
            )

            Result.build { Unit }

        } catch (exception: Exception) {
            Result.build { throw exception }
        }
    }

    override suspend fun updateNote(note: Note): Result<Exception, Unit> {
        return try {
            awaitTaskCompletable(firestore.collection(COLLECTION_NAME)
                    .document(note.creationDate)
                    .set(note.toFirebaseNote)
            )

            Result.build { Unit }

        } catch (exception: Exception) {
            Result.build { throw exception }
        }
    }
}

//var and default arguments used due to firestore requiring a no argument constructor to
//deserialize
data class FirebaseNote(
        var creationDate: String? = "",
        var contents: String? = "",
        var upVotes: Int? = 0,
        var imageurl: String? = "",
        var creator: String? = ""
)