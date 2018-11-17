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
import com.wiseassblog.spacenotes.login.ILoginContract
import com.wiseassblog.spacenotes.login.LoginEvent
import com.wiseassblog.spacenotes.login.LoginLogic
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
class LoginLogicTest {


    private val dispatcher: DispatcherProvider = mockk()

    private val locator: ServiceLocator = mockk()

    private val view: ILoginContract.View = mockk(relaxed = true)


    private val auth: AuthSource = mockk()


    private lateinit var logic: LoginLogic


    fun getUser(uid: String = "8675309",
                name: String = "Ajahn Chah",
                profilePicUrl: String = ""
    ) = User(uid,
            name,
            profilePicUrl)

    @Before
    fun clear() {
        clearMocks()

        logic = LoginLogic(dispatcher, locator, view, auth)

    }

    /**
     * In onstart, we ask firebase auth for the current user object. Based on that result we render
     * the ui appropriately.
     * a. User is retrieved successfully
     * b. User is null
     * c. Error: no network connectivity
     *
     * a:
     * 1. User object retrieved
     * 2. Start antenna loop
     *
     */
    @Test
    fun `On Start retrieve User`() = runBlocking {

        coEvery {
            auth.getCurrentUser(locator)
        } returns Result.build { getUser() }

        logic.event(LoginEvent.OnStart)


        coVerify { auth.getCurrentUser(locator) }
        verify { view.showSignedIn() }
    }

    /**
     *
     */
    @Test
    fun `On Start retrieve null`() = runBlocking {

        coEvery {
            auth.getCurrentUser(locator)
        } returns Result.build { null }

        logic.event(LoginEvent.OnStart)


        coVerify { auth.getCurrentUser(locator) }
        verify { view.showSignedIn() }
    }

    /**
     *
     */
//    @Test
//    fun `On Start retrieve network error`() = runBlocking {
//
//        coEvery {
//            auth.getCurrentUser(locator)
//        } returns Result.build { getUser() }
//
//        logic.event(LoginEvent.OnStart)
//
//
//        coVerify { auth.getCurrentUser(locator) }
//        verify { view.showSignedIn() }
//    }



}
