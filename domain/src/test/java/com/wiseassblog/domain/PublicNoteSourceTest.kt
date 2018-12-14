package com.wiseassblog.domain

import com.wiseassblog.domain.interactor.PublicNoteSource
import io.mockk.mockk
import org.junit.jupiter.api.Test

class PublicNoteSourceTest {


    val source = PublicNoteSource()

    val locator: NoteServiceLocator = mockk()


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