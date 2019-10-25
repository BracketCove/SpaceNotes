package com.wiseassblog.data

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.wiseassblog.data.datamodels.AnonymousRoomNote
import com.wiseassblog.data.datamodels.RegisteredRoomNote
import com.wiseassblog.data.datamodels.RegisteredRoomTransaction
import com.wiseassblog.data.datamodels.FirebaseNote
import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.domain.domainmodel.NoteTransaction
import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.domainmodel.TransactionType
import com.wiseassblog.domain.domainmodel.User
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


suspend fun <T> awaitTaskResult(task: Task<T>): T = suspendCoroutine { continuation ->
    task.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            continuation.resume(task.result!!)
        } else {
            continuation.resumeWithException(task.exception!!)
        }
    }
}

//Wraps Firebase/GMS calls
suspend fun <T> awaitTaskCompletable(task: Task<T>): Unit = suspendCoroutine { continuation ->
    task.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            continuation.resume(Unit)
        } else {
            continuation.resumeWithException(task.exception!!)
        }
    }
}


//Since this.creator is of type Note?, we must give it a default value in such cases.
internal val Note.safeGetUid: String
    get() = this.creator?.uid ?: ""

internal val NoteTransaction.safeGetUid: String
    get() = this.creator?.uid ?: ""

internal val Uri?.defaultIfEmpty: String
    get() = if (this.toString() == "" || this == null) "satellite_beam"
    else this.toString()


//"this" refers to the object upon which this extension property is called
internal val Note.toAnonymousRoomNote: AnonymousRoomNote
    get() = AnonymousRoomNote(
            this.creationDate,
            this.contents,
            this.upVotes,
            this.imageUrl,
            this.safeGetUid
    )

internal val AnonymousRoomNote.toNote: Note
    get() = Note(
            this.creationDate,
            this.contents,
            this.upVotes,
            this.imageUrl,
            User(this.creatorId)
    )

internal val RegisteredRoomTransaction.toTransaction: NoteTransaction
    get() = NoteTransaction(
            this.creationDate,
            this.contents,
            this.upVotes,
            this.imageUrl,
            User(this.creatorId),
            this.transactionType.toTransactionType()
    )

internal val NoteTransaction.toRegisteredRoomTransaction: RegisteredRoomTransaction
    get() = RegisteredRoomTransaction(
            this.creationDate,
            this.contents,
            this.upVotes,
            this.imageUrl,
            this.safeGetUid,
            this.transactionType.value
    )

internal val NoteTransaction.toNote: Note
    get() = Note(
            this.creationDate,
            this.contents,
            this.upVotes,
            this.imageUrl,
            User(this.safeGetUid)
    )

internal fun String.toTransactionType(): TransactionType {
    return if (this.equals(TransactionType.DELETE)) TransactionType.DELETE
    else TransactionType.UPDATE
}

internal val Note.toRegisteredRoomNote: RegisteredRoomNote
    get() = RegisteredRoomNote(
            this.creationDate,
            this.contents,
            this.upVotes,
            this.imageUrl,
            this.safeGetUid
    )

internal val RegisteredRoomNote.toNote: Note
    get() = Note(
            this.creationDate,
            this.contents,
            this.upVotes,
            this.imageUrl,
            User(this.creatorId)
    )

internal val Note.toFirebaseNote: FirebaseNote
    get() = FirebaseNote(
            this.creationDate,
            this.contents,
            this.upVotes,
            this.imageUrl,
            this.safeGetUid
    )


internal val FirebaseNote.toNote: Note
    get() = Note(
            this.creationDate ?: "",
            this.contents ?: "",
            this.upVotes ?: 0,
            this.imageurl ?: "",
            User(this.creator ?: "")
    )

//Maps from lists of different Data Model types
internal fun List<AnonymousRoomNote>.toNoteListFromAnonymous(): List<Note> = this.flatMap {
    listOf(it.toNote)
}

internal fun List<RegisteredRoomNote>.toNoteListFromRegistered(): List<Note> = this.flatMap {
    listOf(it.toNote)
}

internal fun List<RegisteredRoomTransaction>.toNoteTransactionListFromRegistered(): List<NoteTransaction> = this.flatMap {
    listOf(it.toTransaction)
}


internal inline fun <reified T> resultToList(result: QuerySnapshot?): Result<Exception, List<T>> {
    val noteList = mutableListOf<T>()
    result?.forEach { documentSnapshop ->
        noteList.add(documentSnapshop.toObject(T::class.java))
    }
    return Result.build {
        noteList
    }
}