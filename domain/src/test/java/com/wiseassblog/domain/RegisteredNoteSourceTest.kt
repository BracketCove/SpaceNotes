package com.wiseassblog.domain

import com.wiseassblog.domain.domainmodel.*
import com.wiseassblog.domain.error.SpaceNotesError
import com.wiseassblog.domain.interactor.RegisteredNoteSource
import com.wiseassblog.domain.repository.IRemoteNoteRepository
import com.wiseassblog.domain.repository.ITransactionRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Registered Note Source is for users which have authenticated via appropriate sign in functions.
 * Registered users have access to:
 * - A remote repository to share notes across devices, which is the source of truth for state
 * - A local repository to cache the most recent snap shot of the remote data, and to store offline
 * transactions to be pushed to the remote database.
 */
class RegisteredNoteSourceTest {

    val dispatcher: DispatcherProvider = mockk()

    val source = RegisteredNoteSource()

    //Stores transactions (NoteTransaction Cache) to be pushed to Remote eventually
    val transactionRepository: ITransactionRepository = mockk()

    //Contains Remote (SoT) and Local (State Cache)
    val noteRepository: IRemoteNoteRepository = mockk()

    val locator: ServiceLocator = mockk()

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

    fun getTransaction(
            creationDate: String = "28/10/2018",
            contents: String = "When I understand that this glass is already broken, every moment with it becomes precious.",
            upVotes: Int = 0,
            imageUrl: String = "",
            creator: User? = User(
                    "8675309",
                    "Ajahn Chah",
                    ""
            ),
            transactionType: TransactionType = TransactionType.DELETE
    ) = NoteTransaction(
            creationDate = creationDate,
            contents = contents,
            upVotes = upVotes,
            imageUrl = imageUrl,
            creator = creator,
            transactionType = transactionType
    )


    @BeforeEach
    fun setUpRedundantMocks() {
        clearMocks()
        every { dispatcher.provideIOContext() } returns Dispatchers.Unconfined
    }


    /**
     * Upon requesting Notes, multiple steps must occur:
     * 1. Check transactionRepo Cache for items:
     * a. Empty: Proceed 3
     * b. Not Empty: Proceed 2
     *
     * 2. Attempt to synchronize Remote with stored transactions:
     * c. Successful: Delete all transactions from transactionRepo; Proceed 3
     * d. Fail: Proceed 3
     *
     * 3. Get data from IRemoteNoteSource and return
     * e. Success: return data
     * f. Fail: return error
     *
     *
     * successful communication with the remote datasource, and
     *
     */
    @Test
    fun `On Get Notes a, e`() = runBlocking {

        val testNotes = listOf(getNote(), getNote(), getNote())

        every { locator.transactionReg } returns transactionRepository

        every { locator.remoteReg } returns noteRepository

        coEvery { transactionRepository.getTransactions() } returns Result.build {
            emptyList<NoteTransaction>()
        }

        coEvery { noteRepository.getNotes() } returns Result.build {
            testNotes
        }

        val result = source.getNotes(locator, dispatcher)

        coVerify { transactionRepository.getTransactions() }
        coVerify { noteRepository.getNotes() }


        if (result is Result.Value) assertEquals(result.value, testNotes)
        else assertTrue { false }
    }

    /**
     * b - transactions not empty
     * c - successfully synchronized remote
     * e - successfully returned data form remote
     */
    @Test
    fun `On Get Notes b, c, e`() = runBlocking {

        val testNotes = listOf(getNote(), getNote(), getNote())
        val testTransactions = listOf(getTransaction(), getTransaction(), getTransaction())

        every { locator.transactionReg } returns transactionRepository

        every { locator.remoteReg } returns noteRepository

        coEvery { transactionRepository.getTransactions() } returns Result.build {
            testTransactions
        }

        coEvery { transactionRepository.deleteTransactions() } returns Result.build {
            true
        }

        coEvery { noteRepository.getNotes() } returns Result.build {
            testNotes
        }

        coEvery { noteRepository.synchronizeTransactions(testTransactions) } returns Result.build {
            true
        }

        val result = source.getNotes(locator, dispatcher)

        coVerify { transactionRepository.getTransactions() }
        coVerify { noteRepository.synchronizeTransactions(testTransactions) }
        coVerify { transactionRepository.deleteTransactions() }
        coVerify { noteRepository.getNotes() }


        if (result is Result.Value) assertEquals(result.value, testNotes)
        else assertTrue { false }
    }

    /**
     * Attempt to retrieve a note from remote repository.
     * a. Success
     * b. Fail
     *
     * a:
     * 1. Request Note from Remote: success
     * 2. return data
     *
     */
    @Test
    fun `On Get Note a`() = runBlocking {
        val testId = getNote().creationDate


        every { locator.remoteReg } returns noteRepository

        coEvery { noteRepository.getNote(testId) } returns Result.build {
            getNote()
        }

        val result = source.getNoteById(testId, locator, dispatcher)

        coVerify { noteRepository.getNote(testId) }


        if (result is Result.Value) assertEquals(result.value, getNote())
        else assertTrue { false }
    }

    /**
     * b:
     * 1. Request Note from Remote: fail
     * 2. return error
     */
    @Test
    fun `On Get Note b`() = runBlocking {
        val testId = getNote().creationDate


        every { locator.remoteReg } returns noteRepository

        coEvery { noteRepository.getNote(testId) } returns Result.build {
            throw SpaceNotesError.RemoteIOException
        }

        val result = source.getNoteById(testId, locator, dispatcher)

        coVerify { noteRepository.getNote(testId) }

        assertTrue { result is Result.Error }
    }

    /**
     * Attempt to delete a note from remote repository. Failing that, store a transaction object
     * in transaction database. Failing that, return error.
     * a. Success
     * b. Delete Fail
     * c. Transaction Fail
     *
     * a:
     * 1. Delete Note from Remote: success
     * 2. return true
     *
     */
    @Test
    fun `On Delete Note a`() = runBlocking {
        val testNote = getNote()

        every { locator.remoteReg } returns noteRepository

        coEvery { noteRepository.deleteNote(testNote) } returns Result.build {
            true
        }

        val result = source.deleteNote(testNote, locator, dispatcher)

        coVerify { noteRepository.deleteNote(testNote) }

        if (result is Result.Value) {
            //assert the value as being "true"
            assertTrue { result.value }
        } else {
            assertTrue { false }
        }
    }

    /**
     * b:
     * 1. Delete Note from Remote: fail
     * 2. Map to NoteTransaction and store in transactionRepository: success
     * 3. return true
     */
    @Test
    fun `On Delete Note b`() = runBlocking {
        val testNote = getNote()
        val testTransaction = getNote().toTransaction(TransactionType.DELETE)

        every { locator.remoteReg } returns noteRepository
        every { locator.transactionReg } returns transactionRepository


        coEvery { noteRepository.deleteNote(testNote) } returns Result.build {
            throw SpaceNotesError.RemoteIOException
        }

        coEvery { transactionRepository.updateTransactions(testTransaction) } returns Result.build {
            true
        }

        val result = source.deleteNote(testNote, locator, dispatcher)

        coVerify { noteRepository.deleteNote(testNote) }
        coVerify { transactionRepository.updateTransactions(testTransaction) }

        if (result is Result.Value) {
            //assert the value as being "false"
            assertTrue { result.value }
        } else {
            assertTrue { false }
        }
    }

    /**
     * c:
     * 1. Delete Note from Remote: fail
     * 2. Map to NoteTransaction and store in transactionRepository: fail
     * 3. return error
     */
    @Test
    fun `On Delete Note c`() = runBlocking {
        val testNote = getNote()
        val testTransaction = getNote().toTransaction(TransactionType.DELETE)

        every { locator.remoteReg } returns noteRepository
        every { locator.transactionReg } returns transactionRepository


        coEvery { noteRepository.deleteNote(testNote) } returns Result.build {
            throw SpaceNotesError.RemoteIOException
        }

        coEvery { transactionRepository.updateTransactions(testTransaction) } returns Result.build {
            throw SpaceNotesError.TransactionIOException
        }

        val result = source.deleteNote(testNote, locator, dispatcher)

        coVerify { noteRepository.deleteNote(testNote) }
        coVerify { transactionRepository.updateTransactions(testTransaction) }

        assertTrue { result is Result.Error }
    }

    /**
     * Attempt to update a note in remote repository. Failing that, store a transaction object
     * in transaction database. Failing that, return error.
     * a. Success
     * b. Update Fail
     * c. Transaction Fail
     *
     * a:
     * 1. Update Note from Remote: success
     * 2. return true
     *
     */
    @Test
    fun `On Update Note a`() = runBlocking {
        val testNote = getNote()

        every { locator.remoteReg } returns noteRepository

        coEvery { noteRepository.updateNote(testNote) } returns Result.build {
            true
        }

        val result = source.updateNote(testNote, locator, dispatcher)

        coVerify { noteRepository.updateNote(testNote) }

        if (result is Result.Value) {
            //assert the value as being "true"
            assertTrue { result.value }
        } else {
            assertTrue { false }
        }
    }

    /**
     * b:
     * 1. Delete Note from Remote: fail
     * 2. Map to NoteTransaction and store in transactionRepository: success
     * 3. return true
     */
    @Test
    fun `On Update Note b`() = runBlocking {
        val testNote = getNote()
        val testTransaction = getNote().toTransaction(TransactionType.UPDATE)

        every { locator.remoteReg } returns noteRepository
        every { locator.transactionReg } returns transactionRepository

        coEvery { noteRepository.updateNote(testNote) } returns Result.build {
            throw SpaceNotesError.RemoteIOException
        }

        coEvery { transactionRepository.updateTransactions(testTransaction) } returns Result.build {
            true
        }

        val result = source.updateNote(testNote, locator, dispatcher)

        coVerify { noteRepository.updateNote(testNote) }
        coVerify { transactionRepository.updateTransactions(testTransaction) }

        if (result is Result.Value) {
            //assert the value as being "false"
            assertTrue { result.value }
        } else {
            assertTrue { false }
        }
    }

    /**
     * c:
     * 1. Delete Note from Remote: fail
     * 2. Map to NoteTransaction and store in transactionRepository: fail
     * 3. return error
     */
    @Test
    fun `On Update Note c`() = runBlocking {
        val testNote = getNote()
        val testTransaction = getNote().toTransaction(TransactionType.UPDATE)

        every { locator.remoteReg } returns noteRepository
        every { locator.transactionReg } returns transactionRepository

        coEvery { noteRepository.updateNote(testNote) } returns Result.build {
            throw SpaceNotesError.RemoteIOException
        }

        coEvery { transactionRepository.updateTransactions(testTransaction) } returns Result.build {
            throw SpaceNotesError.TransactionIOException
        }

        val result = source.updateNote(testNote, locator, dispatcher)

        coVerify { noteRepository.updateNote(testNote) }
        coVerify { transactionRepository.updateTransactions(testTransaction) }

        assertTrue { result is Result.Error }
    }
}
