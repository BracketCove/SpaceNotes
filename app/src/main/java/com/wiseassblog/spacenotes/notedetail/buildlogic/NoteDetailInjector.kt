package com.wiseassblog.spacenotes.notedetail.buildlogic

import android.app.Application
import android.content.Context
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
import com.wiseassblog.domain.NoteServiceLocator
import com.wiseassblog.domain.UserServiceLocator
import com.wiseassblog.domain.interactor.AnonymousNoteSource
import com.wiseassblog.domain.interactor.AuthSource
import com.wiseassblog.domain.interactor.PublicNoteSource
import com.wiseassblog.domain.interactor.RegisteredNoteSource
import com.wiseassblog.domain.repository.IAuthRepository
import com.wiseassblog.domain.repository.ILocalNoteRepository
import com.wiseassblog.domain.repository.IRemoteNoteRepository
import com.wiseassblog.domain.repository.ITransactionRepository
import com.wiseassblog.spacenotes.notedetail.*
import com.wiseassblog.spacenotes.notelist.NoteListLogic

/**
 *
 */
class NoteDetailInjector(application: Application) : AndroidViewModel(application) {
    init {
        FirebaseApp.initializeApp(application)
    }

    private val anonNoteDao: AnonymousNoteDao by lazy {
        AnonymousNoteDatabase.getInstance(getApplication()).roomNoteDao()
    }

    private val regNoteDao: RegisteredNoteDao by lazy {
        RegisteredNoteDatabase.getInstance(getApplication()).roomNoteDao()
    }

    private val transactionDao: RegisteredTransactionDao by lazy {
        RoomRegisteredTransactionDatabase.getInstance(getApplication()).roomTransactionDao()
    }

    //For non-registered user persistence
    private val localAnon: ILocalNoteRepository by lazy {
        RoomLocalAnonymousRepositoryImpl(anonNoteDao)
    }

    //For registered user remote persistence (Source of Truth)
    private val remoteReg: IRemoteNoteRepository by lazy {
        FirestoreRemoteNoteImpl()
    }

    //For registered user local persistience (cache)
    private val cacheReg: ILocalNoteRepository by lazy {
        RoomLocalCacheImpl(regNoteDao)
    }

    //For registered user remote persistence (Source of Truth)
    private val remoteRepo: IRemoteNoteRepository by lazy {
        RegisteredNoteRepositoryImpl(remoteReg, cacheReg)
    }


    //For registered user local persistience (cache)
    private val transactionReg: ITransactionRepository by lazy {
        RoomTransactionRepositoryImpl(transactionDao)
    }

    //For user management
    private val auth: IAuthRepository by lazy {
        FirebaseAuthRepositoryImpl()
    }

    private lateinit var logic: NoteDetailLogic

    fun buildNoteDetailLogic(view: NoteDetailView,
                             id: String,
                             isPrivate: Boolean): NoteDetailLogic {
        logic = NoteDetailLogic(
                DispatcherProvider,
                NoteServiceLocator(localAnon, remoteRepo, transactionReg),
                UserServiceLocator(auth),
                ViewModelProviders.of(view)
                        .get(NoteDetailViewModel::class.java),
                view,
                AnonymousNoteSource(),
                RegisteredNoteSource(),
                PublicNoteSource(),
                AuthSource(),
                id,
                isPrivate
        )

        view.setObserver(logic)

        return logic
    }
}