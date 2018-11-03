package com.wiseassblog.spacenotes.notelist


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.domain.domainmodel.User

/**
 * isPrivateMode refers to whether the User wants to post to and read from a shared repo, or they
 * would like to store their note in private storage.
 */
class NoteListViewModel(private var adapterData: MutableLiveData<List<Note>> = MutableLiveData(),
                        private var user: MutableLiveData<User?> = MutableLiveData(),
                        private var isPrivateMode: MutableLiveData<Boolean> = MutableLiveData()) : ViewModel(),
        INoteListContract.ViewModel {

    init {
        isPrivateMode.value = true
    }

    override fun getIsPrivateMode(): Boolean {
        return isPrivateMode.value!!
    }

    override fun setIsPrivateMode(isPrivateMode: Boolean) {
        this.isPrivateMode.value = isPrivateMode
    }

    override fun setAdapterState(result: List<Note>) {
        adapterData.value = result
    }

    override fun setUserState(userResult: User?) {
        user.value = userResult
    }

    override fun getUserState(): User? {
        return user.value
    }

    override fun getAdapterState(): List<Note> {
        //return current display state or empty string if value is null
        //see "Elvis Operator"
        return adapterData.value ?: emptyList()
    }
}