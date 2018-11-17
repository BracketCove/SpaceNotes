package com.wiseassblog.spacenotes.notedetail

import com.wiseassblog.domain.domainmodel.Note


/**
 * Created by R_KAY on 10/8/2017.
 */
interface INoteDetailContract {

    interface View {
        fun setBackgroundImage(imageUrl: String)
        fun setDateLabel(date: String)
        fun setNoteBody(content: String)
        fun hideBackButton()
        fun getNoteBody(): String
        fun getTime(): String
        fun startListFeature()
        fun restartFeature()
        fun showMessage(message: String)
        fun showConfirmDeleteSnackbar()
    }

    interface ViewModel {
        fun setIsPrivateMode(isPrivateMode: Boolean)

        fun getIsPrivateMode(): Boolean

        fun setNoteState(note: Note)

        fun getNoteState(): Note?

        fun setId(id: String)

        fun getId(): String?
    }

    interface Logic {
        fun event(event: NoteDetailEvent)
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

