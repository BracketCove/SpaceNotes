package com.wiseassblog.spacenotes

import com.wiseassblog.domain.DispatcherProvider
import com.wiseassblog.domain.NoteServiceLocator
import com.wiseassblog.domain.UserServiceLocator
import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.domainmodel.User
import com.wiseassblog.domain.interactor.AnonymousNoteSource
import com.wiseassblog.domain.interactor.AuthSource
import com.wiseassblog.domain.interactor.PublicNoteSource
import com.wiseassblog.domain.interactor.RegisteredNoteSource
import com.wiseassblog.spacenotes.common.MESSAGE_DELETE_SUCCESSFUL
import com.wiseassblog.spacenotes.notedetail.INoteDetailContract
import com.wiseassblog.spacenotes.notedetail.NoteDetailEvent
import com.wiseassblog.spacenotes.notedetail.NoteDetailLogic
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * Philipp Hauer
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class NoteDetailLogicTest {

    private val dispatcher: DispatcherProvider = mockk()

    private val noteLocator: NoteServiceLocator = mockk()

    private val userLocator: UserServiceLocator = mockk()

    private val vModel: INoteDetailContract.ViewModel = mockk(relaxed = true)

    private val view: INoteDetailContract.View = mockk(relaxed = true)

    private val anonymous: AnonymousNoteSource = mockk()

    private val registered: RegisteredNoteSource = mockk()

    private val public: PublicNoteSource = mockk()

    private val auth: AuthSource = mockk()


    private lateinit var logic: NoteDetailLogic


    //Shout out to Philipp Hauer @philipp_hauer for the snippet below (creating test data) with
    //a default argument wrapper function:
    fun getNote(creationDate: String = "12:30:30, November 3rd, 2018",
                contents: String = "When I understand that this glass is already broken, every moment with it becomes precious.",
                upVotes: Int = 0,
                imageUrl: String = "",
                creator: User? = User(
                        "8675309",
                        "Ajahn Chah",
                        "satellite_beam"
                )
    ) = Note(
            creationDate = creationDate,
            contents = contents,
            upVotes = upVotes,
            imageUrl = imageUrl,
            creator = creator
    )

    fun getUser(uid: String = "8675309",
                name: String = "Ajahn Chah",
                profilePicUrl: String = ""
    ) = User(uid,
            name,
            profilePicUrl)

    fun getLogic(id: String = getNote().creationDate,
                 isPrivate: Boolean = true) = NoteDetailLogic(
            dispatcher,
            noteLocator,
            userLocator,
            vModel,
            view,
            anonymous,
            registered,
            public,
            auth,
            id,
            isPrivate
    )


    @BeforeEach
    fun clear() {
        clearAllMocks()

        every {
            dispatcher.provideUIContext()
        } returns Dispatchers.Unconfined
    }

    /**
     * When auth presses done, they are finished editing their note. They will be returned to a list
     * view of all notes. Depending on if the note isPrivate, and whether or not the user is
     * anonymous, will dictate where the note is written to.
     *
     * a. isPrivate: true, user: null
     * b. isPrivate: false, user: not null
     * c. isPrivate: true, user: not null
     *
     * 1. Check current user status: null (anonymous), isPrivate is beside the point if null user
     * 2. Create a copy of the note in vM, with updated "content" value
     * 3. exit to list activity upon completion
     */
    @Test
    fun `On Done Click private, not logged in`() = runBlocking {

        logic = getLogic()

        every {
            view.getNoteBody()
        } returns getNote().contents

        every {
            vModel.getNoteState()
        } returns getNote()

        coEvery {
            anonymous.updateNote(getNote(), noteLocator)
        } returns Result.build { Unit }

        coEvery {
            auth.getCurrentUser(userLocator)
        } returns Result.build { null }

        //call the unit to be tested
        logic.onChanged(NoteDetailEvent.OnDoneClick)

        //verify interactions and state if necessary

        verify { view.getNoteBody() }
        verify { vModel.getNoteState() }
        coVerify { auth.getCurrentUser(userLocator) }
        coVerify { anonymous.updateNote(getNote(), noteLocator) }
        verify { view.startListFeature() }
    }

    /**
     *b:
     * 1. get current value of noteBody
     * 2. write updated note to repositories
     * 3. exit to list activity
     */
    @Test
    fun `On Done Click private, logged in`() = runBlocking {
        logic = getLogic()

        every {
            view.getNoteBody()
        } returns getNote().contents

        every {
            vModel.getNoteState()
        } returns getNote()

        coEvery {
            registered.updateNote(getNote(), noteLocator)
        } returns Result.build { Unit }

        coEvery {
            auth.getCurrentUser(userLocator)
        } returns Result.build { getUser() }

        //call the unit to be tested
        logic.onChanged(NoteDetailEvent.OnDoneClick)

        //verify interactions and state if necessary

        verify { view.getNoteBody() }
        verify { vModel.getNoteState() }
        coVerify { auth.getCurrentUser(userLocator) }
        coVerify { registered.updateNote(getNote(), noteLocator) }
        verify { view.startListFeature() }
    }

    /**
     * When auth presses delete, they may wish to delete a note. Show confirmation.
     */
    @Test
    fun `On Delete Click`() = runBlocking {
        logic = getLogic()

        every {
            view.showConfirmDeleteSnackbar()
        } returns Unit

        logic.onChanged(NoteDetailEvent.OnDeleteClick)

        verify { view.showConfirmDeleteSnackbar() }
    }

    /**
     * When user confirms that they wish to delete a note, delete the note. There are three possible
     * places to delete from:
     * a. Private Anonymous Repo
     * b. Private Registered Repo
     * c. Public Repo
     *
     * a:
     * 1. Check status of current user: null
     * 2. delete Note from anonymous repo
     * 3. show message to indicate if operation was successful
     * 3. startListFeature
     */
    @Test
    fun `On Delete Confirmation successful anonymous`() {
        logic = getLogic()

        every {
            vModel.getNoteState()
        } returns getNote()

        coEvery {
            auth.getCurrentUser(userLocator)
        } returns Result.build { null }

        coEvery {
            anonymous.deleteNote(getNote(), noteLocator)
        } returns Result.build { Unit }

        logic.onChanged(NoteDetailEvent.OnDeleteConfirmed)

        verify { vModel.getNoteState() }
        verify { view.showMessage(MESSAGE_DELETE_SUCCESSFUL) }
        verify { view.startListFeature() }
        coVerify { anonymous.deleteNote(getNote(), noteLocator) }
        coVerify { auth.getCurrentUser(userLocator) }
    }

    /**
     * When user confirms that they wish to delete a note, delete the note. There are three possible
     * places to delete from:
     * a. Private Anonymous Repo
     * b. Private Registered Repo
     * c. Public Repo
     *
     * a:
     * 1. Check status of current user: not null
     * 2. check isPrivate: true
     * 2. delete Note from registered repo
     * 3. show message to indicate if operation was successful
     * 3. startListFeature
     */
    @Test
    fun `On Delete Confirmation successful registered`() {
        logic = getLogic()

        every {
            vModel.getNoteState()
        } returns getNote()

        every {
            vModel.getIsPrivateMode()
        } returns true

        coEvery {
            auth.getCurrentUser(userLocator)
        } returns Result.build { getUser() }

        coEvery {
            registered.deleteNote(getNote(), noteLocator)
        } returns Result.build { Unit }

        logic.onChanged(NoteDetailEvent.OnDeleteConfirmed)

        verify { vModel.getNoteState() }
        verify { view.showMessage(MESSAGE_DELETE_SUCCESSFUL) }
        verify { view.startListFeature() }
        coVerify { registered.deleteNote(getNote(), noteLocator) }
        coVerify { auth.getCurrentUser(userLocator) }
    }

    /**
     *
     * a:
     * 1. Check status of current user: not null
     * 2. check isPrivate: false
     * 2. delete Note from public repo
     * 3. show message to indicate if operation was successful
     * 3. startListFeature
     */
    @Test
    fun `On Delete Confirmation successful public`() {
//        listener = getListener()
//
//        every {
//            vModel.getNoteState()
//        } returns getNote()
//
//        every {
//            vModel.getIsPrivateMode()
//        } returns false
//
//        coEvery {
//            auth.getCurrentUser(locator)
//        } returns Result.build { getUser() }
//
//        coEvery {
//            public.deleteNote( locator, dispatcher)
//        } returns Result.build { true }
//
//        listener.event(NoteDetailEvent.OnDeleteConfirmed)
//
//        verify { vModel.getNoteState() }
//        verify { view.showMessage(MESSAGE_DELETE_SUCCESSFUL) }
//        verify { view.startListFeature() }
//        coVerify { public.deleteNote(getNote(), locator, dispatcher) }
//        coVerify { auth.getCurrentUser(locator) }
    }

    @Test
    fun `On Back Click`() {
        logic = getLogic()

        logic.onChanged(NoteDetailEvent.OnBackClick)

        verify { view.startListFeature() }
    }


    /**
     * In on bind, we need to check the status of arguments sent in to the feature via intent,
     * check the user status, and call onStart() to render the view.
     *
     * a. get id from vModel: "" or null
     * b. get id from vModel: not null
     * c. get user from auth: null
     * d. get user from auth: not null
     * e. get isPrivate from vModel: true
     * f. get isPrivate from vModel: false
     *
     * a/c:
     * 1. Check User state: null
     * 2. Check arguments from activity: note id = "", isPrivate = true
     * 3. Create new note with date and null user, store in vModel
     * 4. render view
     * - back set to invisible (only delete or save allowed for new notes
     * - start satellite animation
     * - set creation date
     * 4.
     */
    @Test
    fun `On bind a and c`() {
        logic = getLogic("", true)

        every {
            view.getTime()
        } returns getNote().creationDate

        every {
            vModel.getId()
        } returns ""

        every {
            vModel.getIsPrivateMode()
        } returns true

        coEvery {
            auth.getCurrentUser(userLocator)
        } returns Result.build { null }

        logic.onChanged(NoteDetailEvent.OnBind)

        //creatorId should be null for new note. It will be added if the user saves the note while
        //logged in
        verify { vModel.setNoteState(getNote(creator = null, contents = "", imageUrl = "satellite_beam")) }
        verify { vModel.setIsPrivateMode(true) }
        coVerify { auth.getCurrentUser(userLocator) }
        verify { vModel.setId("") }
        verify { view.getTime() }
        verify { view.hideBackButton() }
        excludeRecords {
            view.setBackgroundImage(any())
            view.setDateLabel(any())
            view.setNoteBody(any())
        }
    }

    /**
     *a: Not new Note
     *d: Not null user
     */
    @Test
    fun `On bind a and d`() {
        logic = getLogic("", true)

        every {
            view.getTime()
        } returns getNote().creationDate

        every {
            vModel.getId()
        } returns ""

        every {
            vModel.getIsPrivateMode()
        } returns true

        coEvery {
            auth.getCurrentUser(userLocator)
        } returns Result.build { getUser() }

        logic.onChanged(NoteDetailEvent.OnBind)

        //creatorId should be null for new note. It will be added if the user saves the note while
        //logged in
        verify { vModel.setNoteState(getNote(creator = getUser(), contents = "", imageUrl = "satellite_beam")) }
        verify { vModel.setIsPrivateMode(true) }
        coVerify { auth.getCurrentUser(userLocator) }
        verify { vModel.setId("") }
        verify { view.getTime() }
        verify { view.hideBackButton() }
        excludeRecords {
            view.setBackgroundImage(any())
            view.setDateLabel(any())
            view.setNoteBody(any())
        }
    }

    /**
     *b: Not new Note
     *c: User is null
     *
     * 1. Get current user: null
     * 2. Check id: not null
     * 3. Query anonymous datasource based on id
     */
    @Test
    fun `On bind b and c`() {
        logic = getLogic(getNote().creationDate, true)

        every {
            vModel.getId()
        } returns getNote().creationDate

        every {
            vModel.getIsPrivateMode()
        } returns true

        coEvery {
            auth.getCurrentUser(userLocator)
        } returns Result.build { null }

        coEvery {
            anonymous.getNoteById(getNote().creationDate, noteLocator)
        } returns Result.build { getNote() }

        logic.onChanged(NoteDetailEvent.OnBind)

        //creatorId should be null for new note. It will be added if the user saves the note while
        //logged in
        verify { vModel.setNoteState(getNote()) }
        verify { vModel.setIsPrivateMode(true) }
        coVerify { auth.getCurrentUser(userLocator) }
        verify { vModel.setId(getNote().creationDate) }
        coExcludeRecords {
            anonymous.getNoteById(any(), any())
            view.setBackgroundImage(any())
            view.setDateLabel(any())
            view.setNoteBody(any())
        }
    }

    /**
     *b: Not new Note
     *d: public mode
     */
    @Test
    fun `On bind b and d`() {

    }

    /**On start  can be considered as a generic event to represent the view telling the listener
     * that it's time to rock'n'roll.
     *
     * 1. Get value of the Note from VM
     * 2. Render View
     */
    @Test
    fun `On start`() = runBlocking {
        logic = getLogic(id = getNote().creationDate)

        every {
            vModel.getNoteState()
        } returns getNote()

        logic.onChanged(NoteDetailEvent.OnStart)

        verify { vModel.getNoteState() }
        verify { view.setBackgroundImage(getNote().imageUrl) }
        verify { view.setDateLabel(getNote().creationDate) }
        verify { view.setNoteBody(getNote().contents) }
    }

    @AfterEach
    fun confirm() {
        excludeRecords {
            dispatcher.provideUIContext()

            vModel.getNoteState()

            vModel.setId(any())
            vModel.getId()

            vModel.setIsPrivateMode(any())
            vModel.getIsPrivateMode()
        }
        confirmVerified(
                dispatcher,
                noteLocator,
                userLocator,
                vModel,
                view,
                anonymous,
                registered,
                public,
                auth
        )
    }

}
