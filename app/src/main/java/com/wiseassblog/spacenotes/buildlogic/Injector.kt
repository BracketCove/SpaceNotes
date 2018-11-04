package com.wiseassblog.spacenotes.buildlogic

import android.content.Context
import androidx.lifecycle.ViewModelProviders
import com.wiseassblog.data.note.FirebaseNoteRepositoryImpl
import com.wiseassblog.data.note.FirebaseAuthSourceImpl
import com.wiseassblog.data.note.RoomNoteRepositoryImpl
import com.wiseassblog.domain.ServiceLocator
import com.wiseassblog.domain.interactor.PrivateNoteSource
import com.wiseassblog.domain.interactor.PublicNoteSource
import com.wiseassblog.domain.interactor.AuthSource
import com.wiseassblog.domain.repository.INoteRepository
import com.wiseassblog.domain.repository.IAuthSource
import com.wiseassblog.spacenotes.common.DispatcherProvider
import com.wiseassblog.spacenotes.notedetail.*
import com.wiseassblog.spacenotes.notelist.*

class Injector(private val activityContext: Context) {

    //must be val
    private val local: INoteRepository by lazy {
        RoomNoteRepositoryImpl()
    }

    private val remote: INoteRepository by lazy {
        FirebaseNoteRepositoryImpl()
    }

    private val auth: IAuthSource by lazy {
        FirebaseAuthSourceImpl()
    }

    fun provideNoteListLogic(view: NoteListView): INoteListContract.Logic {
        return NoteListLogic(
                DispatcherProvider,
                ServiceLocator(local, remote, auth),
                ViewModelProviders.of(activityContext as NoteListActivity).get(NoteListViewModel::class.java),
                NoteListAdapter(),
                view,
                PrivateNoteSource(),
                PublicNoteSource(),
                AuthSource()
        )
    }

    fun provideNoteDetailLogic(view: NoteDetailView, id: String, isPrivate:Boolean): INoteDetailContract.Logic {
        return NoteDetailLogic(
                DispatcherProvider,
                ServiceLocator(local, remote, auth),
                ViewModelProviders.of(activityContext as NoteDetailActivity)
                        .get(NoteDetailViewModel::class.java),
                view,
                PrivateNoteSource(),
                PublicNoteSource(),
                AuthSource(),
                id,
                isPrivate
        )
    }
}