package com.wiseassblog.spacenotes.notedetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.spacenotes.notedetail.INoteDetailContract

class NoteDetailViewModel(private var displayState: MutableLiveData<Note> = MutableLiveData(),
                          private var id: MutableLiveData<String> = MutableLiveData()) : ViewModel(),
        INoteDetailContract.ViewModel {
    override fun setId(id: String) {
        this.id.value = id
    }

    override fun getId(): String? {
        return this.id.value
    }

    override fun getDisplayState(): Note? {
        return displayState.value
    }

    override fun setDisplayState(note: Note) {
        displayState.value = note
    }
}

