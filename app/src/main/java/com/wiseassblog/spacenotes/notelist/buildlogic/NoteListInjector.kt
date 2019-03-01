package com.wiseassblog.spacenotes.notelist.buildlogic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.FirebaseApp
import com.wiseassblog.data.auth.FirebaseAuthRepositoryImpl
import com.wiseassblog.data.note.anonymous.AnonymousNoteDao
import com.wiseassblog.data.note.anonymous.AnonymousNoteDatabase
import com.wiseassblog.data.note.anonymous.RoomLocalAnonymousRepositoryImpl
import com.wiseassblog.data.note.registered.*
import com.wiseassblog.data.transaction.RoomRegisteredTransactionDatabase
import com.wiseassblog.data.transaction.RoomTransactionRepositoryImpl
import com.wiseassblog.domain.DispatcherProvider
import com.wiseassblog.domain.interactor.AnonymousNoteSource
import com.wiseassblog.domain.interactor.AuthSource
import com.wiseassblog.domain.interactor.RegisteredNoteSource
import com.wiseassblog.domain.repository.IAuthRepository
import com.wiseassblog.domain.repository.ILocalNoteRepository
import com.wiseassblog.domain.repository.IRemoteNoteRepository
import com.wiseassblog.domain.repository.ITransactionRepository
import com.wiseassblog.domain.servicelocator.NoteServiceLocator
import com.wiseassblog.domain.servicelocator.UserServiceLocator
import com.wiseassblog.spacenotes.notelist.NoteListAdapter
import com.wiseassblog.spacenotes.notelist.NoteListLogic
import com.wiseassblog.spacenotes.notelist.NoteListView
import com.wiseassblog.spacenotes.notelist.NoteListViewModel

class NoteListInjector(application: Application) : AndroidViewModel(application) {
    init {
        FirebaseApp.initializeApp(application)
    }

    private val anonNoteDao: AnonymousNoteDao by lazy {
        AnonymousNoteDatabase.getInstance(application).roomNoteDao()
    }

    private val regNoteDao: RegisteredNoteDao by lazy {
        RegisteredNoteDatabase.getInstance(application).roomNoteDao()
    }

    private val transactionDao: RegisteredTransactionDao by lazy {
        RoomRegisteredTransactionDatabase.getInstance(application).roomTransactionDao()
    }

    //For non-registered user persistence
    private val localAnon: ILocalNoteRepository by lazy {
        RoomLocalAnonymousRepositoryImpl(anonNoteDao)
    }

    //For registered user remote persistence (Source of Truth)
    private val remotePrivate: IRemoteNoteRepository by lazy {
        FirestorePrivateRemoteNoteImpl()
    }

    //For registered user local persistience (cache)
    private val cacheReg: ILocalNoteRepository by lazy {
        RoomLocalCacheImpl(regNoteDao)
    }

    //For registered user remote persistence (Source of Truth)
    private val remotePrivateRepo: IRemoteNoteRepository by lazy {
        RegisteredNoteRepositoryImpl(remotePrivate, cacheReg)
    }

    //For registered user local persistience (cache)
    private val transactionReg: ITransactionRepository by lazy {
        RoomTransactionRepositoryImpl(transactionDao)
    }

    //For user management
    private val auth: IAuthRepository by lazy {
        FirebaseAuthRepositoryImpl()
    }


    private lateinit var logic: NoteListLogic

    fun buildNoteListLogic(view: NoteListView): NoteListLogic {
        logic = NoteListLogic(
                DispatcherProvider,
                NoteServiceLocator(localAnon, remotePrivateRepo, transactionReg),
                UserServiceLocator(auth),
                ViewModelProviders.of(view)
                        .get(NoteListViewModel::class.java),
                NoteListAdapter(),
                view,
                AnonymousNoteSource(),
                RegisteredNoteSource(),
                AuthSource()
        )

        view.setObserver(logic)

        return logic
    }
}