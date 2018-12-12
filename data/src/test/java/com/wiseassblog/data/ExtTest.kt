package com.wiseassblog.data

import com.wiseassblog.data.entities.AnonymousRoomNote
import com.wiseassblog.data.entities.RegisteredRoomNote
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


    fun getAnonymousRoomNote(creationDate: String = "28/10/2018",
                              contents: String = "When I understand that this glass is already broken, every moment with it becomes precious.",
                              upVotes: Int = 0,
                              imageUrl: String = "",
                              creator: String = "8675309"
    ) = AnonymousRoomNote(
            creationDate = creationDate,
            contents = contents,
            upVotes = upVotes,
            imageUrl = imageUrl,
            creatorId = creator
    )

    fun getRegisteredRoomNote(creationDate: String = "28/10/2018",
                             contents: String = "When I understand that this glass is already broken, every moment with it becomes precious.",
                             upVotes: Int = 0,
                             imageUrl: String = "",
                             creator: String = "8675309"
    ) = RegisteredRoomNote(
            creationDate = creationDate,
            contents = contents,
            upVotes = upVotes,
            imageUrl = imageUrl,
            creatorId = creator
    )


    @Test
    fun testExtensionFlatMap(){
        val noteList = listOf<Note>(getNote(), getNote(), getNote())
        val roomNoteList = listOf<AnonymousRoomNote>(getAnonymousRoomNote(), getAnonymousRoomNote(), getAnonymousRoomNote())

        val result = roomNoteList.toNoteListFromAnonymous()

        assertTrue { result == noteList }

    }
}


