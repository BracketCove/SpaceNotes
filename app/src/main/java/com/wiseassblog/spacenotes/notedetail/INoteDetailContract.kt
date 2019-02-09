package com.wiseassblog.spacenotes.notedetail

import androidx.lifecycle.Observer
import com.wiseassblog.domain.domainmodel.Note


/**
 * Created by R_KAY on 10/8/2017.
 */
interface INoteDetailContract {

    interface View {
        fun setBackgroundImage(imageUrl: String)
        fun setDateLabel(date: String)
        fun setNoteBody(content: String)
        fun setObserver(observer: Observer<NoteDetailEvent>)
        fun hideBackButton()
        fun getNoteBody(): String
        fun getTime(): String
        fun restartFeature()
        fun showMessage(message: String)
        fun showConfirmDeleteSnackbar()
        fun startListFeature()
    }

    interface ViewModel {
        fun setIsPrivateMode(isPrivateMode: Boolean)

        fun getIsPrivateMode(): Boolean

        fun setNoteState(note: Note)

        fun getNoteState(): Note?

        fun setId(id: String)

        fun getId(): String?
    }
}

sealed class NoteDetailEvent {
    object OnDoneClick : NoteDetailEvent()
    object OnDeleteClick : NoteDetailEvent()
    object OnDeleteConfirmed : NoteDetailEvent()
    object OnBackClick : NoteDetailEvent()
    object OnStart : NoteDetailEvent()
    object OnBind : NoteDetailEvent()
    object OnDestroy : NoteDetailEvent()
}

