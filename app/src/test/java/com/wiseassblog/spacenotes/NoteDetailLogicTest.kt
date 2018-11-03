package com.wiseassblog.spacenotes

import com.wiseassblog.domain.ServiceLocator
import com.wiseassblog.domain.domainmodel.ColorType
import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.domainmodel.User
import com.wiseassblog.domain.interactor.PrivateNoteSource
import com.wiseassblog.domain.interactor.PublicNoteSource
import com.wiseassblog.domain.interactor.AuthSource
import com.wiseassblog.spacenotes.common.DispatcherProvider
import com.wiseassblog.spacenotes.notedetail.INoteDetailContract
import com.wiseassblog.spacenotes.notedetail.MESSAGE_DELETE_SUCCESSFUL
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
    fun getNote(creationDate: String = "28/10/2018",
                contents: String = "When I understand that this glass is already broken, every moment with it becomes precious.",
                upVotes: Int = 0,
                color: ColorType = ColorType.GREEN,
                creator: User? = User(
                        "8675309",
                        "Ajahn Chah",
                        ""
                )
    ) = Note(
            creationDate = creationDate,
            contents = contents,
            upVotes = upVotes,
            color = color,
            creator = creator
    )


    @Before
    fun clear() {
        clearMocks()

        logic = NoteDetailLogic(
                dispatcher,
                locator,
                vModel,
                view,
                private,
                public,
                auth,
                getNote().creationDate
        )

    }

    /**
     * When auth presses done, they are finished editing their note. They will be returned to a list
     * view of all notes.
     *
     * 1. get current value of noteBody
     * 2. write updated note to repositories
     * 3. exit to list activity
     */
    @Test
    fun `On Done Click`() = runBlocking {

        every {
            view.getNoteBody()
        } returns getNote().contents


        every {
            vModel.getDisplayState()
        } returns getNote()

        every {
            dispatcher.provideUIContext()
        } returns Dispatchers.Unconfined

        coEvery {
            private.updateNote(getNote(), locator)
        } returns Result.build { true }

        //call the unit to be tested
        logic.noteDetailEvent(NoteDetailEvent.OnDoneClick)

        //verify interactions and state if necessary

        verify { view.getNoteBody() }
        verify { vModel.getDisplayState() }
        verify { view.startListFeature() }
    }

    /**
     * When auth presses delete, they may wish to delete a note. Show confirmation.
     */
    @Test
    fun `On Delete Click`() = runBlocking {
        every {
            view.showConfirmDeleteSnackbar()
        } returns Unit

        logic.noteDetailEvent(NoteDetailEvent.OnDeleteClick)

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
            vModel.getDisplayState()
        } returns getNote()

        every {
            dispatcher.provideUIContext()
        } returns Dispatchers.Unconfined

        coEvery {
            private.deleteNote(getNote().creationDate, locator)
        } returns Result.build { true }

        logic.noteDetailEvent(NoteDetailEvent.OnDeleteConfirmed)

        verify { vModel.getDisplayState() }
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
            vModel.getDisplayState()
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

        logic.noteDetailEvent(NoteDetailEvent.OnStart)

        verify { vModel.setDisplayState(any()) }
        coVerify { private.getNoteById(getNote().creationDate, locator) }
        verify { view.setBackgroundImage(getNote().color) }
        verify { view.setDateLabel(getNote().creationDate) }
        verify { view.setNoteBody(getNote().contents) }
    }

    @Test
    fun `On start b`() {
        every {
            vModel.getDisplayState()
        } returns getNote()

        logic.noteDetailEvent(NoteDetailEvent.OnStart)

        verify { vModel.getDisplayState() }
        verify { view.setBackgroundImage(getNote().color) }
        verify { view.setDateLabel(getNote().creationDate) }
        verify { view.setNoteBody(getNote().contents) }
    }


    @Test
    fun `On Back Click`() {
        logic.noteDetailEvent(NoteDetailEvent.OnBackClick)

        verify { view.startListFeature() }
    }

}
