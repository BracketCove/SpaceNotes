package com.wiseassblog.spacenotes.notedetail

import com.wiseassblog.domain.domainmodel.ColorType
import com.wiseassblog.domain.domainmodel.Note


/**
 * Created by R_KAY on 10/8/2017.
 */
interface INoteDetailContract {

    interface View {
        fun setBackgroundImage(color: ColorType)
        fun setDateLabel(date: String)
        fun setNoteBody(content: String)
        fun getNoteBody(): String
        fun startListFeature()
        fun restartFeature()
        fun showMessage(message: String)
        fun showConfirmDeleteSnackbar()
    }

    interface ViewModel {
        fun setDisplayState(note: Note)

        fun getDisplayState(): Note?

        fun setId(id: String)

        fun getId() : String?
    }

    interface Logic {
        fun noteDetailEvent(event: NoteDetailEvent)
        fun bind()
        fun clear()
    }
}

sealed class NoteDetailEvent {
    object OnDoneClick : NoteDetailEvent()
    object OnDeleteClick : NoteDetailEvent()
    object OnDeleteConfirmed : NoteDetailEvent()
    object OnBackClick : NoteDetailEvent()
    object OnStart : NoteDetailEvent()
}

