package com.wiseassblog.domain

import com.wiseassblog.domain.interactor.RegisteredNoteSource
import io.mockk.mockk
import org.junit.jupiter.api.Test

/**
 * Registered Note Source is for users which have authenticated via appropriate sign in functions.
 * Registered users have access to:
 * - A remote repository to share notes across devices, which is the source of truth for state
 * - A local repository to cache the most recent snap shot of the remote data, and to store offline
 * transactions to be pushed to the remote database.
 */
class RegisteredNoteSourceTest {


    val source = RegisteredNoteSource()

    val locator: ServiceLocator = mockk()


    /**
     * Deciding which dataset to return to the view, is dependent on the user's login status,
     *
     * successful communication with the remote datasource, and
     *
     */
    @Test
    fun `On Get Notes `(){

    }

}
