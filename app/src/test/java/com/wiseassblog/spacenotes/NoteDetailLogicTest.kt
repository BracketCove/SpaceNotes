package com.wiseassblog.spacenotes

import com.wiseassblog.domain.ServiceLocator
import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.domainmodel.User
import com.wiseassblog.domain.interactor.AuthSource
import com.wiseassblog.domain.interactor.PrivateNoteSource
import com.wiseassblog.domain.interactor.PublicNoteSource
import com.wiseassblog.domain.DispatcherProvider
import com.wiseassblog.spacenotes.common.MESSAGE_DELETE_SUCCESSFUL
import com.wiseassblog.spacenotes.notedetail.INoteDetailContract
import com.wiseassblog.spacenotes.notedetail.NoteDetailEvent
import com.wiseassblog.spacenotes.notedetail.NoteDetailLogic
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * Philipp Hauer
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class NoteDetailLogicTest {


    private val dispatcher: DispatcherProvider = mockk()

    private val locator: ServiceLocator = mockk()

    private val vModel: INoteDetailContract.ViewModel = mockk(relaxed = true)

    private val view: INoteDetailContract.View = mockk(relaxed = true)

    private val private: PrivateNoteSource = mockk()

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

    fun getLogic(id: String = getNote().creationDate,
                 isPrivate: Boolean = true) = NoteDetailLogic(
            dispatcher,
            locator,
            vModel,
            view,
            private,
            public,
            auth,
            id,
            isPrivate
    )


    @Before
    fun clear() {
        clearMocks()

        logic = getLogic()

    }

    /**
     * When auth presses done, they are finished editing their note. They will be returned to a list
     * view of all notes. Depending on if the note isPrivate, it will either be written to the
     * privateDataSource, or the publicDataSOurce.
     *
     * a. isPrivate: true
     * b. isPrivate: false
     * c. User is logged in
     * d. User is not logged in
     *
     * 1. Check if the note is private: true
     * 2. Check for currently logged in user: false (not sure if this should be a backend concern or not)
     * 3. Create a copy of the note in vM, with update content value
     * 3. exit to list activity
     */
    @Test
    fun `On Done Click private, not logged in`() = runBlocking {

        every {
            view.getNoteBody()
        } returns getNote().contents

        every {
            vModel.getNoteState()
        } returns getNote()

        every {
            vModel.getIsPrivateMode()
        } returns true

        every {
            dispatcher.provideUIContext()
        } returns Dispatchers.Unconfined

        coEvery {
            private.updateNote(getNote(), locator)
        } returns Result.build { true }

        //TODO() figure out if this is a backend concern or not
//        coEvery {
//            auth.getCurrentUser(locator)
//        } returns Result.build { null }

        //call the unit to be tested
        logic.event(NoteDetailEvent.OnDoneClick)

        //verify interactions and state if necessary

        verify { view.getNoteBody() }
        verify { vModel.getNoteState() }
        verify { vModel.getIsPrivateMode() }
     //   coVerify { auth.getCurrentUser(locator) }
        coVerify { private.updateNote(getNote(), locator) }
        verify { view.startListFeature() }
    }

    /**
     * a. isPrivate: true
    * b. isPrivate: false
    * c. User is logged in
    * d. User is not logged in
    *
    * 1. get current value of noteBody
    * 2. write updated note to repositories
    * 3. exit to list activity
    */
    @Test
    fun `On Done Click public, not logged in`() = runBlocking {
//
//        every {
//            view.getNoteBody()
//        } returns getNote().contents
//
//
//        every {
//            vModel.getNoteState()
//        } returns getNote()
//
//        every {
//            dispatcher.provideUIContext()
//        } returns Dispatchers.Unconfined
//
//        coEvery {
//            private.insertOrUpdateNote(getNote(), locator)
//        } returns Result.build { true }
//
//        //call the unit to be tested
//        logic.event(NoteDetailEvent.OnDoneClick)
//
//        //verify interactions and state if necessary
//
//        verify { view.getNoteBody() }
//        verify { vModel.getNoteState() }
//        verify { view.startListFeature() }
//        coVerify {  }
    }

    /**
     * When auth presses delete, they may wish to delete a note. Show confirmation.
     */
    @Test
    fun `On Delete Click`() = runBlocking {
        every {
            view.showConfirmDeleteSnackbar()
        } returns Unit

        logic.event(NoteDetailEvent.OnDeleteClick)

        verify { view.showConfirmDeleteSnackbar() }
    }

    /**
     * When auth confirms that they wish to delete a note, delete the note.
     *
     * 1. auth confirms
     * 2. delete note
     * 3. show message note deleted
     * 3. startListFeature
     */
    @Test
    fun `On Delete Confirmation successful`() {
        every {
            vModel.getNoteState()
        } returns getNote()

        every {
            dispatcher.provideUIContext()
        } returns Dispatchers.Unconfined

        coEvery {
            private.deleteNote(getNote(), locator)
        } returns Result.build { true }

        logic.event(NoteDetailEvent.OnDeleteConfirmed)

        verify { vModel.getNoteState() }
        verify { view.showMessage(MESSAGE_DELETE_SUCCESSFUL) }
        verify { view.startListFeature() }
    }

    /**On start  can be considered as a generic event to represent the view telling the presenter
     *to that it's time to rock'n'roll.
     *
     * Two scenarios may occur when bind is called.
     * a: first time it is called
     * b: n number of times it is called afterwards, as only the view should need to be recreated
     *
     * 1a. viewmodel returns null for it's note value
     * 1b. viewmodel contains a note
     * 2a. get id from viewmodel and use it to call privateNoteSource
     * 2b. render the view
     * 3a. update view and viewmodel with result
     */

    @Test
    fun `On start a`() = runBlocking {
        every {
            vModel.getNoteState()
        } returns null

        every {
            vModel.getId()
        } returns getNote().creationDate

        every {
            dispatcher.provideUIContext()
        } returns Dispatchers.Unconfined

        coEvery {
            private.getNoteById(getNote().creationDate, locator)
        } returns Result.build { getNote() }

        logic.event(NoteDetailEvent.OnStart)

        verify { vModel.setNoteState(any()) }
        coVerify { private.getNoteById(getNote().creationDate, locator) }
        verify { view.setBackgroundImage(getNote().imageUrl) }
        verify { view.setDateLabel(getNote().creationDate) }
        verify { view.setNoteBody(getNote().contents) }
    }

    @Test
    fun `On start b`() {
        every {
            vModel.getNoteState()
        } returns getNote()

        logic.event(NoteDetailEvent.OnStart)

        verify { vModel.getNoteState() }
        verify { view.setBackgroundImage(getNote().imageUrl) }
        verify { view.setDateLabel(getNote().creationDate) }
        verify { view.setNoteBody(getNote().contents) }
    }


    @Test
    fun `On Back Click`() {
        logic.event(NoteDetailEvent.OnBackClick)

        verify { view.startListFeature() }
    }


    /**
     * On bind process for detail view:
     * a. if onbind starts with an empty note id, this means that the user has elected to create a
     * new note instead of editing a note which already exists
     * b. if onbind starts with an id, this means the user wants to edit an existing note
     * c. if onbind starts with isPrivate true, parse and write to privateDataSource
     * d. if onbind starts with isPrivate false, parse and write to publicDataSource
     *
     * 1. Check arguments from activity
     * 2. Check note state
     * 3. Check User state if necessary
     * 4. call OnStart
     *
     *
     * a/c:
     * 1. Check arguments from activity: note id = "", isPrivate = true
     * 2. Create new note with date and null user, store in vModel
     * 3. render view
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

        logic.event(NoteDetailEvent.OnBind)

        //creatorId should be null for new note. It will be added if the user saves the note while
        //logged in
        verify { vModel.setNoteState(getNote(creator = null, contents = "", imageUrl = "satellite_beam")) }
        verify { vModel.setIsPrivateMode(true) }
        verify { vModel.setId("") }
        verify { vModel.setId(getNote().creationDate) }
        verify { view.getTime() }
        verify { view.hideBackButton() }
    }

    /**
     *b: Not new Note
     *d: Not Private Mode
     */
    @Test
    fun `On bind a and d`() {
//        logic = getLogic("", true)
//
//        every {
//            view.getTime()
//        } returns getNote().creationDate
//
//        every {
//            vModel.getId()
//        } returns ""
//
//        every {
//            vModel.getIsPrivateMode()
//        } returns true
//
//        logic.bind()
//
//        //creatorId should be null for new note. It will be added if the user saves the note while
//        //logged in
//        verify { vModel.setNoteState(getNote(creatorId = null, contents = "")) }
//        verify { vModel.setIsPrivateMode(true) }
//        verify { vModel.setId("") }
//        verify { vModel.setId(getNote().creationDate) }
//        verify { view.getTime() }
//        verify { view.hideBackButton() }
    }

    /**
     *b: Not new Note
     *c: isPrivate = true
     *
     * 1. Check arguments from activity: note id = note id, isPrivate = true
     * 2. Retrieve Note from appropriate repository
     * 3. render view
     * - back set to invisible (only delete or save allowed for new notes
     * - start satellite animation
     * - set creation date
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

        every {
            vModel.getNoteState()
        } returns null

        every {
            dispatcher.provideUIContext()
        } returns Dispatchers.Unconfined

        coEvery {
            private.getNoteById(getNote().creationDate, locator)
        } returns Result.build { getNote() }

        logic.event(NoteDetailEvent.OnBind)

        verify { vModel.setIsPrivateMode(true) }
        verify { vModel.setId(getNote().creationDate) }
        verify { vModel.setNoteState(getNote()) }
        coVerify { private.getNoteById(getNote().creationDate, locator) }
    }

    /**
     *b: Not new Note
     *d: public mode
     */
    @Test
    fun `On bind b and d`() {

    }
}
