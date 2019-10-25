package com.wiseassblog.data.note.registered

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.wiseassblog.data.*
import com.wiseassblog.data.datamodels.FirebaseNote
import com.wiseassblog.data.resultToList
import com.wiseassblog.data.toFirebaseNote
import com.wiseassblog.data.toNote
import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.domain.domainmodel.NoteTransaction
import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.repository.IRemoteNoteRepository

private const val COLLECTION_NAME = "notes"

class FirestorePrivateRemoteNoteImpl(
        val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : IRemoteNoteRepository {

    //Currently handled in RegisteredNoteRepositoryImpl
    override suspend fun synchronizeTransactions(transactions: List<NoteTransaction>): Result<Exception, Unit> = Result.build { Unit }


    override suspend fun getNotes(): Result<Exception, List<Note>> {
        var reference = firestore.collection(COLLECTION_NAME)

        return try {
            val task = awaitTaskResult(reference.get())

            return resultToList(task)
        } catch (exception: Exception) {
            Result.build { throw exception }
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