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
import com.wiseassblog.spacenotes.common.MODE_PRIVATE
import com.wiseassblog.spacenotes.notelist.INoteListContract
import com.wiseassblog.spacenotes.notelist.NoteListAdapter
import com.wiseassblog.spacenotes.notelist.NoteListEvent
import com.wiseassblog.spacenotes.notelist.NoteListLogic
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.BeforeEach

class NoteListLogicTest {

    private val dispatcher: DispatcherProvider = mockk()

    private val locator: ServiceLocator = mockk()

    private val vModel: INoteListContract.ViewModel = mockk(relaxed = true)

    private val adapter: NoteListAdapter = mockk(relaxed = true)


    private val view: INoteListContract.View = mockk(relaxed = true)

    private val private: PrivateNoteSource = mockk()

    private val public: PublicNoteSource = mockk()

    private val auth: AuthSource = mockk()


    private lateinit var logic: NoteListLogic


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

    fun getUser(uid: String = "8675309",
                name: String = "Ajahn Chah",
                profilePicUrl: String = ""
    ) = User(uid,
            name,
            profilePicUrl)

    val getNoteList = listOf<Note>(
            getNote(),
            getNote(),
            getNote()
    )


    @Before
    fun build() {

        logic = NoteListLogic(
                dispatcher,
                locator,
                vModel,
                adapter,
                view,
                private,
                public,
                auth
        )

        every { dispatcher.provideUIContext() } returns Dispatchers.Unconfined

    }

    @BeforeEach
    fun clear() {
        clearMocks()

    }

    /**
     * New Note events will have two possible event streams, based on whether or not the auth is
     * in "public mode", and Public mode is only available when the auth is logged in.
     * a: User is in private storage mode (logged in or not)
     *
     * 1a. check isPrivate status on vModel: false
     * 2a. startDetailActivity with empty string as extra
     */
    @Test
    fun `On New Note Click a`() {
        //prepare mock interactions
        every { vModel.getIsPrivateMode() } returns true

        //call the unit to be tested
        logic.event(NoteListEvent.OnNewNoteClick)

        //verify interactions and state if necessary
        verify { vModel.getIsPrivateMode() }
        verify { view.startDetailActivity("", true) }
    }

    /**
     * b: auth is logged in, and in private mode
     *
     * 1b. check isPrivate status on vModel: true
     * 2b. pass empty string and true as extra

     */
    @Test
    fun `On New Note Click b`() {
        every { vModel.getIsPrivateMode() } returns false

        //call the unit to be tested
        logic.event(NoteListEvent.OnNewNoteClick)

        //verify interactions and state if necessary
        verify { vModel.getIsPrivateMode() }
        verify { view.startDetailActivity("", false) }

    }

    /**
     * c: auth is logged in, and in public mode
     *
     * This will be implemented in a later iteration
     */
    @Test
    fun `On New Note Click c`() = runBlocking {
        //        every {vModel.getUserState()} returns getUser()
//
//        //call the unit to be tested
//        logic.event(NoteListEvent.OnNewNoteClick)
//
//        //verify interactions and state if necessary
//        verify { vModel.getUserState() }
//        verify { view.startDetailActivity(getUser().uid, true) }

    }


    /**
     * On bind process, called by view in onCreate:
     * 1. Display Loading View
     * 2. Check for a logged in auth
     * 3a. if User logged in, write that auth to vM
     * 3b. if User not logged in, leave auth null in vM
     * 4. call On start process
     * 5.
     */
    @Test
    fun `On bind a`() = runBlocking {

        every { dispatcher.provideUIContext() } returns Dispatchers.Unconfined

        coEvery { auth.getCurrentUser(locator) } returns Result.build { getUser() }

        logic.bind()

        coVerify { auth.getCurrentUser(locator) }
        verify { vModel.setUserState(getUser()) }
        verify { view.showLoadingView() }
        verify { view.setToolbarTitle(MODE_PRIVATE) }
        verify { view.setAdapter(adapter) }
        verify { adapter.logic = logic }

    }

    @Test
    fun `On bind b`() = runBlocking {
        every { dispatcher.provideUIContext() } returns Dispatchers.Unconfined


        coEvery { auth.getCurrentUser(locator) } returns Result.build { null }

        logic.bind()

        coVerify { auth.getCurrentUser(locator) }
        verify { vModel.setUserState(null) }
        verify { view.showLoadingView() }
        verify { view.setAdapter(adapter) }
        verify { view.setToolbarTitle(MODE_PRIVATE) }
        verify { adapter.logic = logic }
    }

    /**
     *
     * On start basically means that we want to render the UI. This depends on whether the auth
     * is currently logged in, and if they are in private mode or not:
     * a. auth is logged in and in private mode
     * b. no auth found, start in private mode
     * c. auth is logged in and in public mode
     *
     * a:
     *1. Check isPrivate status: true
     *2. Check login status in backend if necessary
     *3. parse datasources accordingly
     *4. draw view accordingly
     */
    @Test
    fun `On Start a`() = runBlocking {
        every { dispatcher.provideUIContext() } returns Dispatchers.Unconfined
        every { vModel.getIsPrivateMode() } returns true
        coEvery { auth.getCurrentUser(locator) } returns Result.build { getUser() }
        coEvery { private.getNotes(locator) } returns Result.build { getNoteList }

        logic.event(NoteListEvent.OnStart)

        verify { vModel.getIsPrivateMode() }
        verify { view.showList() }
        verify { adapter.submitList(getNoteList) }
        coVerify { private.getNotes(locator) }
    }

    /**
     * b:
     *1. Check isPrivate status: false
     *2. Check login status in backend if necessary
     *3. parse datasources accordingly
     *4. draw view accordingly
     *
     */
    @Test
    fun `On Start b`() = runBlocking {
        every { dispatcher.provideUIContext() } returns Dispatchers.Unconfined
        every { vModel.getIsPrivateMode() } returns true
        coEvery { auth.getCurrentUser(locator) } returns Result.build { null }
        coEvery { private.getNotes(locator) } returns Result.build { getNoteList }

        logic.event(NoteListEvent.OnStart)

        verify { vModel.getIsPrivateMode() }
        verify { view.showList() }
        verify { adapter.submitList(getNoteList) }
        coVerify { private.getNotes(locator) }
    }

    /**
     * c. auth is logged in and in public mode
     *1. Check auth status
     *2. Check isPrivate status
     *3.  parse datasources accordingly
     */
    @Test
    fun `On Start c`() = runBlocking {
        //        every { vModel.getUserState() } returns getUser()
//
//        logic.event(NoteListEvent.OnStart)
//
//        verify { vModel.getUserState() }
//        verify { view.startDetailActivity("", true) }
    }


    /**
     * On login click, send auth to Auth Activity in order to manage their login status
     *
     *1. start login activity
     */
    @Test
    fun `On Login Click `() {

        logic.event(NoteListEvent.OnLoginClick)

        verify { view.startUserAuthActivity() }
    }

    /**
     * On Note Item Click, auth wishes to navigate to a detailed view of the selected item
     *a: isPrivate = true
     *1. Get appropriate Note from vModel
     *2. Get isPrivate from vModel
     *2. Start detail Activity with note id passed as extra, and isPrivate result
     */
    @Test
    fun `On Note Item Click a`() = runBlocking {


        every { vModel.getIsPrivateMode() } returns true
        every { vModel.getAdapterState() } returns getNoteList

        //auth selects first item in adapter
        val clickEvent = NoteListEvent.OnNoteItemClick(0)

        logic.event(clickEvent)

        verify { view.startDetailActivity(getNote().creationDate, true) }
        verify { vModel.getAdapterState() }
        verify { vModel.getIsPrivateMode() }
    }


    /**
     *b: isPrivate = false
     *1. Get appropriate Note from vModel
     *2. Get isPrivate from vModel
     *2. Start detail Activity with note id passed as extra, and isPrivate result
     */
    @Test
    fun `On Note Item Click b`() = runBlocking {

        every { vModel.getIsPrivateMode() } returns false
        every { vModel.getAdapterState() } returns getNoteList

        //auth selects first item in adapter
        val clickEvent = NoteListEvent.OnNoteItemClick(0)

        logic.event(clickEvent)

        verify { view.startDetailActivity(getNote().creationDate, false) }
        verify { vModel.getAdapterState() }
        verify { vModel.getIsPrivateMode() }
    }

}