package com.wiseassblog.spacenotes.notedetail

import androidx.lifecycle.Observer
import com.wiseassblog.domain.DispatcherProvider
import com.wiseassblog.domain.NoteServiceLocator
import com.wiseassblog.domain.UserServiceLocator
import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.domainmodel.User
import com.wiseassblog.domain.interactor.AnonymousNoteSource
import com.wiseassblog.domain.interactor.AuthSource
import com.wiseassblog.domain.interactor.PublicNoteSource
import com.wiseassblog.domain.interactor.RegisteredNoteSource
import com.wiseassblog.spacenotes.common.BaseLogic
import com.wiseassblog.spacenotes.common.MESSAGE_DELETE_SUCCESSFUL
import com.wiseassblog.spacenotes.common.MESSAGE_GENERIC_ERROR
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class NoteDetailLogic(dispatcher: DispatcherProvider,
                      val noteLocator: NoteServiceLocator,
                      val userLocator: UserServiceLocator,
                      val vModel: INoteDetailContract.ViewModel,
                      val view: INoteDetailContract.View,
                      val anonymousNoteSource: AnonymousNoteSource,
                      val registeredNoteSource: RegisteredNoteSource,
                      val publicNoteSource: PublicNoteSource,
                      val authSource: AuthSource,
                      id: String,
                      isPrivate: Boolean)
    : BaseLogic(dispatcher), CoroutineScope, Observer<NoteDetailEvent> {

    init {
        vModel.setId(id)
        vModel.setIsPrivateMode(isPrivate)
        jobTracker = Job()
    }

    fun clear() {
        jobTracker.cancel()
    }

    override val coroutineContext: CoroutineContext
        get() = dispatcher.provideUIContext() + jobTracker

    override fun onChanged(event: NoteDetailEvent) {
        when (event) {
            is NoteDetailEvent.OnDoneClick -> onDoneClick()
            is NoteDetailEvent.OnDeleteClick -> onDeleteClick()
            is NoteDetailEvent.OnBackClick -> onBackClick()
            is NoteDetailEvent.OnDeleteConfirmed -> onDeleteConfirmed()
            is NoteDetailEvent.OnStart -> onStart()
            is NoteDetailEvent.OnBind -> bind()
            is NoteDetailEvent.OnDestroy -> clear()
        }
    }

    fun onDoneClick() = launch {

        val userResult = authSource.getCurrentUser(userLocator)

        when (userResult) {
            is Result.Value -> {
                //if null, user is anonymous
                if (userResult.value == null) prepareAnonymousRepoUpdate()
                else if (vModel.getIsPrivateMode()) prepareRegisteredRepoUpdate()
                else preparePublicRepoUpdate()
            }
        }

    }

    private suspend fun prepareAnonymousRepoUpdate() {
        val updatedNote = vModel.getNoteState()!!.copy(contents = view.getNoteBody())

        val result = anonymousNoteSource.updateNote(updatedNote, noteLocator)

        when (result) {
            is Result.Value -> view.startListFeature()
            is Result.Error -> view.showMessage(result.error.toString())
        }
    }

    suspend fun prepareRegisteredRepoUpdate() {

        val updatedNote = vModel.getNoteState()!!.copy(contents = view.getNoteBody())

        val result = registeredNoteSource.updateNote(updatedNote, noteLocator)

        when (result) {
            is Result.Value -> view.startListFeature()
            is Result.Error -> view.showMessage(result.error.toString())
        }
    }

    suspend fun preparePublicRepoUpdate() {

        val updatedNote = vModel.getNoteState()!!
                .copy(contents = view.getNoteBody())

        val result = publicNoteSource.updateNote(updatedNote, noteLocator)

        when (result) {
            is Result.Value -> view.startListFeature()
            is Result.Error -> view.showMessage(result.error.toString())
        }
    }

    fun bind() = launch {

        val userResult = authSource.getCurrentUser(userLocator)

        when (userResult) {
            is Result.Value -> {
                val id = vModel.getId()
                if (id == "" || id == null) createNewNote(userResult.value)
                else getNoteFromSource(id, userResult.value)
            }

            is Result.Error -> view.showMessage(userResult.error.toString())
        }
    }

    fun createNewNote(user: User?) {

        vModel.setNoteState(
                Note(
                        view.getTime(),
                        "",
                        0,
                        "satellite_beam",
                        user
                )
        )

        //only save or delete with new note
        view.hideBackButton()

        onStart()
    }

    fun getNoteFromSource(id: String, user: User?) = launch {
        val noteResult: Result<Exception, Note?>

        //private anonymous
        if (user == null) noteResult = anonymousNoteSource.getNoteById(id, noteLocator)
        //private registered
        else if (vModel.getIsPrivateMode()) noteResult = registeredNoteSource.getNoteById(id, noteLocator)
        //public registered
        else noteResult = publicNoteSource.getNoteById(id, noteLocator)

        when (noteResult) {
            is Result.Value -> {
                vModel.setNoteState(noteResult.value!!)
                onStart()
            }

            is Result.Error -> {
                val message = noteResult.error.message ?: "An error has occured."
                view.showMessage(message)
            }
        }
    }

    fun onStart() {
        val state = vModel.getNoteState()

        //LiveData requires null checks due to nullable return types
        if (state != null) {
            renderView(state)
        } else {
            view.showMessage(MESSAGE_GENERIC_ERROR)
            view.startListFeature()
        }
    }

    private fun renderView(state: Note) {
        view.setBackgroundImage(state.imageUrl)
        view.setDateLabel(state.creationDate)
        view.setNoteBody(state.contents)
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
            view.showMessage(MESSAGE_GENERIC_ERROR)
            view.restartFeature()
        } else {
            val userResult = authSource.getCurrentUser(userLocator)

            when (userResult) {
                is Result.Value -> {
                    if (userResult.value == null) prepareAnonymousRepoDelete(currentNote)
                    else if (vModel.getIsPrivateMode()) prepareRegisteredRepoDelete(currentNote)
                    else preparePublicRepoDelete(currentNote)
                }

                is Result.Error -> view.showMessage(userResult.error.toString())
            }
        }
    }

    private fun preparePublicRepoDelete(note: Note) = launch {
        val result = publicNoteSource.deleteNote(note, noteLocator)

        when (result) {
            is Result.Value -> {
                view.showMessage(MESSAGE_DELETE_SUCCESSFUL)
                view.startListFeature()
            }
            is Result.Error -> view.showMessage(result.error.toString())
        }
    }

    private fun prepareRegisteredRepoDelete(note: Note) = launch {
        val result = registeredNoteSource.deleteNote(note, noteLocator)

        when (result) {
            is Result.Value -> {
                view.showMessage(MESSAGE_DELETE_SUCCESSFUL)
                view.startListFeature()
            }
            is Result.Error -> view.showMessage(result.error.toString())
        }
    }

    private fun prepareAnonymousRepoDelete(note: Note) = launch {
        val result = anonymousNoteSource.deleteNote(note, noteLocator)

        when (result) {
            is Result.Value -> {
                view.showMessage(MESSAGE_DELETE_SUCCESSFUL)
                view.startListFeature()
            }
            is Result.Error -> view.showMessage(result.error.toString())
        }
    }


}