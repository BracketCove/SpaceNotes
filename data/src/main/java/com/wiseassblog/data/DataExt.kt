package com.wiseassblog.data

import android.net.Uri
import com.wiseassblog.data.note.FirebaseNote
import com.wiseassblog.data.note.RoomNote
import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.domain.domainmodel.User

//Since this.creator is of type Note?, we must give it a default value in such cases.
internal val Note.safeGetUid: String
    get() = this.creator?.uid ?: ""

internal val Uri?.defaultIfEmpty: String
    get() = if (this.toString() == "" || this == null) "satellite_beam"
    else this.toString()


//"this" refers to the object upon which this extension property is called
internal val Note.toRoomNote: RoomNote
    get() = RoomNote(
            this.creationDate,
            this.contents,
            this.upVotes,
            this.imageUrl,
            this.safeGetUid
    )


internal val RoomNote.toNote: Note
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
            this.creationDate,
            this.contents,
            this.upVotes,
            this.imageurl,
            User(this.creator)
    )


internal fun List<RoomNote>.toNoteList(): List<Note> = this.flatMap {
    listOf(it.toNote)

}
