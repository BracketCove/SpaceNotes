package com.wiseassblog.data.note

import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.domain.domainmodel.User

//Since this.creator is of type Note?, we must give it a default value in such cases.
val Note.safeGetUid: String
    get() = this.creator?.uid ?: ""

//"this" refers to the object upon which this extension property is called
val Note.toRoomNote: RoomNote
    get() = RoomNote(
            this.creationDate,
            this.contents,
            this.upVotes,
            this.imageUrl,
            this.safeGetUid
    )


val RoomNote.toNote: Note
    get() = Note(
            this.creationDate,
            this.contents,
            this.upVotes,
            this.imageUrl,
            User(this.creatorId)
    )




internal fun List<RoomNote>.toNoteList(): List<Note> = this.flatMap {
    listOf(it.toNote)

}