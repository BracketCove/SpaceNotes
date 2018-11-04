package com.wiseassblog.spacenotes.notedetail

import com.wiseassblog.domain.ServiceLocator
import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.interactor.AuthSource
import com.wiseassblog.domain.interactor.PrivateNoteSource
import com.wiseassblog.domain.interactor.PublicNoteSource
import com.wiseassblog.spacenotes.common.BaseLogic
import com.wiseassblog.spacenotes.common.DispatcherProvider
import com.wiseassblog.spacenotes.common.MESSAGE_DELETE_SUCCESSFUL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class NoteDetailLogic(dispatcher: DispatcherProvider,
                      locator: ServiceLocator,
                      val vModel: INoteDetailContract.ViewModel,
                      val view: INoteDetailContract.View,
                      val privateNoteSource: PrivateNoteSource,
                      val publicNoteSource: PublicNoteSource,
                      val authSource: AuthSource,
                      id: String,
                      isPrivate: Boolean)
    : BaseLogic(dispatcher, locator), INoteDetailContract.Logic, CoroutineScope {



    override fun bind() {
        if (vModel.getId() == "" || vModel.getId() == null){
            vModel.setNoteState(
                    Note(
                            view.getTime(),
                            "",
                            0,
                            "",
                            null
                    )
            )

            //only save or delete with new note
            view.hideBackButton()

        }

        noteDetailEvent(NoteDetailEvent.OnStart)
    }

    override fun clear() {
        jobTracker.cancel()
    }

    init {
        vModel.setId(id)
        vModel.setIsPrivateMode(isPrivate)
        jobTracker = Job()
    }

    override val coroutineContext: CoroutineContext
        get() = dispatcher.provideUIContext() + jobTracker


    override fun noteDetailEvent(event: NoteDetailEvent) {
        when (event) {
            is NoteDetailEvent.OnDoneClick -> onDoneClick()

            is NoteDetailEvent.OnDeleteClick -> onDeleteClick()

            is NoteDetailEvent.OnBackClick -> onBackClick()

            is NoteDetailEvent.OnDeleteConfirmed -> onDeleteConfirmed()

            is NoteDetailEvent.OnStart -> onStart()
        }
    }

    fun onDoneClick() = launch {
        val currentNote = vModel.getNoteState()

        //if VM data is null, we're in a bad spot
        if (currentNote == null) {
            view.restartFeature()
        } else {
            val updatedNote = currentNote.copy(contents = view.getNoteBody())
            val result = privateNoteSource.updateNote(updatedNote, locator)

            when (result) {
                is Result.Value -> view.startListFeature()
                is Result.Error -> view.showMessage(result.error.toString())
            }
        }
    }

    fun onStart() {
        if (jobTracker.isCancelled) jobTracker = Job()

        val state = vModel.getNoteState()

        //LiveData requires null checks due to nullable return types
        if (state == null) {
            val id = vModel.getId()

            if (id == null) {
                view.startListFeature()
            } else {
                getNoteFromSource(id)
            }
        } else {
            renderView(state)
        }
    }

    private fun renderView(state: Note) {
        view.setBackgroundImage(state.imageUrl)
        view.setDateLabel(state.creationDate)
        view.setNoteBody(state.contents)
    }

    fun getNoteFromSource(id: String) = launch {
        val result = privateNoteSource.getNoteById(id, locator)

        when (result) {
            is Result.Value -> {
                vModel.setNoteState(result.value)
                renderView(result.value)
            }

            is Result.Error -> {
                val message = result.error.message ?: "An error has occured."
                view.showMessage(message)
            }
        }
    }

    fun onBackClick() {
        view.startListFeature()
    }

    fun onDeleteClick() {
        view.showConfirmDeleteSnackbar()
    }

    fun onDeleteConfirmed() = launch {
        val currentNote = vModel.getNoteState()

        //if VM data is null, we're in a bad spot
        if (currentNote == null) {
            view.restartFeature()
        } else {
            val result = privateNoteSource.deleteNote(currentNote.creationDate, locator)

            when (result) {
                is Result.Value -> {
                    view.showMessage(MESSAGE_DELETE_SUCCESSFUL)
                    view.startListFeature()
                }
                is Result.Error -> view.showMessage(result.error.toString())
            }
        }
    }

}