package com.wiseassblog.spacenotes.notedetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.spacenotes.notedetail.INoteDetailContract

class NoteDetailViewModel(private var displayState: MutableLiveData<Note> = MutableLiveData(),
                          private var id: MutableLiveData<String> = MutableLiveData(),
                          private var isPrivateMode: MutableLiveData<Boolean> = MutableLiveData()) : ViewModel(),
        INoteDetailContract.ViewModel {

    override fun getIsPrivateMode(): Boolean {
        return isPrivateMode.value!!
    }

    override fun setIsPrivateMode(isPrivateMode: Boolean) {
        this.isPrivateMode.value = isPrivateMode
    }

    override fun setId(id: String) {
        this.id.value = id
    }

    override fun getId(): String? {
        return this.id.value
    }

    override fun getNoteState(): Note? {
        return displayState.value
    }

    override fun setNoteState(note: Note) {
        displayState.value = note
    }
}

