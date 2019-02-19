package com.wiseassblog.spacenotes.notelist

import androidx.lifecycle.Observer
import com.wiseassblog.domain.DispatcherProvider
import com.wiseassblog.domain.servicelocator.NoteServiceLocator
import com.wiseassblog.domain.servicelocator.UserServiceLocator
import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.error.SpaceNotesError
import com.wiseassblog.domain.interactor.AnonymousNoteSource
import com.wiseassblog.domain.interactor.AuthSource
import com.wiseassblog.domain.interactor.PublicNoteSource
import com.wiseassblog.domain.interactor.RegisteredNoteSource
import com.wiseassblog.spacenotes.common.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class NoteListLogic(dispatcher: DispatcherProvider,
                    val noteLocator: NoteServiceLocator,
                    val userLocator: UserServiceLocator,
                    val vModel: INoteListContract.ViewModel,
                    var adapter: NoteListAdapter,
                    val view: INoteListContract.View,
                    val anonymousNoteSource: AnonymousNoteSource,
                    val registeredNoteSource: RegisteredNoteSource,
                    val publicNoteSource: PublicNoteSource,
                    val authSource: AuthSource)
    : BaseLogic(dispatcher), CoroutineScope, Observer<NoteListEvent<Int>> {
    override fun onChanged(event: NoteListEvent<Int>?) {
        when (event) {
            is NoteListEvent.OnNoteItemClick -> onNoteItemClick(event.position)
            is NoteListEvent.OnNewNoteClick -> onNewNoteClick()
            is NoteListEvent.OnLoginClick -> onLoginClick()
            is NoteListEvent.OnTogglePublicMode -> onTogglePublicMode()
            is NoteListEvent.OnStart -> onStart()
            is NoteListEvent.OnBind -> bind()
            is NoteListEvent.OnDestroy -> clear()
        }
    }

    init {
        //This is directly analogous to CompositeDisposable
        jobTracker = Job()
    }

    //dispatcher.provideUIContext is very analogous to observeOn(Dispatchers.UI)
    override val coroutineContext: CoroutineContext
        get() = dispatcher.provideUIContext() + jobTracker

    private fun onNewNoteClick() = view.startNoteDetailFeatureWithExtras(
            "",
            vModel.getIsPrivateMode()
    )

    private fun onStart() {
        getListData(vModel.getIsPrivateMode())
    }

    fun getListData(isPrivateMode: Boolean) = launch {
        val dataResult: Result<Exception, List<Note>>

        when (isPrivateMode) {
            true -> dataResult = getPrivateListData()
            false -> dataResult = getPublicListData()
        }

        when (dataResult) {
            is Result.Value -> {
                vModel.setAdapterState(dataResult.value)
                renderView(dataResult.value)
            }
            is Result.Error -> {
                view.showEmptyState()
                view.showErrorState(MESSAGE_GENERIC_ERROR)
            }
        }
    }

    suspend fun getPublicListData(): Result<Exception, List<Note>> {
        return if (vModel.getUserState() != null) publicNoteSource.getNotes(noteLocator)
        else Result.build { throw SpaceNotesError.LocalIOException }
    }

    suspend fun getPrivateListData(): Result<Exception, List<Note>> {
        return if (vModel.getUserState() == null) anonymousNoteSource.getNotes(noteLocator)
        else registeredNoteSource.getNotes(noteLocator)
    }

    fun renderView(list: List<Note>) {
        view.setPrivateIcon(vModel.getIsPrivateMode())
        if (vModel.getIsPrivateMode()) view.setToolbarTitle(MODE_PRIVATE)
        else view.setToolbarTitle(MODE_PUBLIC)

        if (list.isEmpty()) view.showEmptyState()
        else view.showList()

        adapter.submitList(list)
    }

    private fun onTogglePublicMode() {
        if (vModel.getUserState() != null) {
            if (vModel.getIsPrivateMode()) {
                vModel.setIsPrivateMode(false)
                getListData(false)
            } else {
                vModel.setIsPrivateMode(true)
                getListData(true)
            }
        } else {
            view.showErrorState(MESSAGE_LOGIN)
        }

    }

    private fun onLoginClick() {
        view.startLoginFeature()
    }

    private fun onNoteItemClick(position: Int) {
        val listData = vModel.getAdapterState()

        view.startNoteDetailFeatureWithExtras(
                listData[position].creationDate, vModel.getIsPrivateMode())
    }


    fun bind() {
        view.setToolbarTitle(MODE_PRIVATE)
        view.showLoadingView()
        adapter.setObserver(this)
        view.setAdapter(adapter)
        view.setObserver(this)

        launch {
            val result = authSource.getCurrentUser(userLocator)
            if (result is Result.Value) vModel.setUserState(result.value)
            //otherwise defaults to null
        }
    }

    //Single Expression Syntax
    fun clear() = jobTracker.cancel()

}
