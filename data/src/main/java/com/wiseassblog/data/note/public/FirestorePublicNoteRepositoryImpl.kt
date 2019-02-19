package com.wiseassblog.data.note.public

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.wiseassblog.data.awaitTaskCompletable
import com.wiseassblog.data.awaitTaskResult
import com.wiseassblog.data.datamodels.FirebaseNote
import com.wiseassblog.data.toFirebaseNote
import com.wiseassblog.data.toNote
import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.repository.IPublicNoteRepository


const val COLLECTION_PUBLIC = "public_notes"
object FirestoreRemoteNoteImpl : IPublicNoteRepository {
    override suspend fun getNotes(): Result<Exception, List<Note>> {
        val firestore = FirebaseFirestore.getInstance()

        var reference = firestore.collection(COLLECTION_PUBLIC)

        return try {
            val task = awaitTaskResult(reference.get())

            return resultToNoteList(task)
        } catch (exception: Exception) {
            Result.build { throw exception }
        }
    }

    override suspend fun getNote(id: String): Result<Exception, Note?> {
        val firestore = FirebaseFirestore.getInstance()

        var reference = firestore.collection(COLLECTION_PUBLIC)
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
        val firestore = FirebaseFirestore.getInstance()

        return try {
            awaitTaskCompletable(firestore.collection(COLLECTION_PUBLIC)
                    .document(note.creationDate)
                    .delete()
            )

            Result.build { Unit }

        } catch (exception: Exception) {
            Result.build { throw exception }
        }
    }

    override suspend fun updateNote(note: Note): Result<Exception, Unit> {
        val firestore = FirebaseFirestore.getInstance()

        return try {
            awaitTaskCompletable(firestore.collection(COLLECTION_PUBLIC)
                    .document(note.creationDate)
                    .set(note.toFirebaseNote)
            )

            Result.build { Unit }

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
}