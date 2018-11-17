package com.wiseassblog.spacenotes.notelist

import com.wiseassblog.domain.ServiceLocator
import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.interactor.AuthSource
import com.wiseassblog.domain.interactor.PrivateNoteSource
import com.wiseassblog.domain.interactor.PublicNoteSource
import com.wiseassblog.spacenotes.common.BaseLogic
import com.wiseassblog.domain.DispatcherProvider
import com.wiseassblog.spacenotes.common.MODE_PRIVATE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class NoteListLogic(dispatcher: DispatcherProvider,
                    locator: ServiceLocator,
                    val vModel: INoteListContract.ViewModel,
                    var adapter: NoteListAdapter,
                    val view: INoteListContract.View,
                    val privateNoteSource: PrivateNoteSource,
                    val publicNoteSource: PublicNoteSource,
                    val authSource: AuthSource)
    : BaseLogic(dispatcher, locator),
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

    private fun onNewNoteClick() {
        val isPrivate = vModel.getIsPrivateMode()

        view.startDetailActivity("", isPrivate)

    }

    private fun getDate(): String {
        return System.currentTimeMillis().toString()
    }

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

    suspend fun getPublicListData():Result<Exception, List<Note>>  {
        return publicNoteSource.getNotes(locator)
    }

    suspend fun getPrivateListData():Result<Exception, List<Note>> {
        return privateNoteSource.getNotes(locator)
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
        view.startUserAuthActivity()
    }

    private fun onNoteItemClick(position: Int) {
        val listData = vModel.getAdapterState()

        view.startDetailActivity(listData[position].creationDate, vModel.getIsPrivateMode())
    }


     fun bind() {
        view.setToolbarTitle(MODE_PRIVATE)
        view.showLoadingView()
        adapter.logic = this
        view.setAdapter(adapter)

        launch {
            val result = authSource.getCurrentUser(locator)

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
