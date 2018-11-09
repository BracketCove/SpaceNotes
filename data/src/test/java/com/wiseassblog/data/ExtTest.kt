package com.wiseassblog.data

import com.wiseassblog.data.note.RoomNote
import com.wiseassblog.data.note.toNoteList
import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.domain.domainmodel.User
import org.junit.Test
import kotlin.test.assertTrue


class ExtTest{
    fun getNote(creationDate: String = "28/10/2018",
                contents: String = "When I understand that this glass is already broken, every moment with it becomes precious.",
                upVotes: Int = 0,
                imageUrl: String = "",
                creator: User? = User(
                        "8675309",
                        "Ajahn Chah",
                        ""
                )
    ) = Note(
            creationDate = creationDate,
            contents = contents,
            upVotes = upVotes,
            imageUrl = imageUrl,
            creator = creator
    )


    fun getRoomNote(creationDate: String = "28/10/2018",
                contents: String = "When I understand that this glass is already broken, every moment with it becomes precious.",
                upVotes: Int = 0,
                imageUrl: String = "",
                creator: String = "8675309"
    ) = RoomNote(
            creationDate = creationDate,
            contents = contents,
            upVotes = upVotes,
            imageUrl = imageUrl,
            creatorId = creator
    )


    @Test
    fun testExtensionFlatMap(){
        val noteList = listOf<Note>(getNote(), getNote(), getNote())
        val roomNoteList = listOf<RoomNote>(getRoomNote(), getRoomNote(), getRoomNote())

        val result = roomNoteList.toNoteList()

        assertTrue { result == noteList }

    }
}


