package com.wiseassblog.spacenotes.notelist

import com.wiseassblog.domain.DispatcherProvider
import com.wiseassblog.domain.NoteServiceLocator
import com.wiseassblog.domain.UserServiceLocator
import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.interactor.AnonymousNoteSource
import com.wiseassblog.domain.interactor.AuthSource
import com.wiseassblog.domain.interactor.PublicNoteSource
import com.wiseassblog.domain.interactor.RegisteredNoteSource
import com.wiseassblog.spacenotes.common.BaseLogic
import com.wiseassblog.spacenotes.common.MODE_PRIVATE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class NoteListLogic(dispatcher: DispatcherProvider,
                    val noteLocator: NoteServiceLocator,
                    val userLocator: UserServiceLocator,
                    val navigator: INoteListContract.Navigator,
                    val vModel: INoteListContract.ViewModel,
                    var adapter: NoteListAdapter,
                    val view: INoteListContract.View,
                    val anonymousNoteSource: AnonymousNoteSource,
                    val registeredNoteSource: RegisteredNoteSource,
                    val publicNoteSource: PublicNoteSource,
                    val authSource: AuthSource)
    : BaseLogic(dispatcher),
        INoteListContract.Logic, CoroutineScope {

    init {
        jobTracker = Job()
    }

    override val coroutineContext: CoroutineContext
        get() = dispatcher.provideUIContext() + jobTracker

    override fun event(event: NoteListEvent<Int>) {
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

    private fun onNewNoteClick() = navigator.startNoteDetailFeatureWithExtras("", vModel.getIsPrivateMode())

    private fun onStart() {
        //similar to CompositeDisposable from RxJava 2
        jobTracker = Job()
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
                //TODO() handle this error case
            }
        }
    }

    suspend fun getPublicListData(): Result<Exception, List<Note>> {
        return publicNoteSource.getNotes(noteLocator, dispatcher)
    }

    suspend fun getPrivateListData(): Result<Exception, List<Note>> {
        return if (vModel.getUserState() == null) anonymousNoteSource.getNotes(noteLocator, dispatcher)
        else registeredNoteSource.getNotes(noteLocator, dispatcher)
    }

    fun renderView(list: List<Note>) {
        if (list.isEmpty()) view.showEmptyState()
        else view.showList()

        adapter.submitList(list)
    }

    private fun onTogglePublicMode() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun onLoginClick() {
        navigator.startLoginFeature()
    }

    private fun onNoteItemClick(position: Int) {
        val listData = vModel.getAdapterState()

        navigator.startNoteDetailFeatureWithExtras(listData[position].creationDate, vModel.getIsPrivateMode())
    }


    fun bind() {
        view.setToolbarTitle(MODE_PRIVATE)
        view.showLoadingView()
        adapter.logic = this
        view.setAdapter(adapter)

        launch {
            val result = authSource.getCurrentUser(userLocator)

            when (result) {
                //Note: Null does not constitute a failure, just no user found
                is Result.Value -> {
                    vModel.setUserState(result.value)
                }
                is Result.Error -> {
                    TODO()
                }
            }
        }
    }


    fun clear() {
        jobTracker.cancel()
    }
}
