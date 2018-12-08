package com.wiseassblog.data

import com.wiseassblog.data.note.FirestoreRemoteNoteImpl
import com.wiseassblog.data.note.RegisteredNoteRepositoryImpl
import com.wiseassblog.data.note.RoomLocalCacheImpl
import com.wiseassblog.domain.DispatcherProvider
import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.domainmodel.User
import com.wiseassblog.domain.error.SpaceNotesError
import com.wiseassblog.domain.repository.ILocalNoteRepository
import com.wiseassblog.domain.repository.IRemoteNoteRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RegisteredNoteRepositoryTest {

    val dispatcher: DispatcherProvider = mockk()

    val cache: ILocalNoteRepository = mockk()

    val remote: IRemoteNoteRepository = mockk()

    val repo = RegisteredNoteRepositoryImpl(remote, cache)

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
        clearMocks()
        every { dispatcher.provideIOContext() } returns Dispatchers.Unconfined

    }

    /**
     * On get notes, we first request Notes from the Remote. Data is returned either from the remote
     * or local based on that result.
     * a: Success
     * b: Error
     *
     * a:
     * 1. Request Data from Remote: Success
     * 2. Update the Local repository
     * 3. Return data from Remote
     */
    @Test
    fun `Get Notes Success`() = runBlocking {
        val testList = listOf(getNote(), getNote(), getNote())

        coEvery { remote.getNotes() } returns Result.build { testList }

        coEvery { cache.updateAll(testList) } returns Result.build { true }
        coEvery { cache.deleteAll() } returns Result.build { true }

        val result = repo.getNotes()

        coVerify { remote.getNotes() }
        coVerify { cache.deleteAll() }
        coVerify { cache.updateAll(testList) }

        if (result is Result.Value) assertEquals(result.value, testList)
        else assertTrue { false }
    }

    /**
     * b:
     * 1. Request Data from Remote: Error
     * 2. Return Data from Local
     */
    @Test
    fun `Get Notes Fail`() = runBlocking {
        val testNote = getNote()
        val testList = listOf(getNote(), getNote(), getNote())

        coEvery { remote.getNotes() } returns Result.build { throw SpaceNotesError.RemoteIOException }

        coEvery { cache.getNotes() } returns Result.build { testList }

        val result = repo.getNotes()

        coVerify { remote.getNotes() }
        coVerify { cache.getNotes() }

        if (result is Result.Value) assertEquals(result.value, testList)
        else assertTrue { false }
    }

    /**
     * On get note, we first request Notes from the Remote. Data is returned either from the remote
     * or local based on that result.
     * a: Success
     * b: Fail
     *
     * a:
     * 1. Request Data from Remote: Success
     * 2. Return data from Remote
     */
    @Test
    fun `Get Note Success`() = runBlocking {
        val testNote = getNote()

        coEvery { remote.getNote(testNote.creationDate) } returns Result.build { testNote }

        val result = repo.getNote(testNote.creationDate)

        coVerify { remote.getNote(testNote.creationDate) }

        if (result is Result.Value) assertEquals(result.value, testNote)
        else assertTrue { false }
    }

    /**
     * b:
     * 1. Request Data from Remote: Fail
     * 2. Return Data from Local
     */
    @Test
    fun `Get Note Fail`() = runBlocking {
        val testNote = getNote()

        coEvery { remote.getNote(testNote.creationDate) } returns Result.build {
            throw SpaceNotesError.RemoteIOException
        }
        coEvery { cache.getNote(testNote.creationDate) } returns Result.build { testNote }

        val result = repo.getNote(testNote.creationDate)

        coVerify { remote.getNote(testNote.creationDate) }
        coVerify { cache.getNote(testNote.creationDate) }

        if (result is Result.Value) assertEquals(result.value, testNote)
        else assertTrue { false }
    }

    /**
     * On delete note:
     * a: Success
     * b: Fail
     *
     * a:
     * 1. Delete Data from Remote: Success
     * 2. Return: true
     */
    @Test
    fun `Delete Note Success`() = runBlocking {
        val testNote = getNote()

        coEvery { remote.deleteNote(testNote) } returns Result.build {
            true
        }

        val result = repo.deleteNote(testNote)

        coVerify { remote.deleteNote(testNote) }

        assertTrue { result is Result.Value }
    }

    /**
     * b:
     * 1. Delete Data from Remote: Fail
     * 2. Return: Error
     */
    @Test
    fun `Delete Note Fail`() = runBlocking {
        val testNote = getNote()

        coEvery { remote.deleteNote(testNote) } returns Result.build {
            throw SpaceNotesError.RemoteIOException
        }

        val result = repo.deleteNote(testNote)

        coVerify { remote.deleteNote(testNote) }

        assertTrue { result is Result.Error }
    }

    /**
     * On delete note:
     * a: Success
     * b: Fail
     *
     * a:
     * 1. Update Data from Remote: Success
     * 2. Return: true
     */
    @Test
    fun `Update Note Success`() = runBlocking {
        val testNote = getNote()

        coEvery { remote.updateNote(testNote) } returns Result.build {
            true
        }

        val result = repo.updateNote(testNote)

        coVerify { remote.updateNote(testNote) }

        assertTrue { result is Result.Value }
    }

    /**
     * b:
     * 1. Update Data from Remote: Fail
     * 2. Return: Error
     */
    @Test
    fun `Update Note Fail`() = runBlocking {
        val testNote = getNote()

        coEvery { remote.updateNote(testNote) } returns Result.build {
            throw SpaceNotesError.RemoteIOException
        }

        val result = repo.updateNote(testNote)

        coVerify { remote.updateNote(testNote) }

        assertTrue { result is Result.Error }
    }
}