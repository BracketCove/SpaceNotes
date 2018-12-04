package com.wiseassblog.spacenotes.buildlogic

import android.content.Context
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.FirebaseApp
import com.wiseassblog.data.auth.FirebaseAuthRepositoryImpl
import com.wiseassblog.data.note.*
import com.wiseassblog.domain.DispatcherProvider
import com.wiseassblog.domain.ServiceLocator
import com.wiseassblog.domain.interactor.AnonymousNoteSource
import com.wiseassblog.domain.interactor.AuthSource
import com.wiseassblog.domain.interactor.PublicNoteSource
import com.wiseassblog.domain.interactor.RegisteredNoteSource
import com.wiseassblog.domain.repository.IAuthRepository
import com.wiseassblog.domain.repository.ILocalNoteRepository
import com.wiseassblog.spacenotes.login.ILoginContract
import com.wiseassblog.spacenotes.login.LoginActivity
import com.wiseassblog.spacenotes.login.LoginLogic
import com.wiseassblog.spacenotes.notedetail.*
import com.wiseassblog.spacenotes.notelist.*

class Injector(private val activityContext: Context) {
    init {
        FirebaseApp.initializeApp(activityContext)
    }

    //For non-registered user persistence
    private val localAnon: ILocalNoteRepository by lazy {
        RoomLocalAnonymousDatabase(noteDao)
    }

    //For registered user remote persistence (Source of Truth)
    private val remoteReg: ILocalNoteRepository by lazy {
        FirebaseNoteRepositoryImpl()
    }

    //For registered user local persistience (cache)
    private val cacheReg: ILocalNoteRepository by lazy {
        RoomLocalRegisteredCacheImpl(noteDao)
    }

    //For user management
    private val auth: IAuthRepository by lazy {
        FirebaseAuthRepositoryImpl()
    }

    private val noteDao: RoomNoteDao by lazy {
        AnonymousNoteDatabase.getInstance(activityContext).roomNoteDao()
    }

    fun provideNoteListLogic(view: NoteListView): INoteListContract.Logic {
        return NoteListLogic(
                DispatcherProvider,
                ServiceLocator(localAnon, remoteReg, cacheReg, auth),
                ViewModelProviders.of(activityContext as NoteListActivity).get(NoteListViewModel::class.java),
                NoteListAdapter(),
                view,
                AnonymousNoteSource(),
                RegisteredNoteSource(),
                PublicNoteSource(),
                AuthSource()
        )
    }

    fun provideLoginLogic(view: LoginActivity): ILoginContract.Logic {
        return LoginLogic(
                DispatcherProvider,
                ServiceLocator(localAnon, remoteReg, cacheReg, auth),
                view,
                AuthSource()
        )
    }

    fun provideNoteDetailLogic(view: NoteDetailView, id: String, isPrivate:Boolean): INoteDetailContract.Logic {
        return NoteDetailLogic(
                DispatcherProvider,
                ServiceLocator(localAnon, remoteReg, cacheReg, auth),
                ViewModelProviders.of(activityContext as NoteDetailActivity)
                        .get(NoteDetailViewModel::class.java),
                view,
                AnonymousNoteSource(),
                RegisteredNoteSource(),
                PublicNoteSource(),
                AuthSource(),
                id,
                isPrivate
        )
    }
}