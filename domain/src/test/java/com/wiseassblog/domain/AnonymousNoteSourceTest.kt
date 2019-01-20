package com.wiseassblog.domain

import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.domainmodel.User
import com.wiseassblog.domain.error.SpaceNotesError
import com.wiseassblog.domain.interactor.AnonymousNoteSource
import com.wiseassblog.domain.repository.ILocalNoteRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Anonymous Note Source is for users that have not authenticated any social media accounts (such as
 * via Google Sign In)
 * Anonymous users have access to:
 * - A local Repository; nothing else.
 */

class AnonymousNoteSourceTest {

    val dispatcher: DispatcherProvider = mockk()

    val anonSource = AnonymousNoteSource()

    val locator: NoteServiceLocator = mockk()

    val localNoteRepo: ILocalNoteRepository = mockk()

    //Shout out to Philipp Hauer @philipp_hauer for the snippet below (creating test data) with
    //a default argument wrapper function:
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


    @BeforeEach
    fun setUpRedundantMocks() {
        clearAllMocks()
        every { dispatcher.provideIOContext() } returns Dispatchers.Unconfined
    }

    /**
     * When an anonymous user navigates to the List Feature, we retrieve data from a local
     * repository only.
     *
     * a. Retrieve Notes Successfully
     * b. Error cases
     *
     * a:
     *1. Request data from localNoteRepo
     *2.
     *
     */
    @Test
    fun `On Get Notes Successful`() = runBlocking {
        //1 Set up Test data and mock responses

        val testList = listOf(getNote(), getNote(), getNote())

        every { locator.localAnon } returns localNoteRepo

        coEvery { localNoteRepo.getNotes() } returns Result.build { testList }

        //2 Call the Unit to be tested
        val result: Result<Exception, List<Note>> = anonSource.getNotes(locator)

        //3 Verify behaviour and state
        verify { dispatcher.provideIOContext() }
        verify { locator.localAnon }
        coVerify { localNoteRepo.getNotes() }

        if (result is Result.Value) assertEquals(result.value, testList)
        else assertTrue { false }
    }

    /**
     *b:
     *1.
     *
     */
    @Test
    fun `On Get Notes Error`() = runBlocking {

        every { locator.localAnon } returns localNoteRepo

        coEvery { localNoteRepo.getNotes() } returns Result.build { throw SpaceNotesError.LocalIOException }

        //2 Call the Unit to be tested
        val result: Result<Exception, List<Note>> = anonSource.getNotes(locator)

        //3 Verify behaviour and state
        verify { dispatcher.provideIOContext() }
        verify { locator.localAnon }
        coVerify { localNoteRepo.getNotes() }

        assert(result is Result.Error)
    }

    /**
     * Retrieve a given note based on a passed in id
     * a. Note retrieved successfully
     * b. Error
     *
     * 1. Get note from repo
     */
    @Test
    fun `On Get Note Successful`() = runBlocking {

        val testNote = getNote()

        every { locator.localAnon } returns localNoteRepo

        coEvery { localNoteRepo.getNote(testNote.creationDate) } returns Result.build {
            testNote
        }

        //2 Call the Unit to be tested
        val result: Result<Exception, Note?> = anonSource.getNoteById(
                testNote.creationDate,
                locator
        )

        //3 Verify behaviour and state
        verify { dispatcher.provideIOContext() }
        verify { locator.localAnon }
        coVerify { localNoteRepo.getNote(testNote.creationDate) }

        if (result is Result.Value) assertEquals(result.value, testNote)
        else assertTrue { false }
    }

    /**
     *b:
     */
    @Test
    fun `On Get Note Error`() = runBlocking {

        val testId = getNote().creationDate

        every { locator.localAnon } returns localNoteRepo

        coEvery { localNoteRepo.getNote(testId) } returns Result.build { throw SpaceNotesError.LocalIOException }

        //2 Call the Unit to be tested
        val result: Result<Exception, Note?> = anonSource.getNoteById(testId, locator)

        //3 Verify behaviour and state
        verify { dispatcher.provideIOContext() }
        verify { locator.localAnon }
        coVerify { localNoteRepo.getNote(testId) }

        assert(result is Result.Error)
    }

    /**
     * When an anonymous user is done editing their note, attempt to update the value
     * in the local repository
     * a. Success: true
     * b. Error
     */
    @Test
    fun `On Update Note Success`() = runBlocking {

        val testNote = getNote()

        every { locator.localAnon } returns localNoteRepo

        coEvery { localNoteRepo.updateNote(testNote) } returns Result.build {
            Unit
        }

        //2 Call the Unit to be tested
        val result: Result<Exception, Unit> = anonSource.updateNote(
                testNote,
                locator
        )

        //3 Verify behaviour and state
        verify { dispatcher.provideIOContext() }
        verify { locator.localAnon }
        coVerify { localNoteRepo.updateNote(testNote) }

        if (result is Result.Value) assertTrue(true)
        else assertTrue { false }
    }

    /**
     * b:
     */
    @Test
    fun `On Update Note Error`() = runBlocking {

        val testNote = getNote()

        every { locator.localAnon } returns localNoteRepo

        coEvery { localNoteRepo.updateNote(testNote) } returns Result.build {
            throw SpaceNotesError.LocalIOException
        }

        //2 Call the Unit to be tested
        val result: Result<Exception, Unit> = anonSource.updateNote(
                testNote,
                locator
        )

        //3 Verify behaviour and state
        verify { dispatcher.provideIOContext() }
        verify { locator.localAnon }
        coVerify { localNoteRepo.updateNote(testNote) }

        assertTrue(result is Result.Error)
    }

    /**
     * When the user wishes to delete a note then we try to delete the note.
     *a. successfully deleted : true
     *b. Error
     *
     */
    @Test
    fun `On Delete Note Successful`() = runBlocking {

        val testNote = getNote()

        every { locator.localAnon } returns localNoteRepo

        coEvery { localNoteRepo.deleteNote(testNote) } returns Result.build {
            Unit
        }

        //2 Call the Unit to be tested
        val result: Result<Exception, Unit> = anonSource.deleteNote(
                testNote,
                locator
        )

        //3 Verify behaviour and state
        verify { dispatcher.provideIOContext() }
        verify { locator.localAnon }
        coVerify { localNoteRepo.deleteNote(testNote) }

        if (result is Result.Value) assertTrue(true)
        else assertTrue { false }
    }

    /**
     * b:
     */
    @Test
    fun `On Delete Note Error`() = runBlocking {

        val testNote = getNote()

        every { locator.localAnon } returns localNoteRepo

        coEvery { localNoteRepo.deleteNote(testNote) } returns Result.build {
            throw SpaceNotesError.LocalIOException
        }

        //2 Call the Unit to be tested
        val result: Result<Exception, Unit> = anonSource.deleteNote(
                testNote,
                locator
        )

        //3 Verify behaviour and state
        verify { dispatcher.provideIOContext() }
        verify { locator.localAnon }
        coVerify { localNoteRepo.deleteNote(testNote) }

        assertTrue(result is Result.Error)
    }

    @AfterEach
    fun confirm() {
        confirmVerified(
            dispatcher,
            locator,
            localNoteRepo
        )
    }

}
