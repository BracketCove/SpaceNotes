package com.wiseassblog.domain

import com.wiseassblog.domain.domainmodel.*
import com.wiseassblog.domain.error.SpaceNotesError
import com.wiseassblog.domain.interactor.PublicNoteSource
import com.wiseassblog.domain.repository.IPublicNoteRepository
import com.wiseassblog.domain.servicelocator.NoteServiceLocator
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PublicNoteSourceTest {

    val remote: IPublicNoteRepository = mockk()

    val locator: NoteServiceLocator = mockk()

    val source = PublicNoteSource()

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


    /**
     * Notes come from a single source, without any other shit involved.
     *
     * a: Notes returned
     * b: error returned
     *
     * a:
     * 1. Ask remote for notes: Notes
     *
     */
    @Test
    fun `On Get Notes`() = runBlocking {
        val testList = listOf(getNote(), getNote())

        every { locator.remotePublic } returns remote

        coEvery { remote.getNotes() } returns Result.build { testList }

        val result = source.getNotes(locator)

        coVerify { remote.getNotes() }

        if (result is Result.Value) assertEquals(result.value, testList)
        else assertTrue { false }

    }

    /**
     *b:
     *1. Ask remote for notes: error
     */
    @Test
    fun `On Get Notes error`() = runBlocking {
        every { locator.remotePublic } returns remote

        coEvery { remote.getNotes() } returns Result.build { throw SpaceNotesError.RemoteIOException }

        val result = source.getNotes(locator)

        coVerify { remote.getNotes() }

        assertTrue { result is Result.Error }
    }

    /**
     * Get a note by an id
     *
     * a: Note returned
     * b: error returned
     *
     * a:
     * 1. Ask remote for note: Note
     *
     */
    @Test
    fun `On Get Note`() = runBlocking {
        val testNote = getNote()

        every { locator.remotePublic } returns remote

        coEvery { remote.getNote(testNote.creator!!.uid) } returns Result.build { testNote }

        val result = source.getNoteById(testNote.creator!!.uid,locator)

        coVerify { remote.getNote(testNote.creator!!.uid) }

        if (result is Result.Value) assertEquals(result.value, testNote)
        else assertTrue { false }

    }

    /**
     *b:
     *1. Ask remote for notes: error
     */
    @Test
    fun `On Get Note error`() = runBlocking {
        val testNote = getNote()

        every { locator.remotePublic } returns remote

        coEvery { remote.getNote(testNote.creator!!.uid) } returns Result.build { throw SpaceNotesError.RemoteIOException }

        val result = source.getNoteById(testNote.creator!!.uid,locator)

        coVerify { remote.getNote(testNote.creator!!.uid) }

        assertTrue { result is Result.Error }
    }

}