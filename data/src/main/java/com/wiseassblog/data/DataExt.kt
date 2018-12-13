package com.wiseassblog.data

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.wiseassblog.data.entities.AnonymousRoomNote
import com.wiseassblog.data.entities.RegisteredRoomNote
import com.wiseassblog.data.entities.RegisteredRoomTransaction
import com.wiseassblog.data.note.registered.FirebaseNote
import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.domain.domainmodel.NoteTransaction
import com.wiseassblog.domain.domainmodel.TransactionType
import com.wiseassblog.domain.domainmodel.User
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine




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


internal fun List<AnonymousRoomNote>.toNoteListFromAnonymous(): List<Note> = this.flatMap {
    listOf(it.toNote)
}

internal fun List<RegisteredRoomNote>.toNoteListFromRegistered(): List<Note> = this.flatMap {
    listOf(it.toNote)
}

internal fun List<RegisteredRoomTransaction>.toNoteTransactionListFromRegistered(): List<NoteTransaction> = this.flatMap {
    listOf(it.toTransaction)
}
