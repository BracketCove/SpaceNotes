package com.wiseassblog.spacenotes.login

import com.wiseassblog.spacenotes.notedetail.NoteDetailEvent

interface ILoginContract {

    interface View {
        fun showSignedIn()
        fun showSignedOut()
        fun showLoading()
    }

    interface Logic {
        fun event(event: LoginEvent)
    }

}

sealed class LoginEvent {
    object OnLoginButtonClick : LoginEvent()
    object OnBackClick : LoginEvent()
    object OnStart : LoginEvent()
    object OnDestroy : LoginEvent()
}